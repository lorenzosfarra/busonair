package boa.server.plugin.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "List")
public class CheckPointsObjects {
	@XmlElement(name = "checkPointsObjectsList")
	public List<CheckPoint> checkpointlist = new ArrayList<CheckPoint>();

	public CheckPointsObjects() {
	}

	public void add(CheckPoint cp) {
		checkpointlist.add(cp);
	}

	public void add(boa.server.domain.CheckPoint cp) {
		checkpointlist.add(new CheckPoint(cp));
	}

}
