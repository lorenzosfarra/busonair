/**
 * Utils for real time actions.
 */

var RealTime = {
	// Used when we are checking a running bus!
	timeout: null,
	// reference to the map and related stuff
	Demo: null,
	
	// Marker stuff
	busImage: new google.maps.MarkerImage('images/bus_popup.png',
	new google.maps.Size(30, 28),
	new google.maps.Point(0,0),
	new google.maps.Point(0, 28)),

	shadow: new google.maps.MarkerImage('images/xferdisk.png',
		new google.maps.Size(9, 9),
		new google.maps.Point(0,0),
		new google.maps.Point(4, 4)),

	busMarker: null,
	
	// Some variables to hold specific values (autoexplained..)
	curRunId: 0,
	curRouteId: 0,
	firstCheckPoint: null,
	
	timeoutTime: 2000,

  followRunElem: null,
  followRunRoute: 0,
  followRunLastStop: 0,
	
	init: function(demo) {
		RealTime.Demo = demo;
		RealTime.busMarker = new google.maps.Marker({
				map: RealTime.Demo.map,
				shadow: RealTime.shadow,
				icon: RealTime.busImage,
				visible: false,
			title: 'Drag to Change'
			});
		RealTime.Demo.markers.push(RealTime.busMarker);
		$('.followbus').live('click', function(e) {
			e.preventDefault();
			RealTime.followBus();
      $(this).fadeOut('slow');
		});
	},

	/**
 	 * show an actions table for the clicked route.
	 * @param elem the element representing the route in the table
	 * @param routeId the route id
	 */
	showActionTable: function(elem, routeId) {
		console.log("Show action table!");
    RealTime.followRunElem = elem;
    RealTime.followRunRoute = routeId;
		// Remove all the other <tr> actions bar
		$(".firstavailablerun").remove();
		$.getJSON(document.URL + "routes/" + routeId + "/getfirstavailablerun", function(data) {
			console.log(data);
			RealTime.showActionTableAux(elem, data);
		});
	},
	showActionTableAux: function(elem, data) {
		console.log("First checkpoint time = " + data.firstCheckPointTime);
		RealTime.curRouteId = data.route;
		RealTime.curRunId = data.id;
		RealTime.firstCheckPoint = data.firstCheckPoint;
		var nextRunTimeAux = RealTime.secondsToTime(data.firstCheckPointTime);
		var minutes = nextRunTimeAux.minutes;	
		var nextRunTime = nextRunTimeAux.hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
		var html;
		if ((typeof data.status != 'undefined')	&& (data.status != 200))
			html = '<td>-</td><td><strong>No more runs today.</td>';
		else
			html = '<td><button class="followbus">Follow</button></td><td><strong>Next run at:</strong> ' + nextRunTime + '</td>';
		var tr = $("<tr>", { html: html, className: "firstavailablerun" });
		$(elem).after(tr);
    // Now, add another row for showing delays / additional info
    html = '<td id="busdelayinfo" colspan="2"></td>';
		var tr1 = $("<tr>", { className: "firstavailablerun", html: html });
    $(tr).after(tr1);
	},

	/**
     	* Convert number of seconds into time object
     	*
     	* @param integer secs Number of seconds to convert
     	* @return object
     	*/
    	secondsToTime: function(secs) {
          var days = Math.floor(secs / 86400);
          var hours = Math.floor((secs % 86400) / 3600);
          var minutes = Math.floor(((secs % 86400) % 3600) / 60);
          var seconds = ((secs % 86400) % 3600) % 60;


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
			console.log(data);
			RealTime.followBusAux(data);
		});
		RealTime.startFollow();
	},
	followBusAux: function(data) {
		busMarker.setVisible(true);
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
		console.log("startfollowaux: " + JSON.stringify(data));
		var dataPosition = data.latLon;
		var position = new google.maps.LatLng(dataPosition.lat, dataPosition.lon);
		moveBusMarker(position);
	},
  /**
   * Check if there is a delay for the monitored bus.
   */
  checkDelay: function(data) {
    console.log(JSON.stringify(data));
    var html = '<p class="laststation">' + data.stationName + "</p>";
    var delay = parseInt(data.delay);
    if (delay > 0) {
               html += '<p><span class="laststationdelay">Delay:</span> ' + data.delay + " minutes</p>";
    }
    $("#busdelayinfo").fadeOut('slow', function() { $(this).html(html).fadeIn('slow'); });
    if ((data.nextInRun == "") && (RealTime.followRunLastStop != 0) && (RealTime.followRunLastStop != data.id)) {
      // Last Stop!
      clearTimeout(RealTime.timeout);
      $("#busdelayinfo").fadeOut('slow', function() { $(this).html("Run is over."); });
      RealTime.showActionTable(RealTime.followRunElem, RealTime.followRunRoute);
      RealTime.followRunLastStop = 0;
      $(".followbus").fadeIn('fast');
    } else {
      RealTime.followRunLastStop = data.id;
      $("#busdelayinfo").fadeOut('slow', function() { $(this).html(html).fadeIn('slow'); });
    }
  },
};
