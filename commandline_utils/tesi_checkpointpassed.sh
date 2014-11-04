#!/bin/bash


if [ $# -lt 3 ]; then
  echo "[HELP] 3 params needed:"
  echo
  echo " - runID"
  echo " - checkpointID"
  echo " - time"
  exit;
fi

URL="http://localhost:8000/runs/$1/rt/checkpointpass?checkpointid=$2&time=$3"
URL_CP="http://localhost:8000/runs/$1/checkpoints/$2"

curl $URL
echo -e "\n\nCheckpoint INFO:\n"
curl $URL_CP
echo -e "\n"


