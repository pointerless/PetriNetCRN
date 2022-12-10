package org.pointerless.PetriNetCRN;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.Place;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PetriNetExecutor {

	private PetriNet petriNet;
	private Double t = 0.0;
	private final Integer repeats;
	private Integer repeatNum = 0;

	private final StateOutput stateOutput;

	public PetriNetExecutor(PetriNet petriNet, Integer repeats, StateOutput stateOutput){
		this.petriNet = petriNet;
		this.repeats = repeats;
		this.stateOutput = stateOutput;
		this.stateOutput.start(petriNet.getPlaces().stream().map(Place::getElement).sorted().collect(Collectors.toList()));
	}

	public boolean step(Random random, Double tMax) throws IOException {
		if(t > tMax || !petriNet.canFire()){
			return this.checkForRepeat();
		}
		t = petriNet.runTransitions(t, random);
		stateOutput.write(petriNet.getStateForTimeAndRepeatNum(t, repeatNum));
		return true;
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
		stateOutput.end();
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

}
