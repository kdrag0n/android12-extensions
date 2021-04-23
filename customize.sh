#!/system/bin/sh

dp_ver="$(getprop ro.build.id | cut -d. -f1 | sed 's/SPP//')"
if ! getprop ro.build.id | grep -q SPP || [[ "$dp_ver" -lt 3 ]]; then
    ui_print "This module is ONLY for Android 12 Developer Preview 3."
    exit 1
fi

chmod 755 $MODPATH/service.sh $MODPATH/service.payload.sh
