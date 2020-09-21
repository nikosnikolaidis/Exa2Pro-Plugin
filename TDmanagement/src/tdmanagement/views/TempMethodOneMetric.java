package tdmanagement.views;

public class TempMethodOneMetric {
	private String name;
	private Double metric;


	public TempMethodOneMetric(String name, Double double1){
		this.name=name;
		this.metric=double1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getMetric() {
		return metric;
	}

	public void setMetric(Double metric) {
		this.metric = metric;
	}

	public String toString() {
		return name;
	}

}
