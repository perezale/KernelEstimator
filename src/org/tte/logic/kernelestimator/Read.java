package org.tte.logic.kernelestimator;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Read {

	@JsonProperty("Tiempo")
	private String tiempo;
	@JsonProperty("LAP")
	private String lap;
	@JsonProperty("Signal")
	private Integer signal;
	@JsonProperty("Distancia")
	private Integer distancia;

	public Read() {
	}

	public Read(String tiempo, String lap, Integer signal, Integer distancia) {
		super();
		this.tiempo = tiempo;
		this.lap = lap;
		this.signal = signal;
		this.distancia = distancia;
	}

	public String getTiempo() {
		return tiempo;
	}

	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}

	public String getLap() {
		return lap;
	}

	public void setLap(String lap) {
		this.lap = lap;
	}

	public Integer getSignal() {
		return signal;
	}

	public void setSignal(Integer signal) {
		this.signal = signal;
	}

	public Integer getDistancia() {
		return distancia;
	}

	public void setDistancia(Integer distancia) {
		this.distancia = distancia;
	}

	@Override
	public String toString() {
		return "Read [tiempo=" + tiempo + ", lap=" + lap + ", signal=" + signal
				+ ", distancia=" + distancia + "]";
	}

}
