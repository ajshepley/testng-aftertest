#!/bin/bash

echo "Deploying!"

echo "Sleeping 5m..."

date

curl https://httpstat.us/200?sleep=90000 --max-time 145

echo "Done sleeping!"

echo "Finishing!"

date

exit 0

