package boa.server.domain;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Stop {
	protected static final String ID = "id";
	protected static final String TYPE = "type";
	protected static final String TIME = "time";
	protected static final String STATICTIME = "staticTime";

	protected Node underlyingNode;

	// --- TRANSIENT ATTRIBUTES ---
	public boolean visited = false;
	public Stop nextInRun = null;
	public Stop nextInStation = null;
	public Stop prevInRun = null;
	public Stop prevInStation = null;
	public Stop nextWalk = null;
	public Stop prevWalk = null;
	public Stop prevSP = null;
	public int numeroCambi = 0;
	public int departureTime = 0;
	public int minChangeTime = 1400;
	public int walkDistance = 0;
	public int travelTime = 0;
	public int waitTime = 0;
	public int walkTime = 0;

	// --- end TRANSIENT ATTRIBUTES ---

	public Stop() {
	}

	public Stop(Node node) {
		underlyingNode = node;
	}

	public Stop(Node node, int id, int time, Station s, Run r) {
		this(node);
		setId(id);
		setTime(time);
		setType();
		setStation(s);
		setRun(r);
		Stops.getStops().addStop(this);
		if (s != null)
			s.addStop(this);
	}

	public void deleteAllRels() {
		setNextInRun(null);
		setNextInStation(null);
		setRun(null);
		setStation(null);
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	public Integer getId() {
		return (Integer) underlyingNode.getProperty(ID);
	}

	private void setId(int id) {
		underlyingNode.setProperty(Stop.ID, id);
	}

	public String getType() {
		return (String) underlyingNode.getProperty(TYPE);
	}

	private void setType() {
		underlyingNode.setProperty(Stop.TYPE, "Stop");
	}

	public Integer getTime() {
		return (Integer) underlyingNode.getProperty(TIME);
	}

	public void setTime(int time) {
		underlyingNode.setProperty(Stop.TIME, time);
	}

	public Integer getStaticTime() {
		return (Integer) underlyingNode.getProperty(STATICTIME);
	}

	private void setStaticTime(int staticTime) {
		underlyingNode.setProperty(Stop.STATICTIME, staticTime);
	}

	public Station getStation() {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.STOP_STATION, Direction.OUTGOING);
		return new Station(rel.getEndNode());
	}

	public void setStation(Station s) {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.STOP_STATION, Direction.OUTGOING);
		if (rel != null)
			rel.delete();

		if (s != null)
			underlyingNode.createRelationshipTo(s.getUnderlyingNode(),
					RelTypes.STOP_STATION);
	}

	public Run getRun() {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.STOP_RUN, Direction.OUTGOING);
		return new Run(rel.getEndNode());
	}

	public void setRun(Run r) {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.STOP_RUN, Direction.OUTGOING);
		if (rel != null)
			rel.delete();

		if (r != null)
			underlyingNode.createRelationshipTo(r.getUnderlyingNode(),
					RelTypes.STOP_RUN);
	}

	public Stop getNextInStation() {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.NEXTINSTATION, Direction.OUTGOING);
		if (rel == null)
			return null;
		else
			return new Stop(rel.getEndNode());
	}

	public Stop getPrevInStation() {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.NEXTINSTATION, Direction.INCOMING);
		if (rel == null)
			return null;
		else
			return new Stop(rel.getStartNode());
	}

	public void setNextInStation(Stop stop) {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.NEXTINSTATION, Direction.OUTGOING);
		if (rel != null)
			rel.delete();

		if (stop != null)
			underlyingNode.createRelationshipTo(stop.getUnderlyingNode(),
					RelTypes.NEXTINSTATION);
	}

	public Stop getNextInRun() {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.NEXTINRUN, Direction.OUTGOING);
		if (rel == null)
			return null;
		else
			return new Stop(rel.getEndNode());
	}

	public Stop getPrevInRun() {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.NEXTINRUN, Direction.INCOMING);
		if (rel == null)
			return null;
		else
			return new Stop(rel.getStartNode());
	}

	public void setNextInRun(Stop stop) {
		Relationship rel = underlyingNode.getSingleRelationship(
				RelTypes.NEXTINRUN, Direction.OUTGOING);

		if (rel != null)
			rel.delete();

		if (stop != null)
			underlyingNode.createRelationshipTo(stop.getUnderlyingNode(),
					RelTypes.NEXTINRUN);
	}

	@Override
	public boolean equals(final Object otherStop) {
		if (otherStop == null)
			return false;

		if (otherStop instanceof TransientStop)
			return false;

		if (otherStop instanceof Stop) {
			return underlyingNode
					.equals(((Stop) otherStop).getUnderlyingNode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return underlyingNode.hashCode();
	}

	@Override
	public String toString() {
		return ("(STOPID" + getId() + ":NODEID" + getUnderlyingNode().getId()
				+ ":TIME" + getTime() + ":STATICTIME" + getStaticTime() + ")");
	}

	public void updateStaticTime(int staticTime) {
		setStaticTime(staticTime);
		setTime(staticTime);
		updateStopPosition();
	}

	public void updateStopPosition() {
		Stop pis = getPrevInStation();
		Stop nis = getNextInStation();

		if (pis != null && pis.getTime() > getTime()) {
			while (pis != null && pis.getTime() > getTime()) {
				nis = pis;
				pis = pis.getPrevInStation();
			}
			moveStop(pis, nis);
		} else if (nis != null && nis.getTime() < getTime()) {
			while (nis != null && nis.getTime() < getTime()) {
				pis = nis;
				nis = nis.getNextInStation();
			}
			moveStop(pis, nis);
		}
	}

	public void moveStop(Stop prev, Stop next) {
		// sposta s tra prev e next

		if (prev == null && getNextInStation() != null
				&& getNextInStation().equals(next))
			return;

		if (prev != null && prev.getNextInStation() != null
				&& prev.getNextInStation().equals(this)
				&& getNextInStation() != null
				&& getNextInStation().equals(next))
			return;

		Stop pis = getPrevInStation();
		Stop nis = getNextInStation();

		if (prev != null)
			prev.setNextInStation(this);
		setNextInStation(next);
		if (pis != null) {
			pis.setNextInStation(nis);
		}
	}

	public String getUrl() {
		return "/stops/" + getId();
	}

}
