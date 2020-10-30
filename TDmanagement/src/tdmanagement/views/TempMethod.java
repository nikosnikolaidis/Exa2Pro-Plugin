package tdmanagement.views;

public class TempMethod {
	private String name;
	private int CC;
	private double LCOL;


	TempMethod(String name, int CC, double LCOL){
		this.name=name;
		this.CC=CC;
		this.LCOL=LCOL;
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

	public double getLCOL() {
		return LCOL;
	}

	public void setLOC(int lCOL) {
		LCOL = lCOL;
	}

	public String toString() {
		return name;
	}
}
