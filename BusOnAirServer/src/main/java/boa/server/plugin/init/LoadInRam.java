package boa.server.plugin.init;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.GET;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;

import boa.server.domain.DbConnection;

/**
 * Load all the Nodes and relationships in RAM. Neo4j loads all the data lazily,
 * meaning it loads them into memory at first access. The caching option is just
 * about the GC strategy, so when (or if) the references will be GCed. So, to
 * load the whole graph into memory, we need to traverse the whole graph once.
 */
@Path("/init")
public class LoadInRam {

	private GraphDatabaseService graph;
	private int nodesNumber;
	private int relationshipsNumber;
	private long loadingTime;
	private GlobalGraphOperations graphOperation;

	public LoadInRam() {

	}
	public LoadInRam(@Context Database database,
			@Context HttpServletRequest req, @Context OutputFormat output)
			throws IOException {
		this(new SessionFactoryImpl(req.getSession(true)), database, output);
	}

	public LoadInRam(SessionFactoryImpl sessionFactoryImpl, Database database,
			OutputFormat output) throws IOException {
		DbConnection.createDbConnection(database);
		graph = DbConnection.getDb();
		graphOperation = GlobalGraphOperations.at(graph);

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/loadinram")
	public Response getAll(
			@QueryParam("relationships") Boolean loadRelationships)
			throws IOException {
		boa.server.test.utils.Chronometer ch = new boa.server.test.utils.Chronometer();
		ch.start();
		for (Node node : graphOperation.getAllNodes()) {
			nodesNumber += 1;
			// Nodes like this are already loaded. Load relationships, too?
			if (loadRelationships == null) {
				loadRelationships = false;
			}
			if (loadRelationships) {
				relationshipsNumber += IteratorUtil.count(node
						.getRelationships());
			}
		}
		ch.stop();
		if (!loadRelationships)
			relationshipsNumber = -1;

		boa.server.plugin.json.LoadInRam response = new boa.server.plugin.json.LoadInRam(
				nodesNumber, relationshipsNumber, ch.getTime());
		return Response.ok().entity(response).build();

	}

	public int getNodesNumber() {
		return this.nodesNumber;
	}

	public int getRelationshipsNumber() {
		return this.relationshipsNumber;
	}

}
