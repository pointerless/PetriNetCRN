package org.pointerless.PetriNetCRN.containers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class State implements Serializable {

	private HashMap<String, Long> state;

	private Double time;

	private Long volume;

	private Integer repeatNum;

	public State(HashMap<String, Long> state, Double time, Long volume, Integer repeatNum){
		this.state = state;
		this.time = time;
		this.volume = volume;
		this.repeatNum = repeatNum;
	}

	public HashMap<String, Long> getState() {
		return state;
	}

	public void setState(HashMap<String, Long> state) {
		this.state = state;
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double t) {
		this.time = t;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public Integer getRepeatNum() {
		return repeatNum;
	}

	public void setRepeatNum(Integer repeatNum) {
		this.repeatNum = repeatNum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		State state1 = (State) o;
		return state.equals(state1.state) && time.equals(state1.time) && volume.equals(state1.volume) && repeatNum.equals(state1.repeatNum);
	}

	@Override
	public int hashCode() {
		return Objects.hash(state, time, volume, repeatNum);
	}
}
