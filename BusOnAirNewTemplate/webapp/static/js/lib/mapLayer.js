(function (w,L) {

var baseURL = 'http://localhost:7474/plugin';

// create a map in the "map" div, set the view to a given place and zoom
map = L.map("map", {zoomControl: false});
mappa = map;
// create the tile layer with correct attribution
var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
var osmAttrib='Map data © OpenStreetMap contributors';
var osm = new L.TileLayer(osmUrl, {minZoom: 12, maxZoom: 20, attribution: osmAttrib});	

// Extend the Default marker class - Red icon
var RedIcon = L.Icon.Default.extend({
	options: {
		iconUrl: 'img/redMarker.png',
		iconSize:     [41, 41], // size of the icon
	}
});
var redIcon = new RedIcon();

// Extend the Default marker class - Bus stop icon
var BusStop = L.Icon.Default.extend({
	options: {
		iconUrl: 'img/busStop.png',
		iconSize:     [41, 46], // size of the icon
	}
});
var busStop = new BusStop();
busSop = busStop;

// Extend the Default marker class - Start icon
var startIcon = L.Icon.Default.extend({
	options: {
		iconUrl: 'img/startIcon.png',
		iconSize:     [41, 44], // size of the icon
	}
});
var startIcon = new startIcon();

// Extend the Default marker class - End icon
var endIcon = L.Icon.Default.extend({
	options: {
		iconUrl: 'img/endIcon.png',
		iconSize:     [41, 44], // size of the icon
	}
});
var endIcon = new endIcon();

// Extend the Default marker class - Bus icon
BusIcon = L.Icon.Default.extend({
	options: {
		iconUrl: 'img/busIcon.png',
		iconSize:     [30, 28], // size of the icon
	}
});





// start the map in l'aquila
map.locate({setView: true, minZoom: 14, maxZoom: 16});


// creare un marker


function onLocationFound(e) {
    var radius = 150;
    var markers = [];
    layer=undefined;
    L.marker(e.latlng,{icon:redIcon,draggable:true})
		.addTo(map)
		.bindPopup("You are here").openPopup()
		.on('dragend', function(event) {
		    if(typeof layer !== 'undefined') map.removeLayer(layer);
		    var marker = event.target;  // you could also simply access the marker through the closure
		    var result = marker.getLatLng();  // but using the passed event is cleaner
		    $.getJSON( baseURL + "/stations/getneareststations?lat=" + result.lat + "&lon=" + result.lng + "&range=500", function( data ) {
				$.each( data.stationlist, function( key, val ) {
					$.getJSON( baseURL + val, function( datata ) {
							markers [key] = L.marker([datata.latLon.lat, datata.latLon.lon],{
								icon:busStop, title : datata.name, alt : datata.url });
					});
				});
				setTimeout(function(){
					layer = L.layerGroup(markers).addTo(map);
				},300)
			});
		});

}
map.on('locationfound', onLocationFound);


//show start and end 
layerTrip=undefined;
layerTripUno=undefined;
layerTripDue=undefined;
markersTrip = [];
draw = function(lat1,lon1,lat2,lon2) {
	if(typeof stopLayer !== 'undefined') map.removeLayer(stopLayer);
	if(typeof line !== 'undefined') map.removeLayer(line);

    if(typeof layerTripUno !== 'undefined') map.removeLayer(layerTripUno);
	if(typeof layerTripDue !== 'undefined') map.removeLayer(layerTripDue);

    if (!lat1) return;
    markersTripUno = []
	markersTripUno[0] = L.marker([lat1, lon1],{icon:startIcon,draggable:true});
				
			markersTripUno[0].on('dragend', function(event) {
			    var marker = event.target;  // you could also simply access the marker through the closure
			    start = marker.getLatLng();  // but using the passed event is cleaner

				var URL = 'http://nominatim.openstreetmap.org/reverse?';
			  	var format = 'json';
	   			$.getJSON(URL + "&format=" + format + "&lat=" + start.lat + "&lon=" + start.lng, function( reverse ){
	   				nominatim1.value = reverse.address.road;
	   			});
	   			setTimeout(searchgo,1000);
			});

	layerTripUno = L.layerGroup(markersTripUno).addTo(map);

	markersTripDue = []
	markersTripDue[0] = L.marker([lat2, lon2],{icon:endIcon,draggable:true});

			markersTripDue[0].on('dragend', function(event) {
				var marker = event.target;  // you could also simply access the marker through the closure
				end = marker.getLatLng();  // but using the passed event is cleaner

				var URL = 'http://nominatim.openstreetmap.org/reverse?';
				var format = 'json';
		   		$.getJSON(URL + "&format=" + format + "&lat=" + end.lat + "&lon=" + end.lng, function( reverse ){
			   		nominatim2.value = reverse.address.road;
			   	});
			   	setTimeout(searchgo,1000);
			});

	layerTripDue = L.layerGroup(markersTripDue).addTo(map);
	};

// create the bus route
stopLayer=undefined;
line=undefined;
busMarker=undefined;
showRoute = function(data, lineName) {
  console.log("showRoute " + lineName);
  console.log(JSON.stringify(data));
	if(typeof layerTripUno !== 'undefined') map.removeLayer(layerTripUno);
	if(typeof layerTripDue !== 'undefined') map.removeLayer(layerTripDue);
	if(typeof stopLayer !== 'undefined') map.removeLayer(stopLayer);
	if(typeof line !== 'undefined') map.removeLayer(line);
	for (i in polis) {
		map.removeLayer(polis[i]);
	}
	if(typeof markeris !== 'undefined') mappa.removeLayer(markeris);
	polis = [];

	var markers = [];	
	var marker = data.routelist;
	var linePts = [];

	for (var it in marker) {
		linePts[it] = [data.routelist[it].latLon.lat,data.routelist[it].latLon.lon];
	}

	for( i=0; i < linePts.length; i=i+1 ) {
	    // turn this coordinate into a LatLng
	 	linePts[i] = new L.LatLng( linePts[ i ][ 0 ], linePts[ i ][ 1 ] );
	 	if (i == 0) {
	 		markers [i] = L.marker([linePts[ i ].lat, linePts[ i ].lng],{
	 			icon:endIcon});	
	 	} else if (i == linePts.length-1) {
	 		markers [i] = L.marker([linePts[ i ].lat, linePts[ i ].lng],{
	 			icon:startIcon});
 		} else {
 			markers [i] = L.marker([linePts[ i ].lat, linePts[ i ].lng],{
	 			icon:busStop});
 		}
	}

	// add the line
	line = new L.Polyline( linePts, { 
						color: 'rgb(31, 177, 154)',
	               		weight: 5,
	                	opacity: .7
									} );
	map.addLayer(line);
	stopLayer = L.layerGroup(markers).addTo(map);
	// zoom the map to the polyline
	map.fitBounds(line.getBounds());
  //RealTime.line = line;
}

polis = [];
function processLine(json) {
	if(typeof stopLayer !== 'undefined') map.removeLayer(stopLayer);
	if(typeof line !== 'undefined') map.removeLayer(line);

	var walks = json.directionslist[0].walks;
	var routes = json.directionslist[0].routes;
	var poli = undefined;
	for (i = 0; i < walks.length; i++) {
		var linePts = [];
		linePts[0] = new L.LatLng( walks[ i ].latLon[ 0 ].lat, walks[ i ].latLon[ 0 ].lon );
		linePts[1] = new L.LatLng( walks[ i ].latLon[ 1 ].lat, walks[ i ].latLon[ 1 ].lon );

		poli = new L.Polyline( linePts, { color: 'rgb(31, 177, 154)', weight: 5, opacity: .7 } );
		polis[polis.length] = poli;

		


	}

}
	

map.addLayer(osm);

// add control zoom to map
map.addControl( L.control.zoom({position: 'topright'}) );

// create the logo BusOnAir and add it on the right
var logo = L.control({position: 'bottomright'});

logo.onAdd = function (map) {
	var div = L.DomUtil.create('div', 'info legend');
    div.innerHTML +=
    '<img src="img/BusOnAir.png" alt="legend" width="200" height="54">';
	return div;
	};
logo.addTo(map);


})(window, window.L);



