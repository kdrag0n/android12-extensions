#!/system/bin/sh

beta_ver="$(getprop ro.build.id | cut -d. -f1 | sed 's/SPB//')"
if ! getprop ro.build.id | grep -q SPB || [[ "$beta_ver" -ne 5 ]]; then
    ui_print "This module is ONLY for Android 12 Beta 5."

    # Abort install and clean up
    rm -fr $TMPDIR $MODPATH
    exit 1
fi

chmod 755 $MODPATH/service.sh $MODPATH/service.payload.sh
