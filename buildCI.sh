#!/bin/bash

echo "Begine build."
# Exit if error
set -e

# Move agconnect-services.json in apps.
cp MLKit-Sample/module-text/sample-agconnect-services.json MLKit-Sample/module-text/agconnect-services.json
cp MLKit-Sample/module-vision/sample-agconnect-services.json MLKit-Sample/module-vision/agconnect-services.json
cp ID-Photo-DIY/app/sample-agconnect-services.json ID-Photo-DIY/app/agconnect-services.json
cp Smile-Camera/app/sample-agconnect-services.json Smile-Camera/app/agconnect-services.json
cp Photo-Translate/app/sample-agconnect-services.json Photo-Translate/app/agconnect-services.json
echo "Copy agc file end."

# Build
./gradlew build
