package tdmanagement.views;

public class TempMethod {
	private String name;
	private int CC;
	private int LOC;


	TempMethod(String name, int CC, int LOC){
		this.name=name;
		this.CC=CC;
		this.LOC=LOC;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCC() {
		return CC;
	}

	public void setCC(int cC) {
		CC = cC;
	}

	public int getLOC() {
		return LOC;
	}

	public void setLOC(int lOC) {
		LOC = lOC;
	}

	public String toString() {
		return name;
	}
}
