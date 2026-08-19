# No consumer rules needed yet.
#
# This file must exist even when empty: AndroidLibraryPlugin declares
# consumerProguardFiles("consumer-rules.pro") for every library module, and the build
# fails outright if it is missing. Rules a library needs its *consumers* to apply belong
# here — not in a proguard-rules.pro, which a library never resolves because R8 runs once
# over the whole app rather than per module.
#
# Before adding one, check whether the dependency already ships its own: anything with
# rules under META-INF/com.android.tools/ is applied automatically, and a hand-written
# copy is usually both broader and redundant.
