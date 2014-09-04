#!/bin/sh
curl -X POST \
  -H "X-Parse-Application-Id: INSERT_HERE" \
  -H "X-Parse-Master-Key: INSERT_HERE" \
  -H "Content-Type: application/json" \
  -d '{"code":"'"$1"'", "sponsor":"'"$2"'"}' \
  https://api.parse.com/1/classes/VerificationCode
