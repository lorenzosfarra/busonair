/**
 * Utils for real time actions.
 */

var RealTime = {
	// Used when we are checking a running bus!
	timeout: null,

	busMarker: null,

  map: null,

  // Polyline
  line: null,
	
	// Some variables to hold specific values (autoexplained..)
	curRunId: 0,
	curRouteId: 0,
	firstCheckPoint: null,
  currentBusPosition: null,
  animatedLayer: null,
	
	timeoutTime: 5000,
	
	init: function(map) {
		RealTime.map = map;
	},

	/**
 	 * show info and action for the clicked route.
	 * @param routeId the route ID
   * @param lineName the bus line

	 */
	showLineActions: function(routeId, lineName) {
    $(".separator").removeClass("hidden").hide();
    $(".firstavailablerun").removeClass("hidden").hide();
    $(".currentpositionname").removeClass("hidden").hide();
    $(".delay").removeClass("hidden").hide();
    $(".linename").removeClass("hidden").hide();
		$.getJSON(document.URL + "routes/" + routeId + "/getfirstavailablerun", function(data) {
			console.log(data);
			RealTime.showLineActionsAux(data);
		});
    // Change line name
    $(".linename").fadeOut("slow",
          function() {
            console.log("linename " + lineName);
            $(this).text(lineName).fadeIn("slow");
            $("#separatorone").fadeIn("slow"); 
            $(".followbus").fadeIn("slow");
          });
	},
	showLineActionsAux: function(data) {
		RealTime.curRouteId = data.route;
		RealTime.curRunId = data.id;
		RealTime.firstCheckPoint = data.firstCheckPoint;
		RealTime.currentBusPosition = data.firstCheckPoint;
		var html;
		if ((typeof data.status != 'undefined')	&& (data.status != 200)) {
			html = '<strong>No more runs today.</strong>';
		} else {
		  var nextRunTimeAux = RealTime.secondsToTime(data.firstCheckPointTime);
		  var minutes = nextRunTimeAux.minutes;	
		  var nextRunTime = nextRunTimeAux.hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
			html = nextRunTime;
    }

    $(".nextrun").html(html);
    $(".firstavailablerun").fadeIn("slow");
    $("#separatortwo").fadeIn("slow");
	},
	
	/**
     	* Convert number of seconds into time object
     	*
     	* @param integer secs Number of seconds to convert
     	* @return object
     	*/
    	secondsToTime: function(secs) {
	        var hours = Math.floor(secs / (60 * 60));
       
        	var divisor_for_minutes = secs % (60 * 60);
        	var minutes = Math.floor(divisor_for_minutes / 60);
     
   	     	var divisor_for_seconds = divisor_for_minutes % 60;
        	var seconds = Math.ceil(divisor_for_seconds);
       
      	  	var obj = {
            		"hours": hours,
            		"minutes": minutes,
            		"seconds": seconds
       		};
        	return obj;
    	},

	
	/**
	 * Follow the currently selected bus.
	 */
	followBus: function() {
		console.log("Following run " + RealTime.curRunId + ", route " + RealTime.curRouteId);
		// Clear previously-setted timers
		clearInterval(RealTime.timeout);
		$.getJSON(document.URL + "runs/" + RealTime.curRunId + "/checkpoints/" + RealTime.firstCheckPoint, function(data) {
			//console.log(data);
			RealTime.followBusAux(data);
		});
		RealTime.startFollow();
	},
	followBusAux: function(data) {
		//busMarker.setVisible(true);
		var dataPosition = data.latLon;
    //var marker = L.marker([dataPosition.lat, dataPosition.lon], { icon:busIcon }).addTo(RealTime.map);
	},
	/**
	 * Real-time updates on the map for the currently selected bus
	 * TODO: there HAS TO BE a check for the moment the timer should start,
	 * the moment of the bus departure
	*/
	startFollow: function() {
		RealTime.timeout = setInterval(function() {
      // Get last GPS position to update the map!
			$.getJSON(document.URL + "runs/" + RealTime.curRunId + "/rt/getlastgpsposition", function(data) {
				RealTime.startFollowAux(data);
			});
      // Get last stop and time to check for delays
			$.getJSON(document.URL + "runs/" + RealTime.curRunId + "/rt/getlastgpsstop", { "delay": true }, function(data) {
				RealTime.checkDelay(data);
			});
		}, RealTime.timeoutTime);
	},
	startFollowAux: function(data) {
		var dataPosition = data.latLon;
		RealTime.moveBus(parseFloat(dataPosition.lat), parseFloat(dataPosition.lon));
	},
  moveBus: function(lat, lon) {
    console.log("lat, lon: " + lat + ", " + lon);
    if (RealTime.line == null) {
      RealTime.line = new Array();
      RealTime.line.push([lat, lon]);
      return;
    }
    var lineLength = RealTime.line.length;
    var currentPositionBus = RealTime.line[lineLength -1];
    console.log("currentPositionBus: " + JSON.stringify(currentPositionBus));
    if ((currentPositionBus[0] != lat) && (currentPositionBus[1] != lon)) {
      RealTime.line.push([lat, lon]);
      lineLength += 1;
      if (lineLength > 2) {
        // Remove the first element of the array
        RealTime.line.shift();
      } else if (lineLength < 2) {
        return;
      }
      // Move the marker
      if (RealTime.animatedLayer != null) {
        console.log("qui1");
        RealTime.map.removeLayer(RealTime.animatedLayer);
        console.log("qui2");
      }
      console.log("Creating a polyline with line: " + JSON.stringify(RealTime.line));
      var lineAux = RealTime.line.slice(0);
      var polyline = L.polyline(lineAux);
      RealTime.polyline = polyline;
      console.log("polyline.getLatLngs() = " + polyline.getLatLngs());
      var busIcon = new BusIcon();
      RealTime.animatedMarker = L.animatedMarker(polyline.getLatLngs(), { icon: busIcon, autostart: true });
      RealTime.map.addLayer(RealTime.animatedMarker);
      console.log("qui4");
    }

  },
  /**
   * Check if there is a delay for the monitored bus.
   */
  checkDelay: function(data) {
    //console.log(JSON.stringify(data));
    $(".currentpositionname").removeClass("hidden").hide().text(data.stationName).fadeIn("slow");
    $("#separatortwo").removeClass("hidden").fadeIn("slow");
    var delay = parseInt(data.delay);
    if (delay > 0) {
               $('.delayvalue').text(data.delay);
               $('.delay').removeClass("hidden").fadeIn("slow");
              $("#separatorthree").removeClass("hidden").fadeIn("slow");
    }
  },
};
