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

# `@Serializable` on the line before a route declaration. -A1 pairs them up; the sed
# pulls the declared name out of `data class Foo(` / `data object Foo`, whatever
# visibility it carries — the pairing routes are `private`, and dropping them from the
# check because of a modifier would silently narrow it to the routes that need it least.
routes=$(
    find feature -type d -name navigation -path '*/src/main/*' -exec \
        grep -rh -A1 '^@Serializable' --include='*.kt' {} + |
        sed -nE 's/^(public |internal |private )?data (class|object) ([A-Za-z0-9_]+).*/\3/p' |
        sort -u
)

if [ -z "$routes" ]; then
    echo "ERROR: found no @Serializable routes in the sources. The extraction above" >&2
    echo "       has drifted from the code — fix it rather than deleting this check." >&2
    exit 1
fi

missing=0
found=0
while IFS= read -r route; do
    # A route survives as itself; a data class also carries a generated $$serializer.
    # Either form proves the route was not shrunk away. Obfuscated names are fine —
    # only the left-hand (original) side of the mapping is inspected.
    if grep -qE "^[A-Za-z0-9_.]*\.navigation\.${route}( |\$)" "$MAPPING" ||
        grep -qE "^[A-Za-z0-9_.]*\.navigation\.${route}\\\$\\\$serializer " "$MAPPING"; then
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