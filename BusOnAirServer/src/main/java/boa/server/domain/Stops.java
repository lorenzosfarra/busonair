package boa.server.domain;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import boa.server.config.BoaConfig;

public class Stops {
	protected Index<Node> stopsIndex;

	protected static Stops instance = null;

	public static synchronized Stops getStops() {
		if (instance == null)
			instance = new Stops();
		return instance;
	}

	public static void destroy() {
		instance = null;
	}

	protected Stops() {
		stopsIndex = DbConnection.getDb().index().forNodes("stopsIndex");
	}

	public void addStop(Stop s) {
		stopsIndex.add(s.getUnderlyingNode(), "id", s.getId());
	}

	public void removeStop(Stop s) {
		stopsIndex.remove(s.getUnderlyingNode());
	}

	public Stop getStopById(Integer id) {
		if (id == null)
			return null;

		IndexHits<Node> result = stopsIndex.get("id", id);
		Node n = result.getSingle();
		result.close();
		if (n == null) {
			return null;
		} else {
			return new Stop(n);
		}
	}

	public ArrayList<Stop> getAll() {
		ArrayList<Stop> output = new ArrayList<Stop>();
		IndexHits<Node> result = stopsIndex.query("id", "*");
		for (Node n : result) {
			output.add(new Stop(n));
		}
		result.close();
		return output;
	}

	public Stop createOrUpdateStop(boa.server.domain.json.Stop js) {
		// creates a new stop having the specified id
		// if the id already exists then updates the corresponding db record

		Stop s = getStopById(js.getId());
		Station staz = Stations.getStations().getStationById(js.getStation());
		Run run = Runs.getRuns().getRunById(js.getRun());

		if (s == null) { // create
			s = new Stop(DbConnection.getDb().createNode(), js.getId(),
					js.getStaticTime(), staz, run);
			addStop(s);
		}

		s.updateStaticTime(js.getStaticTime());
		s.setRun(run);
		s.setStation(staz);

		if (staz != null) {
			staz.updateStopIndex(s);
			// COMMENT THE LINE BELOW IF IMPORTING FOR THE FIRST TIME!
			staz.linkStopsInStation();
		}

		Stop pir = Stops.getStops().getStopById(js.getPrevInRun());
		Stop nir = Stops.getStops().getStopById(js.getNextInRun());

		if (pir != null) {
			pir.setNextInRun(s);
		}

		s.setNextInRun(nir);

		return s;
	}

	public void createOrUpdateStops(boa.server.domain.json.Stops stops) {
		// creates new stops having the specified ids
		// if an id already exists then updates the corresponding db record

		int i = 0;

		for (boa.server.domain.json.Stop s : stops.stopsObjectsList) {
			Transaction tx = DbConnection.getDb().beginTx();

			try {
				createOrUpdateStop(s);
				if ((i > 0) && (i % BoaConfig.TRANSACTIONS_LIMIT == 0)) {
					tx.success();
					tx.finish();
					tx = DbConnection.getDb().beginTx();
				}
				tx.success();
			} finally {
				tx.finish();
			}
			i++;
		}

	}

	public void deleteStop(Stop s) {
		Run run = s.getRun();

		Stop fsrun = run.getFirstStop();

		if (s.equals(fsrun))
			run.setFirstStop(s.getNextInRun());

		Stop pis = s.getPrevInStation();
		Stop nis = s.getNextInStation();

		Stop pir = s.getPrevInRun();
		Stop nir = s.getNextInRun();

		Station staz = s.getStation();

		if (pis != null) {
			pis.setNextInStation(nis);
		}

		if (pir != null) {
			pir.setNextInRun(nir);
		}

		Iterable<Relationship> rels = s.getUnderlyingNode().getRelationships(
				RelTypes.CHECKPOINTFROM, Direction.INCOMING);
		for (Relationship r : rels) {
			run.deleteCheckPoint(new CheckPoint(r.getStartNode()));
		}

		rels = s.getUnderlyingNode().getRelationships(
				RelTypes.CHECKPOINTTOWARDS, Direction.INCOMING);
		for (Relationship r : rels) {
			run.deleteCheckPoint(new CheckPoint(r.getStartNode()));
		}

		staz.removeStop(s);
		s.setRun(null);
		s.setStation(null);
		s.setNextInRun(null);
		s.setNextInStation(null);
		removeStop(s);
		s.getUnderlyingNode().delete();
	}

	public void deleteAllStops() {
		for (Stop s : getAll()) {
			deleteStop(s);
		}
	}
}
