#!/usr/bin/env sh

for file in ./samples/*.ppm
do
    convert $file ${file/.ppm/.png}
done
