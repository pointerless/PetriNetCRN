package org.pointerless.PetriNetCRN.containers;

import java.util.*;
import java.util.stream.Stream;

public class TransitionPriorityQueue {

	public static class TransitionComparator implements Comparator<Transition> {

		@Override
		public int compare(Transition o1, Transition o2) {
			if (Objects.equals(o1.getTimeRemaining(), o2.getTimeRemaining())){
				return 0;
			} else if (o1.getTimeRemaining() > o2.getTimeRemaining()){
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

	public boolean actionNext(Double tickStep, Volume volume, Random random){
		this.queue.parallelStream().forEach(transition -> transition.updateTimeRemaining(tickStep, volume, random));
		Optional<Transition> fireable = this.queue.stream().sorted(comparator)
				.filter(Transition::canFire).findFirst();
		if(fireable.isPresent()){
			fireable.get().fire();
			return true;
		}
		return false;
	}

	public Stream<Transition> stream(){
		return this.queue.stream().sorted(comparator);
	}

	public ArrayList<Transition> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<Transition> queue) {
		this.queue = queue;
	}

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
