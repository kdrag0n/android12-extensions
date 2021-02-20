#!/system/bin/sh

MODPATH=/data/adb/modules/android12-extensions

# Layer of indirection fixes SELinux issues
exec su -c $MODPATH/service.sh > /dev/kmsg 2>&1
