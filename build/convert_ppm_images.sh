#!/usr/bin/env sh

for file in ./examples/img/*.ppm
do
    echo "Converting $file"
    convert $file "${file%.ppm}.png"
done
