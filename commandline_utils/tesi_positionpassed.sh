#!/bin/bash


if [ $# -lt 4 ]; then
  echo "[HELP] 4 params needed:"
  echo
  echo " - runID"
  echo " - lat"
  echo " - lon"
  echo " - time"
  exit;
fi

URL="http://localhost:8000/runs/$1/rt/updateposition?lat=$2&lon=$3&time=$4
curl $URL
echo


