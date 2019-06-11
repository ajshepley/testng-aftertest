#!/bin/bash

echo "Deploying!"

echo "Sleeping 5m..."

date

curl https://httpstat.us/200?sleep=300000 --max-time 300

echo "Done sleeping!"

date

exit 0

