package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoadInRam {

	private int nodesNumber;
	private int relsNumber;
	private long time;

	public LoadInRam() {

	}

	public LoadInRam(int nodesNumber, int relsNumber, long time) {
		this.nodesNumber = nodesNumber;
		this.relsNumber = relsNumber;
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getSeconds() {
		return time / 1000;
	}

	public int getNodesNumber() {
		return nodesNumber;
	}

	public void setNodesNumber(int nodesNumber) {
		this.nodesNumber = nodesNumber;
	}

	public int getRelsNumber() {
		return relsNumber;
	}

	public void setRelsNumber(int relsNumber) {
		this.relsNumber = relsNumber;
	}

}
