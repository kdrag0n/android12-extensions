#!/usr/bin/env bash

cd "$(dirname "$0")"

pushd monet
./generate_overlays.py
popd

sysui_flags/build.sh

rm -fr ../system/product/overlay/*
mv monet/out/dist/* ../system/product/overlay/
mkdir ../system/product/overlay/SystemUIFlags
mv sysui_flags/dist/SystemUIFlags.apk ../system/product/overlay/SystemUIFlags/
