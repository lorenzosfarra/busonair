#!/bin/bash


if [ $# -lt 1 ]; then
  echo "[HELP] 1 param needed:"
  echo
  echo " - runID"
  exit;
fi

URL_CP="http://localhost:8000/runs/$1/rt/getlastgpscheckpoint"
URL_STOP="http://localhost:8000/runs/$1/rt/getlastgpsstop"
URL_GPS="http://localhost:8000/runs/$1/rt/getlastgpsposition"

echo

echo -e "LAST STOP\n"
curl $URL_STOP

echo -e "\n\nLAST CHECKPOINT\n"
curl $URL_CP

echo -e "\n\nLAST GPS POSITION\n"
curl $URL_GPS

echo -e "\n\n"
