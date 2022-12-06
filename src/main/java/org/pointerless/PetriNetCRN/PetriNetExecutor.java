package org.pointerless.PetriNetCRN;

import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.State;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class PetriNetExecutor {

	private PetriNet petriNet;
	private final Double tickStep;
	private Double t = 0.0;
	private final ArrayList<State> stateHistory = new ArrayList<>();

	public PetriNetExecutor(PetriNet petriNet, Double tickStep){
		this.petriNet = petriNet;
		this.tickStep = tickStep;
	}

	public boolean step(Random random) {
		t += tickStep;
		boolean fired = petriNet.runTransitions(t, random);
		stateHistory.add(petriNet.getStateForTime(t));
		return fired;
	}

	public PetriNet getNet() {
		return petriNet;
	}

	public void setNet(PetriNet petriNet) {
		this.petriNet = petriNet;
	}

	public Double getTickStep() {
		return this.tickStep;
	}

	public Double getT() {
		return this.t;
	}

	public ArrayList<State> getStateHistory() {
		return this.stateHistory;
	}

	public static void writeStateHistoryToCSV(File output, ArrayList<State> states){
		if(states.size() == 0) return;
		ArrayList<String> elements = new ArrayList<>(states.get(0).getState().keySet());
		ArrayList<String> columns = new ArrayList<>(elements);
		columns.add("Time");
		columns.add("Volume");
		try (PrintWriter printWriter = new PrintWriter(output)){
			printWriter.println(toCSVFormat(columns));
			states.forEach(state -> {
				ArrayList<String> line = new ArrayList<>(columns.size());
				elements.forEach(element -> line.add(state.getState().get(element).toString()));
				line.add(state.getT().toString());
				line.add(state.getVolume().toString());
				printWriter.println(toCSVFormat(line));
			});
		}catch (FileNotFoundException e){
			throw new RuntimeException("Could not open output file: '"+output+"'");
		}
	}

	public static void writeRepeatStateHistoryToCSV(File output, HashMap<Integer, ArrayList<State>> statesRepeats){
		if(statesRepeats.size() == 0) return;
		ArrayList<String> elements = new ArrayList<>(statesRepeats.get(0).get(0).getState().keySet());
		ArrayList<String> columns = new ArrayList<>(elements);
		columns.add("Time");
		columns.add("Volume");
		columns.add("RepeatNum");
		try (PrintWriter printWriter = new PrintWriter(output)){
			printWriter.println(toCSVFormat(columns));
			statesRepeats.forEach((repeat, states) -> {
				states.forEach(state -> {
					ArrayList<String> line = new ArrayList<>(columns.size());
					elements.forEach(element -> line.add(state.getState().get(element).toString()));
					line.add(state.getT().toString());
					line.add(state.getVolume().toString());
					line.add(repeat.toString());
					printWriter.println(toCSVFormat(line));
				});
			});
		}catch (FileNotFoundException e){
			throw new RuntimeException("Could not open output file: '"+output+"'");
		}
	}

	private static <E, T extends Collection<E>> String  toCSVFormat(T values){
		return values.stream().map(Object::toString).collect(Collectors.joining(","));
	}
}
