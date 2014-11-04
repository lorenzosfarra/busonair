package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "firstavailablerun")
public class FirstAvailableRun {
	private Integer id;
	private Integer route;
	private Integer firstStop;
	private Integer firstCheckPoint;
	private Integer firstCheckPointTime;

	public FirstAvailableRun() {
	}

	public FirstAvailableRun(Integer id, Integer route, Integer firstStop,
			Integer firstCheckPoint) {
		super();
		this.id = id;
		this.route = route;
		this.firstStop = firstStop;
		this.firstCheckPoint = firstCheckPoint;
	}

	public FirstAvailableRun(boa.server.domain.Run r) {
		this(r.getId(), r.getRoute().getId(), r.getFirstStop().getId(), r
				.getFirstCheckPoint().getId());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRoute() {
		return route;
	}

	public void setRoute(Integer route) {
		this.route = route;
	}

	public Integer getFirstStop() {
		return firstStop;
	}

	public void setFirstStop(Integer firstStop) {
		this.firstStop = firstStop;
	}

	public Integer getFirstCheckPoint() {
		return firstCheckPoint;
	}

	public void setFirstCheckPoint(Integer firstCheckPoint) {
		this.firstCheckPoint = firstCheckPoint;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof FirstAvailableRun))
			return false;

		FirstAvailableRun otherRun = (FirstAvailableRun) other;

		if (getId() != otherRun.getId())
			return false;

		return true;
	}

	public Integer getFirstCheckPointTime() {
		return firstCheckPointTime;
	}

	public void setFirstCheckPointTime(Integer firstCheckPointTime) {
		this.firstCheckPointTime = firstCheckPointTime;
	}

	@Override
	public String toString() {
		return "Run [id=" + id + ", route=" + route + ", firstStop="
				+ firstStop + ", firstCheckPoint=" + firstCheckPoint + "]";
	}

}
