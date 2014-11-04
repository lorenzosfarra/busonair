#!/bin/bash

x=1

while [ $x -lt 7 ];
do 
  ./copy_runs.py  $x
  ./copy_stops.py $x; 
  x=$(($x + 1));
done
