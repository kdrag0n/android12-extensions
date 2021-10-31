#!/system/bin/sh

# Stable Android 12 or Pixel 6 branch
build_id="$(getprop ro.build.id)"
if [[ "$build_id" != "SP1A."* ]] && [[ "$build_id" != "SD1A."* ]]; then
    ui_print "This module is ONLY for Android 12."

    # Abort install and clean up
    rm -fr $TMPDIR $MODPATH
    exit 1
fi

chmod 755 $MODPATH/service.sh $MODPATH/service.payload.sh
