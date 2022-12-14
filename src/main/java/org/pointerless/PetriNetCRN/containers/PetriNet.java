package org.pointerless.PetriNetCRN.containers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


/**
 * PetriNet class, represents a Petri Net for a set of
 * transitions and places with a given volume.
 */
public class PetriNet {

	private ArrayList<Place> places;

	private TransitionPriorityQueue transitions;

	private Volume volume;

	private final Place consensusElement;

	public PetriNet(ArrayList<Place> places, ArrayList<Transition> transitions, Volume volume, Place consensusElement){
		this.places = places;
		this.transitions = new TransitionPriorityQueue(transitions);
		this.volume = volume;
		this.consensusElement = consensusElement;
		this.places.forEach(place -> place.updateContainsFromVolume(volume));
	}

	public boolean canFire() {
		return this.transitions.anyCanFire();
	}

	public Double runTransitions(Double time, Random random){
		return this.transitions.actionNext(time, this.volume, random);
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

	public Place getConsensusElement() {
		return consensusElement;
	}

	public boolean hasConsensus(){
		if(consensusElement == null) return false;
		return consensusElement.getContains().equals(volume.getVolume());
	}

	@Override
	public String toString() {
		return "PetriNet{\n" +
				"places=" + places +
				"\n, transitions=" + transitions +
				"\n, volume=" + volume +
				"\n, consensusElement=" + consensusElement +
				"\n}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PetriNet petriNet = (PetriNet) o;
		return Objects.equals(places, petriNet.places) && Objects.equals(transitions, petriNet.transitions) &&
				Objects.equals(consensusElement, petriNet.consensusElement);
	}

	@Override
	public int hashCode() {
		return Objects.hash(places, transitions, consensusElement);
	}
}
