#!/usr/bin/env python
# Print 'lat,long' of the given checkpoint

import sys
import json
import urllib2

if len(sys.argv) < 3:
  print "[HELP] 2 params needed:\n\n\t- runID\n\tcheckpointID\n"
  sys.exit(1)

URL_CP="http://localhost:8000/runs/" + sys.argv[1] + "/checkpoints/" + sys.argv[2]
URL_CP_TIME="http://localhost:8000/runs/" + sys.argv[1] + "/checkpoints/" + sys.argv[2] + "/gettime"
jsonData = urllib2.urlopen(URL_CP)

data = json.load(jsonData)
latLon = data['latLon']
print "\nCheckpoint LAT,LNG: " + latLon['lat'] + "," + latLon['lon']
print

jsonData = urllib2.urlopen(URL_CP_TIME)
data = json.load(jsonData)
print "\nCheckpoint time: " + data['time']
