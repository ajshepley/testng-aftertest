#!/bin/bash

echo "Deploying!"

echo "Sleeping 5m..."

curl https://httpstat.us/200?sleep=5001

echo "Done sleeping!"

exit 0

