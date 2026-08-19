#!/usr/bin/env bash
#
# Asserts that every @Serializable navigation route survived R8.
#
# Navigation routes are the one part of this app whose correctness depends on a keep
# rule rather than on reachability: `toRoute<T>()` resolves them through
# kotlinx.serialization, so a stripped serializer is invisible in every debug build
# and every unit test, and shows up only as a crash on a release install. The keeps
# that protect them are not written here at all — they ship inside
# kotlinx-serialization-core — which is exactly why a change somewhere else (a library
# upgrade, a rule edit, an R8 mode flip) could remove them without anyone editing a
# file that mentions routes.
#
# Reads the route names out of the sources rather than hardcoding a list, so adding a
# route extends the check for free.
#
# Usage: scripts/verify-release-mapping.sh [path/to/mapping.txt]

set -euo pipefail

MAPPING="${1:-app/build/outputs/mapping/release/mapping.txt}"

if [ ! -f "$MAPPING" ]; then
    echo "ERROR: no mapping at $MAPPING — run ./gradlew :app:assembleRelease first." >&2
    exit 1
fi

# Any main-source file in a `navigation` package, wherever the module lives. Anchoring
# this to a `feature/` directory — as it first did — made the check fail outright on a
# tree that simply does not have one yet.
mapfile -t navigation_files < <(
    find . -type f -name '*.kt' -path '*/src/main/*' -path '*/navigation/*' \
        -not -path '*/build/*' | sort
)

# Each route as `package.Name`: the package comes from the file the declaration sits in
# rather than from an assumed package layout. `@Serializable` sits on the line before the
# declaration, so -A1 pairs them up; the sed takes the name out of `data class Foo(` or
# `data object Foo` whatever visibility it carries — routes are often `private`, and
# dropping them over a modifier would narrow the check to the routes needing it least.
routes=$(
    for file in "${navigation_files[@]}"; do
        package=$(sed -nE 's/^package[[:space:]]+([A-Za-z0-9_.]+).*/\1/p' "$file" | head -n 1)
        [ -n "$package" ] || continue
        grep -A1 '^@Serializable' "$file" |
            sed -nE "s/^(public |internal |private )?data (class|object) ([A-Za-z0-9_]+).*/${package}.\3/p"
    done | sort -u
)

# A template with no feature modules has no routes to lose, and failing here would only
# teach everyone to delete the check before it ever protects anything. The trade-off is
# real though: while this branch is taken, a broken extraction and an empty repository
# look identical, so the check starts guarding nothing until the first route lands.
if [ -z "$routes" ]; then
    echo "No @Serializable routes in navigation packages — nothing to verify yet."
    exit 0
fi

missing=0
found=0
while IFS= read -r route; do
    # A route survives as itself; a data class also carries a generated $$serializer.
    # Either form proves the route was not shrunk away. Obfuscated names are fine —
    # only the left-hand (original) side of the mapping is inspected.
    escaped=${route//./\\.}
    if grep -qE "^${escaped}( |\$)" "$MAPPING" ||
        grep -qE "^${escaped}\\\$\\\$serializer " "$MAPPING"; then
        found=$((found + 1))
    else
        echo "MISSING from the release mapping: $route" >&2
        missing=$((missing + 1))
    fi
done <<<"$routes"

if [ "$missing" -ne 0 ]; then
    echo >&2
    echo "$missing navigation route(s) did not survive R8. Release navigation would" >&2
    echo "crash at runtime while every debug build stays green. Check the keep rules" >&2
    echo "that kotlinx-serialization-core contributes before adding one by hand." >&2
    exit 1
fi

echo "All $found navigation routes survived R8."
