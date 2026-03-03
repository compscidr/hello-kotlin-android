#!/bin/bash
set -e

DEVICES=$(adb devices | tail -n +2 | awk 'NF {print $1}')

if [ -z "$DEVICES" ]; then
    echo "No devices connected, skipping package cleanup."
    exit 0
fi

for DEVICE in $DEVICES; do
    timeout 20 adb -s "$DEVICE" wait-for-device
    for i in $(adb -s "$DEVICE" shell pm list packages -f | grep com.example.myapplication | sed -n 's/^.*\/base.apk=//p'); do
        adb -s "$DEVICE" uninstall --user 0 "$i"
    done
done