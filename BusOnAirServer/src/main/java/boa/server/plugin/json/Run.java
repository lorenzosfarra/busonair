package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;
// LOGGING
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@XmlRootElement(name = "run")
public class Run {
	private int id;
	private String route;
	private String firstStop;
	private String firstCheckPoint;
	private String url;

	public Run() {
	}

	private Run(int id) {
		this.id = id;
	}

	public Run(boa.server.domain.Run r) {
		this(r.getId());
		setRoute(r.getRoute().getUrl());
		try {
			FileWriter logFile = new FileWriter("/tmp/booo.log", true);
			BufferedWriter log = new BufferedWriter(logFile);
			log.write(r.toString());

		} catch (IOException e) {
		}
		setFirstStop(r.getFirstStop().getUrl());
		setFirstCheckPoint(r.getFirstCheckPoint().getUrl());
		setUrl(r.getUrl());

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getFirstStop() {
		return firstStop;
	}

	public void setFirstStop(String firstStop) {
		this.firstStop = firstStop;
	}

	public String getFirstCheckPoint() {
		return firstCheckPoint;
	}

	public void setFirstCheckPoint(String firstCheckPoint) {
		this.firstCheckPoint = firstCheckPoint;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
