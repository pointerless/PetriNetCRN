package org.pointerless.PetriNetCRN.containers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class State implements Serializable {

	private HashMap<String, Long> state;

	private Double t;

	private Long volume;

	public State(HashMap<String, Long> state, Double t, Long volume){
		this.state = state;
		this.t = t;
		this.volume = volume;
	}

	public HashMap<String, Long> getState() {
		return state;
	}

	public void setState(HashMap<String, Long> state) {
		this.state = state;
	}

	public Double getT() {
		return t;
	}

	public void setT(Double t) {
		this.t = t;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		State state1 = (State) o;
		return state.equals(state1.state) && t.equals(state1.t) && volume.equals(state1.volume);
	}

	@Override
	public int hashCode() {
		return Objects.hash(state, t, volume);
	}
}
