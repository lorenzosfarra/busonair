package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "station")
public class Station {
	private int id;
	private String name;
	private Coordinate latLon;
	private String url;

	public Station() {
	}

	public Station(int id, String name, Coordinate latLon) {
		super();
		this.id = id;
		this.name = name;
		this.latLon = latLon;
	}

	public Station(boa.server.domain.Station s) {
		this(s.getId(), s.getName(), new Coordinate(s.getLatitude(),
				s.getLongitude()));
		setUrl(s.getUrl());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Coordinate getLatLon() {
		return latLon;
	}

	public void setLatLon(Coordinate latLon) {
		this.latLon = latLon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
