package org.pointerless.PetriNetCRN.containers;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Transition {

	private HashMap<Place, Long> inputPlaces;

	private HashMap<Place, Long> outputPlaces;

	private Double c;

	private Double timeRemaining;

	private Double propensity;

	public Transition(HashMap<Place, Long> inputPlaces, HashMap<Place, Long> outputPlaces, Double c) {
		this.inputPlaces = inputPlaces;
		this.outputPlaces = outputPlaces;
		this.c = c;
		this.timeRemaining = 1.0/c;
		this.updatePropensity();
	}

	public boolean canFire() {
		for(Place place : inputPlaces.keySet()){
			if(!place.canConsume(inputPlaces.get(place))) return false;
		}
		return true;
	}

	public void fire() {
		if(!canFire()) throw new RuntimeException("Cannot fire!");
		for(Place place : inputPlaces.keySet()){
			place.consume(inputPlaces.get(place));
		}

		for(Place place : outputPlaces.keySet()){
			place.add(outputPlaces.get(place));
		}
	}

	public HashMap<Place, Long> getInputPlaces() {
		return inputPlaces;
	}

	public void setInputPlaces(HashMap<Place, Long> inputPlaces) {
		this.inputPlaces = inputPlaces;
	}

	public HashMap<Place, Long> getOutputPlaces() {
		return outputPlaces;
	}

	public void setOutputPlaces(HashMap<Place, Long> outputPlaces) {
		this.outputPlaces = outputPlaces;
	}

	public Double getC() {
		return c;
	}

	public void setC(Double c) {
		this.c = c;
	}

	public Double getPropensity() {
		return propensity;
	}

	private Double choose(Long n, Long k){
		return LongStream.concat(LongStream.of(1), LongStream.range(1, k + 1))
				.mapToObj(Double::valueOf)
				.reduce((x, i) -> (x*(n+1-i)/i))
				.orElseThrow();
	}

	private void updatePropensity() {
		this.propensity = Stream.concat(Stream.of(this.c),
				inputPlaces.entrySet().stream().map(p -> choose(p.getKey().getContains(), p.getValue())))
						.reduce((a, b) -> a*b).orElseThrow();
	}

	public Double getTimeRemaining() {
		return timeRemaining;
	}

	public Double updateTimeRemaining(Double time, Random random){
		this.updatePropensity();
		this.timeRemaining = time - (Math.log(random.nextFloat())/this.propensity);
		return timeRemaining;
	}

	@Override
	public String toString() {
		return "Transition{" +
				inputPlaces.entrySet().stream()
						.map(placeAmount -> placeAmount.getValue()+"*"+placeAmount.getKey().getElement())
						.collect(Collectors.joining(" + "))
				+ " = " +
				outputPlaces.entrySet().stream()
						.map(placeAmount -> placeAmount.getValue()+"*"+placeAmount.getKey().getElement())
						.collect(Collectors.joining(" + ")) +
				", c=" + c +
				", propensity=" + propensity +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transition that = (Transition) o;
		return inputPlaces.equals(that.inputPlaces) && outputPlaces.equals(that.outputPlaces) && c.equals(that.c);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inputPlaces, outputPlaces, c);
	}
}
