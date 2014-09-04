#!/bin/bash
#generate_codes_on_mac.sh 500 dangerous_codes.txt theSponsor
#count, file_name, sponsor
for i in $(seq 0 $1)
do
   md5value=$(md5 <<< "$i $RANDOM")
   md5value="${md5value//-/}"
   md5value=$(echo $md5value | sed "s/'//")
   echo $md5value >> ./"$2"
   ./push_code.sh "${md5value// -/}" $3
done
