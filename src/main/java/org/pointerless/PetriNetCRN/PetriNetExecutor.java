package org.pointerless.PetriNetCRN;

import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.Place;
import org.pointerless.PetriNetCRN.containers.State;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * PetriNetExecutor class, contains all code to execute
 * a set of samples.
 */
public class PetriNetExecutor {

	private PetriNet petriNet;
	private Double t = 0.0;
	private final Integer repeats;
	private Integer repeatNum = 0;

	private final StateOutput stateOutput;

	private final OutputThread outputThread;

	private final Thread threadHandle;

	public PetriNetExecutor(PetriNet petriNet, Integer repeats, StateOutput stateOutput){
		this.petriNet = petriNet;
		this.repeats = repeats;
		this.stateOutput = stateOutput;
		this.stateOutput.writeStart(petriNet.getPlaces().stream().map(Place::getElement).sorted().collect(Collectors.toList()));
		this.outputThread = new OutputThread(stateOutput);
		this.threadHandle = new Thread(outputThread);
		this.threadHandle.start();
	}

	public boolean step(Random random, Double tMax) throws IOException, InterruptedException {
		t = petriNet.runTransitions(t, random);
		boolean canRepeat = true;
		State state = petriNet.getStateForTimeAndRepeatNum(t, repeatNum);
		if(t >= tMax  || petriNet.hasConsensus() || !petriNet.canFire()){
			canRepeat = this.checkForRepeat();
		}
		state.setEnd(!canRepeat);
		stateOutput.add(state);
		return canRepeat;
	}

	private boolean checkForRepeat(){
		if(petriNet.nextVolume()){
			this.t = 0.0;
			return true;
		} else if (repeatNum+1 < repeats) {
			repeatNum += 1;
			petriNet.resetVolume();
			this.t = 0.0;
			return true;
		}
		return false;
	}

	public PetriNet getNet() {
		return petriNet;
	}

	public void setNet(PetriNet petriNet) {
		this.petriNet = petriNet;
	}

	public Double getT() {
		return this.t;
	}

	public Thread getThreadHandle(){
		return this.threadHandle;
	}

}
