package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "route")
public class Route {
	private int id;
	private String line;
	private String from;
	private String towards;
	private String url;

	public Route() {
	}

	private Route(int id, String line) {
		this.id = id;
		this.line = line;
	}

	public Route(boa.server.domain.Route r) {
		this(r.getId(), r.getLine());
		setFrom(r.getFrom().getUrl());
		setTowards(r.getTowards().getUrl());
		setUrl(r.getUrl());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getline() {
		return line;
	}

	public void setline(String line) {
		this.line = line;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTowards() {
		return towards;
	}

	public void setTowards(String towards) {
		this.towards = towards;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
