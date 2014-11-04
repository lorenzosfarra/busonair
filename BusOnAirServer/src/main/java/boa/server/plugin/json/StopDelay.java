package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stop")
public class StopDelay {
	private int id;
	private int staticTime;
	private String nextInRun;
	private String prevInRun;
	private String station;
	private String stationName;
	private String run;
	private String url;
	private int delay;

	public StopDelay() {
	}

	private StopDelay(int id, int staticTime) {
		this.id = id;
		this.staticTime = staticTime;
	}

	public StopDelay(boa.server.domain.Stop s, int delay) {
		this(s.getId(), s.getStaticTime());
		setNextInRun(s.getNextInRun() != null ? s.getNextInRun().getUrl() : "");
		setPrevInRun(s.getPrevInRun() != null ? s.getPrevInRun().getUrl() : "");
		boa.server.domain.Station station = s.getStation();
		setStation(station.getUrl());
		setStationName(station.getName());
		setRun(s.getRun().getUrl());
		setUrl(s.getUrl());
		setDelay(delay);
	}

	public String getStationName() {
		return this.stationName;
	}

	public void setStationName(String name) {
		this.stationName = name;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStaticTime() {
		return staticTime;
	}

	public void setStaticTime(int staticTime) {
		this.staticTime = staticTime;
	}

	public String getNextInRun() {
		return nextInRun;
	}

	public void setNextInRun(String nextInRun) {
		this.nextInRun = nextInRun;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getPrevInRun() {
		return prevInRun;
	}

	public void setPrevInRun(String prevInRun) {
		this.prevInRun = prevInRun;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
