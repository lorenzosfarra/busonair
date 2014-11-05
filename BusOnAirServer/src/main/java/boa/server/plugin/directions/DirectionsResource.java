package boa.server.plugin.directions;

import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;

import com.vividsolutions.jts.geom.Coordinate;

import boa.server.domain.DbConnection;
import boa.server.routing.Criteria;
import boa.server.routing.ShortestPathGeo;
import boa.server.config.BoaConfig;

@Path("/directions/getdirections")
public class DirectionsResource {
	private BufferedWriter log;

	private int timeInterval = BoaConfig.SEARCH_DAYS_LIMIT_IN_MINUTES;

	public DirectionsResource(@Context Database database,
			@Context HttpServletRequest req, @Context OutputFormat output)
			throws IOException {
		this(new SessionFactoryImpl(req.getSession(true)), database, output);

		String fullURL = req.getRequestURL().append("?")
				.append(req.getQueryString()).toString();
		log.write("\n-HttpServletRequest(" + fullURL + ")");
		log.flush();
	}

	public DirectionsResource(SessionFactoryImpl sessionFactoryImpl,
			Database database, OutputFormat output) throws IOException {
		FileWriter logFile = new FileWriter(
				"/tmp/trasportaqdirectionsinside.log", true);
		log = new BufferedWriter(logFile);
		DbConnection.createDbConnection(database);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getDirections(@QueryParam("lat1") Double lat1,
			@QueryParam("lon1") Double lon1, @QueryParam("lat2") Double lat2,
			@QueryParam("lon2") Double lon2,
			@QueryParam("departureday") Integer departureday,
			@QueryParam("departuretime") Integer departuretime,
			@QueryParam("minchangetime") Integer minchangetime,
			@QueryParam("criterion") String criterion,
			@QueryParam("maxwalkdistance") Integer maxwalkdistance)
			throws IOException {

		log.write("\ngetDirection");

		if (lat1 == null || lon1 == null || lat2 == null || lon2 == null
				|| departureday == null || departuretime == null)
			return Response
					.ok()
					.entity(new boa.server.plugin.json.Response(400,
							"lat1, lon1, lat2, lon2, departureday, departuretime cannot be blank"))
					.build();

		if (maxwalkdistance == null)
			maxwalkdistance = new Integer(BoaConfig.MAX_WALK_DISTANCE);

		Criteria crit;

		log.write("departureday: " + departureday + ", departuretime: "
				+ departuretime + ", minchangetime: " + minchangetime);
		log.flush();

		if (criterion == null || criterion.equals("MINCHANGES"))
			crit = Criteria.MINCHANGES;
		else if (criterion.equals("LATESTLEAVING")
				|| criterion.equals("DURATION"))
			crit = Criteria.DURATION;
		else if (criterion.equals("MINWALK"))
			crit = Criteria.MINWALK;
		else
			crit = Criteria.MINCHANGES;
		Date start = new Date();
		ShortestPathGeo mysp = new ShortestPathGeo(departuretime,
				this.timeInterval, lat1, lon1, lat2, lon2, maxwalkdistance,
				crit);
		mysp.shortestPath();
		Date end = new Date();
		// Get msec from each, and subtract.
		long diff = end.getTime() - start.getTime();
		long diffSeconds = diff / 1000 % 60;
		log.write("\n\tstartTime: " + start + ", endTime: " + end
				+ ". Seconds: " + diff + "\n");
		log.flush();
		boa.server.plugin.json.Directions directs = new boa.server.plugin.json.Directions(
				new Coordinate(lon1, lat1), mysp.getArrivalList());
		return Response.ok().entity(directs).build();
	}
}
