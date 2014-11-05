package boa.server.domain;

import org.neo4j.graphdb.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.index.lucene.ValueContext;

import boa.server.config.BoaConfig;

public class Station {
	protected static final String ID = "id";
	protected static final String TYPE = "type";
	protected static final String NAME = "name";
	protected static final String LATITUDE = "lat";
	protected static final String LONGITUDE = "lon";
	protected static final String IS_SCHOOL = "is_school";
	protected static final String IS_TERMINAL = "is_terminal";

	protected Node underlyingNode;
	protected Index<Node> stopIndex;

	public Station() {
	}

	public Station(Node node) {
		underlyingNode = node;
		stopIndex = DbConnection.getDb().index()
				.forNodes("stopIndex" + getId());
	}

	public Station(Node node, int id, String name, double latitude,
			double longitude, boolean isSchool, boolean isTerminal) {
		underlyingNode = node;
		setId(id);
		setName(name);
		setLatitude(latitude);
		setLongitude(longitude);
		setIsSchool(isSchool);
		setIsSchool(isTerminal);
		setType();
		stopIndex = DbConnection.getDb().index()
				.forNodes("stopIndex" + getId());
	}

	public Station(Node node, int id, String name, double latitude,
			double longitude) {
		this(node, id, name, latitude, longitude, false, false);
	}

	public void removeStop(Stop s) {
		stopIndex.remove(s.getUnderlyingNode());
	}

	public void setType() {
		underlyingNode.setProperty(Station.TYPE, "Station");
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	public Integer getId() {
		return (Integer) underlyingNode.getProperty(ID);
	}

	public String getName() {
		return (String) underlyingNode.getProperty(NAME);
	}

	public Double getLatitude() {
		return (Double) underlyingNode.getProperty(LATITUDE);
	}

	public Double getLongitude() {
		return (Double) underlyingNode.getProperty(LONGITUDE);
	}

	public Boolean getIsSchool() {
		return (Boolean) underlyingNode.getProperty(IS_SCHOOL);
	}

	public Boolean getIsTerminal() {
		return (Boolean) underlyingNode.getProperty(IS_TERMINAL);
	}

	public void setId(int id) {
		underlyingNode.setProperty(Station.ID, id);
	}

	public void setName(String name) {
		underlyingNode.setProperty(Station.NAME, name);
	}

	public void setLatitude(double lat) {
		// after changing lat/lon call updatePosition() to update
		// stationSpatialIndex
		underlyingNode.setProperty(Station.LATITUDE, lat);
	}

	public void setLongitude(double lng) {
		// after changing lat/lon call updatePosition() to update
		// stationSpatialIndex
		underlyingNode.setProperty(Station.LONGITUDE, lng);
	}

	public void updatePosition() {
		Stations.getStations().updateSpatialIndex(this);
	}

	public void setIsSchool(boolean isSchool) {
		underlyingNode.setProperty(Station.IS_SCHOOL, isSchool);
	}

	public void setIsTerminal(boolean isTerminal) {
		underlyingNode.setProperty(Station.IS_TERMINAL, isTerminal);
	}

	public void addStop(Stop s) {
		stopIndex.add(s.getUnderlyingNode(), "time",
				new ValueContext(s.getTime()).indexNumeric());
	}

	public void updateStopIndex(Stop s) {
		stopIndex.remove(s.getUnderlyingNode());
		addStop(s);
	}

	public Iterable<Stop> getStopsFromTime(int startTime, int endTime) {
		List<Stop> stops = new ArrayList<Stop>();
		IndexHits<Node> result = stopIndex.query(QueryContext.numericRange(
				"time", startTime, endTime).sortNumeric("time", false));
		for (Node n : result) {
			stops.add(new Stop(n));
		}
		result.close();
		return stops;
	}

	public Stop getFirstStopFromTime(int startTime) {
		int endTime = startTime + BoaConfig.SEARCH_DAYS_LIMIT_IN_MINUTES;
		QueryContext query = QueryContext.numericRange("time", startTime,
				endTime);
		query.sortNumeric("time", false);

		IndexHits<Node> hits = stopIndex.query(query);
		if ((hits == null) || (hits.size() < 1)) {
			// XXX: This should be part of the graph?
			// For now we check this in the code
			if (startTime >= 8640) {
				// Last day! Start the research with the first!
				return getFirstStopFromTime(0);

			} else {
				hits.close();
				return null;
			}
		}

		Node n = hits.next();
		hits.close();

		if (n == null)
			return null;
		else {

			Stop out = new Stop(n);
			while (out.getPrevInStation() != null
					&& out.getPrevInStation().getTime() >= startTime) {
				out = out.getPrevInStation();
			}

			return out;
		}
	}

	public Iterable<Stop> getStopsFromTime(int startTime) {
		int endTime = startTime + BoaConfig.SEARCH_DAYS_LIMIT_IN_MINUTES;
		return getStopsFromTime(startTime, endTime);
	}

	// public Iterable<Stop> getAllIncidentStops(){
	// ArrayList<Stop> output = new ArrayList<Stop>();
	//
	// Iterable<Relationship> rels =
	// getUnderlyingNode().getRelationships(RelTypes.STOP_STATION,
	// Direction.INCOMING);
	// for(Relationship r : rels){
	// output.add(new Stop(r.getStartNode()));
	// }
	//
	// return output;
	// }
	//
	// public Iterable<Stop> getAllStopsInIndex(){
	// ArrayList<Stop> output = new ArrayList<Stop>();
	//
	// IndexHits<Node> result = stopIndex.query("time", "*");
	// for(Node n : result){
	// output.add(new Stop(n));
	// }
	// result.close();
	//
	// return output;
	// }

	public Iterable<Stop> getAllStops() {
		ArrayList<Stop> output = new ArrayList<Stop>();
		Stop s = getFirstStopFromTime(0);
		while (s != null) {
			output.add(s);
			s = s.getNextInStation();
		}

		return output;
	}

	@Override
	public boolean equals(final Object otherStation) {
		if (otherStation instanceof Station) {
			return underlyingNode.equals(((Station) otherStation)
					.getUnderlyingNode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return underlyingNode.hashCode();
	}

	@Override
	public String toString() {
		return ("Station: " + "\n\tnode: " + underlyingNode + "\n\tid: "
				+ getId() + "\n\tname: " + getName() + "\n\tlatitude: "
				+ getLatitude().toString() + "\n\tlongitude: " + getLongitude()
				.toString());
		// "\n\tIsSchool: " + getIsSchool().toString() +
		// "\n\tIsTerminal: " + getIsTerminal().toString());
	}

	public ArrayList<Route> getAllRoutes() {
		Set<Route> set = new HashSet<Route>();

		for (Stop s : getAllStops()) {
			set.add(s.getRun().getRoute());
		}

		ArrayList<Route> result = new ArrayList<Route>(set);
		return result;
	}

	public ArrayList<Run> getAllRuns() {
		Set<Run> set = new HashSet<Run>();

		for (Stop s : getAllStops()) {
			set.add(s.getRun());
		}

		ArrayList<Run> result = new ArrayList<Run>(set);
		return result;
	}

	public String getUrl() {
		return "/stations/" + getId();
	}

	public void deleteStopIndex() {
		stopIndex.delete();
	}

	public void linkAllStopsInStation(int start, int end) {

		Transaction tx = DbConnection.getDb().beginTx();
		try {
			for (int dynEnd = 1440, dynStart = start; dynStart < end; dynStart += 1440) {
				dynEnd += 1440;
				if (dynStart <= BoaConfig.SEARCH_DAYS_LIMIT_IN_MINUTES) {
					linkStopsInStation(0, dynEnd);
				} else {
					linkStopsInStation(dynStart, dynEnd);
				}
				tx.success();
				tx.finish();
				tx = DbConnection.getDb().beginTx();
			}
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public void linkStopsInStation() {
		linkStopsInStation(0, BoaConfig.DAYS_IN_MINUTES);
	}

	public void linkStopsInStation(int start, int end) {
		// The whole week!
		Iterable<Stop> stops = getStopsFromTime(start, end);
		Stop prevStop = null;
		for (Stop s : stops) {
			s.setNextInStation(null);
			if (prevStop != null) {
				prevStop.setNextInStation(s);
			}
			prevStop = s;
		}
	}
}
