#!/usr/bin/env bash

mkdir $1
cd $1
nvim 000.log
grep 'Cam16(' 000.log > cam16.log
grep 'Cam16Ucs(' 000.log > cam16-ucs.log
grep 'CieLch(' 000.log > cielab.log
grep 'Jzczhz(' 000.log > jzazbz.log
grep 'Oklch(' 000.log > oklab.log
grep 'Srlch2(' 000.log > srlab2.log
rm -f 000.log
