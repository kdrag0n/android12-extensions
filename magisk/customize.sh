#!/system/bin/sh

if [[ "$(getprop ro.build.id)" != "SP1A."* ]]; then
    ui_print "This module is ONLY for Android 12 v1."

    # Abort install and clean up
    rm -fr $TMPDIR $MODPATH
    exit 1
fi

chmod 755 $MODPATH/service.sh $MODPATH/service.payload.sh
