#!/system/bin/sh

# Stable Android 12
if [[ "$(getprop ro.build.version.release)" != "12" ]]; then
    ui_print "This module is ONLY for Android 12."

    # Abort install and clean up
    rm -fr $TMPDIR $MODPATH
    exit 1
fi

chmod 755 $MODPATH/service.sh $MODPATH/service.payload.sh
