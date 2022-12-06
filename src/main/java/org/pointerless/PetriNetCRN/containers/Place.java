package org.pointerless.PetriNetCRN.containers;

import java.util.Objects;

public class Place {

	private String element;

	private Double volumeRatio;

	private Long contains;

	public Place(String element, Double volumeRatio) {
		this.element = element;
		this.volumeRatio = volumeRatio;
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

	public Double getVolumeRatio() {
		return volumeRatio;
	}

	public void setVolumeRatio(Double volumeRatio) {
		this.volumeRatio = volumeRatio;
	}

	public void updateContainsFromVolume(Volume volume){
		this.contains = Math.round(volume.getVolume() * this.volumeRatio);
	}

	@Override
	public String toString() {
		return "Place{" +
				"element='" + element + '\'' +
				", contains=" + contains +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Place place = (Place) o;
		return element.equals(place.element) && volumeRatio.equals(place.volumeRatio);
	}

	@Override
	public int hashCode() {
		return Objects.hash(element);
	}
}
