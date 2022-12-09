package org.pointerless.PetriNetCRN;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pointerless.PetriNetCRN.containers.PetriNet;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PetriNetExecutor {

	private PetriNet petriNet;
	private Double t = 0.0;
	private final Integer repeats;
	private Integer repeatNum = 0;

	private final PrintStream printStream;

	private static final ObjectMapper objectMapper = SerializationHelper.getObjectMapper();

	private boolean isFirst = true;

	public PetriNetExecutor(PetriNet petriNet, Integer repeats, PrintStream printStream){
		this.petriNet = petriNet;
		this.repeats = repeats;
		this.printStream = printStream;
		this.printStream.print("[");
	}

	public boolean step(Random random, Double tMax) throws IOException {
		if(t > tMax || !petriNet.canFire()){
			return this.checkForRepeat();
		}
		t = petriNet.runTransitions(t, random);
		if(!isFirst) printStream.print(",");
		else isFirst = false;
		printStream.write(objectMapper.writeValueAsBytes(petriNet.getStateForTimeAndRepeatNum(t, repeatNum)));
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
		printStream.print("]");
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

	/* TODO implement streamable
	public static void writeStateHistoryToCSV(File output, ArrayList<State> states){
		if(states.size() == 0) return;
		ArrayList<String> elements = new ArrayList<>(states.get(0).getState().keySet());
		ArrayList<String> columns = new ArrayList<>(elements);
		columns.add("time");
		columns.add("volume");
		columns.add("repeatNum");
		try (PrintWriter printWriter = new PrintWriter(output)){
			printWriter.println(toCSVFormat(columns));
			states.forEach(state -> {
				ArrayList<String> line = new ArrayList<>(columns.size());
				elements.forEach(element -> line.add(state.getState().get(element).toString()));
				line.add(state.getTime().toString());
				line.add(state.getVolume().toString());
				line.add(state.getRepeatNum().toString());
				printWriter.println(toCSVFormat(line));
			});
		}catch (FileNotFoundException e){
			throw new RuntimeException("Could not open output file: '"+output+"'");
		}
	}
	 */

	private static <E, T extends Collection<E>> String  toCSVFormat(T values){
		return values.stream().map(Object::toString).collect(Collectors.joining(","));
	}
}
