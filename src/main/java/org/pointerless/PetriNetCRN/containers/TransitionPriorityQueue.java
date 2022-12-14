package org.pointerless.PetriNetCRN.containers;

import java.util.*;
import java.util.stream.Stream;


/**
 * Priority Queue to automatically find the next
 * transition and update next occurrences on
 * system change.
 */
public class TransitionPriorityQueue {

	public static class TransitionComparator implements Comparator<Transition> {

		@Override
		public int compare(Transition o1, Transition o2) {
			if (Objects.equals(o1.getNextOccurrence(), o2.getNextOccurrence())){
				return 0;
			} else if (o1.getNextOccurrence() > o2.getNextOccurrence()){
				return 1;
			}
			return -1;
		}
	}

	private static final TransitionComparator comparator = new TransitionComparator();

	private ArrayList<Transition> queue;

	public TransitionPriorityQueue(ArrayList<Transition> transitions){
		queue = transitions;
	}

	public Double actionNext(Double time, Volume volume, Random random){
		this.queue.parallelStream().forEach(transition -> transition.updateTimeRemaining(time, volume, random));
		Optional<Transition> fireable = this.queue.stream().sorted(comparator)
				.filter(Transition::canFire).findFirst();
		if(fireable.isPresent()){
			return fireable.get().fire();
		}
		throw new RuntimeException("Cannot fire");
	}

	public boolean anyCanFire(){
		return this.queue.parallelStream().anyMatch(Transition::canFire);
	}

	public ArrayList<Transition> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<Transition> queue) {
		this.queue = queue;
	}

	public Stream<Transition> stream(){ return this.queue.stream().sorted(comparator); }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TransitionPriorityQueue that = (TransitionPriorityQueue) o;
		return queue.equals(that.queue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(queue);
	}
}
