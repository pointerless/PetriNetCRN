package org.pointerless.PetriNetCRN.containers;

import org.mariuszgromada.math.mxparser.Function;

import java.util.Objects;


/**
 * Place class, represents a place within a Petri Net,
 * essentially a holder for a quantity of a specific
 * element
 */
public class Place {

	private String element;

	private Long contains;

	private final Function quantityGenerator;

	public Place(String element, Double volumeRatio) {
		this.element = element;
		this.quantityGenerator = new Function("f(n) = n*"+volumeRatio.toString());
	}

	public Place(String element, String generator){
		this.element = element;
		this.quantityGenerator = new Function(generator);
	}

	public void add(Long amount) {
		this.contains += amount;
	}

	public void consume(Long amount) {
		if(this.contains - amount < 0) throw new IllegalArgumentException("Cannot consume "+amount+" from "+contains);
		this.contains -= amount;
	}

	public boolean canConsume(Long amount) {
		return this.contains >= amount;
	}

	public String getElement() {
		return element;
	}

	public void setAccepts(String accepts) {
		this.element = element;
	}

	public Long getContains() {
		return contains;
	}


	public void updateContainsFromVolume(Volume volume){
		this.contains = Math.round(this.quantityGenerator.calculate(volume.getVolume()));
	}

	@Override
	public String toString() {
		return "Place{" +
				"element='" + element + '\'' +
				", contains=" + contains +
				", generator=" + quantityGenerator.getFunctionExpressionString() +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Place place = (Place) o;
		return element.equals(place.element) && Objects.equals(contains, place.contains) && quantityGenerator.equals(place.quantityGenerator);
	}

	@Override
	public int hashCode() {
		return Objects.hash(element);
	}
}
