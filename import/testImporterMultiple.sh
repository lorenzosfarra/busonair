export BOA_BASE_URL=http://localhost:7474/plugin

#curl --connect-timeout 3600000 --max-time 3600000 $BOA_BASE_URL/backend/stations/exportall > stations.json
#curl --connect-timeout 3600000 --max-time 3600000 $BOA_BASE_URL/backend/routes/exportall > routes.json
#curl --connect-timeout 3600000 --max-time 3600000 $BOA_BASE_URL/backend/runs/exportall > runs.json
#curl --connect-timeout 3600000 --max-time 3600000 $BOA_BASE_URL/backend/stops/exportall > stops.json

PHASE=0
if [ $# -eq 0 ]; then
  # ALL THE STEPS TOGETHER
  PHASE=100
else
  PHASE=$1
fi
echo "PHASE: $PHASE"

DAYS=7

function runsImport {
  x=0
  echo "[RUNS]"
  while [ $x -lt $DAYS ];
  do
    echo -n "DAY " $(($x + 1)) ": "
    curl --connect-timeout 3600000 --max-time 3600000 -sH "Content-Type: application/json" -X POST -d @weeklydata/jsons/runs_day$x.json $BOA_BASE_URL/backend/runs/bulkimport | grep -Po '"status":.*?[^//]"' | grep 200
    if [ "$?" == "0" ]; then
      echo "LOADED."
      x=$(($x + 1));
    else
      echo "NOT LOADED, retrying...";
    fi
  done
}

function stopsImport {
  x=0
  echo "[STOPS]"
  while [ $x -lt $DAYS ];
  do
    echo -n "DAY " $(($x + 1)) ": "
    curl --connect-timeout 3600000 --max-time 3600000 -sH "Content-Type: application/json" -X POST -d @weeklydata/jsons/stops_day$x.json $BOA_BASE_URL/backend/stops/bulkimport | grep -Po '"status":.*?[^//]"' | grep 200 
    if [ "$?" == "0" ]; then
      echo "LOADED."; 
      x=$(($x + 1));
    else 
      echo "NOT LOADED, retrying...";
    fi
  done
}
echo ""
echo ""
echo "---"
echo "cleandb"
echo "---"
if [ $PHASE == 1 ] || [ $PHASE == 100 ]; then 
  curl --connect-timeout 3600000 --max-time 3600000 $BOA_BASE_URL/backend/cleandb
else
  echo "clean SKIPPED."
fi

echo ""
echo ""
echo "---"
echo "stations/bulkimport"
echo "---"
if [ $PHASE == 1 ] || [ $PHASE == 100 ]; then 
  curl --connect-timeout 3600000 --max-time 3600000 -sH "Content-Type: application/json" -X POST -d @stations.json $BOA_BASE_URL/backend/stations/bulkimport
else
  echo "import stations SKIPPED."
fi

echo ""
echo ""
echo "---"
echo "routes/bulkimport"
echo "---"
if [ $PHASE == 1 ] || [ $PHASE == 100 ]; then 
  curl --connect-timeout 3600000 --max-time 3600000 -sH "Content-Type: application/json" -X POST -d @routes.json $BOA_BASE_URL/backend/routes/bulkimport
else
  echo "import routes skipped."
fi

echo ""
echo ""
echo "---"
echo "runs/bulkimport"
echo "---"
if [ $PHASE == 1 ] || [ $PHASE == 100 ]; then 
  runsImport
else
  echo "first Runs import SKIPPED."
fi

echo ""
echo ""
echo "---"
echo "stops/bulkimport"
echo "---"
if [ $PHASE == 2 ] || [ $PHASE == 100 ]; then 
  stopsImport
else
  echo "first Stops import SKIPPED."
fi

#echo ""
#echo ""
#echo "---"
#echo "stations/linkallstops"
#echo "---"
#if [ $PHASE == 3 ] || [ $PHASE == 100 ]; then
#  curl --connect-timeout 3600000 --max-time 3600000 -sH "Content-Type: application/json" -X POST -d @stations.json $BOA_BASE_URL/backend/stations/linkallstops
#else
#  echo "skipping stations link stops."
#fi
#
echo ""
echo ""
echo "---"
echo "stops/bulkimport"
echo "---"
if [ $PHASE == 4 ] || [ $PHASE == 100 ]; then 
  stopsImport
else
  echo "second Stops import SKIPPED."
fi

echo ""
echo ""
echo "---"
echo "stations/linkallstops"
echo "---"
if [ $PHASE == 5 ] || [ $PHASE == 100 ]; then
  curl --connect-timeout 3600000 --max-time 3600000 -sH "Content-Type: application/json" -X POST -d @stations.json $BOA_BASE_URL/backend/stations/linkallstops
else
  echo "skipping stations link stops."
fi

echo ""
echo ""
echo "---"
echo "runs/bulkimport"
echo "---"
if [ $PHASE == 6 ] || [ $PHASE == 100 ]; then 
  runsImport
else
  echo "second Runs import SKIPPED."
fi


echo ""
echo ""
echo "---"
echo "checkpoints/bulkcreation"
echo "---"
echo ""
if [ $PHASE == 7 ] || [ $PHASE == 100 ]; then 
  curl --connect-timeout 3600000 --max-time 3600000 $BOA_BASE_URL/backend/checkpoints/bulkcreation
else
  echo "checkpoints import SKIPPED."
fi

echo
echo "DONE."
