#!/usr/bin/env bash

for file in ./samples/*.ppm
do
    convert $file ${file/.ppm/.png}
done
