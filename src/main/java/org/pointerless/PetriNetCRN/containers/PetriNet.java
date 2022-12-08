package org.pointerless.PetriNetCRN.containers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class PetriNet {

	private ArrayList<Place> places;

	private TransitionPriorityQueue transitions;

	private Volume volume;

	public PetriNet(ArrayList<Place> places, ArrayList<Transition> transitions, Volume volume){
		this.places = places;
		this.transitions = new TransitionPriorityQueue(transitions);
		this.volume = volume;
		this.places.forEach(place -> place.updateContainsFromVolume(volume));
	}

	public boolean canFire() {
		return this.transitions.stream().anyMatch(Transition::canFire);
	}

	public boolean runTransitions(Double tickStep, Random random){
		return this.transitions.actionNext(tickStep, this.volume, random);
	}

	public State getStateForTime(Double t) {
		HashMap<String, Long> state = new HashMap<>();
		for(Place place : this.places){
			state.put(place.getElement(), place.getContains());
		}
		return new State(state, t, this.volume.getVolume(), 0);
	}

	public State getStateForTimeAndRepeatNum(Double t, Integer repeatNum) {
		HashMap<String, Long> state = new HashMap<>();
		for(Place place : this.places){
			state.put(place.getElement(), place.getContains());
		}
		return new State(state, t, this.volume.getVolume(), repeatNum);
	}

	public ArrayList<Place> getPlaces() {
		return places;
	}

	public void setPlaces(ArrayList<Place> places) {
		this.places = places;
	}

	public TransitionPriorityQueue getTransitions() {
		return transitions;
	}

	public void setTransitions(ArrayList<Transition> transitions) {
		this.transitions = new TransitionPriorityQueue(transitions);
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
		this.places.forEach(place -> place.updateContainsFromVolume(volume));
	}

	public void resetVolume() {
		this.volume.resetVolume();
		this.places.forEach(place -> place.updateContainsFromVolume(volume));
	}

	public boolean nextVolume() {
		if (this.volume.nextVolume()) {
			this.places.forEach(place -> place.updateContainsFromVolume(volume));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "PetriNet{\n" +
				"places=" + places +
				"\n, transitions=" + transitions +
				"\n, volume=" + volume +
				"\n}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PetriNet petriNet = (PetriNet) o;
		return Objects.equals(places, petriNet.places) && Objects.equals(transitions, petriNet.transitions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(places, transitions);
	}
}
