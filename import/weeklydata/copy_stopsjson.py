#!/usr/bin/env python

import json
import sys

class StopsJson:
  filename = None
  startingStopId = 0
  dayNumber = 0
  stops = None

  def __init__(self, filename="stops_day0.json", dayNumber=1):
    self.filename = filename
    self.dayNumber = dayNumber

  def load_stops(self):
    # Load stops info
    json_data = open(self.filename)
    data = json.load(json_data)
    self.stops = stops = data['stopsObjectsList']
    print "Stops: " + str(len(stops))
    self.startingStopId = len(stops) * self.dayNumber
    return self.stops
