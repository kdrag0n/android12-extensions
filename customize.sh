#!/sbin/sh

sdk="$(getprop ro.build.version.sdk)"
if ! getprop ro.build.fingerprint | grep -q SPP; then
    ui_print "This module is for Android 12 previews ONLY."
    exit 1
fi

chmod 755 $MODPATH/service.sh $MODPATH/service.payload.sh
