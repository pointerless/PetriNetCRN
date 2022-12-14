package org.pointerless.PetriNetCRN;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pointerless.PetriNetCRN.containers.State;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;


/**
 * StateOutput class, outputs states in configured format
 */
public class StateOutput {

	public enum Type {
		JSONPrintStream,
		CSVPrintStream,
		DataBase,
	}

	private Type type;

	private String dbURL;

	private PrintStream printStream;

	private final ObjectMapper objectMapper = SerializationHelper.getObjectMapper();

	private final ArrayBlockingQueue<State> queue = new ArrayBlockingQueue<>(1024);

	public void dbOutput(String dbURL){
		this.dbURL = dbURL;
	}

	public void csvOutput(PrintStream output){
		this.type = Type.CSVPrintStream;
		this.printStream = output;
	}

	public void jsonOutput(PrintStream output){
		this.type = Type.JSONPrintStream;
		this.printStream = output;
	}

	public void write(State state) throws IOException, RuntimeException {
		switch(type){
			case JSONPrintStream -> this.writeJSON(state);
			case CSVPrintStream -> this.writeCSV(state);
			case DataBase -> throw new RuntimeException("Can't output to DB yet");
		}
	}

	public void writeStart(List<String> elements){
		switch(type){
			case JSONPrintStream -> this.printStream.print("[");
			case CSVPrintStream -> this.writeCSVColumns(elements);
			case DataBase -> throw new RuntimeException("Can't output to DB yet");
		}
	}

	public void writeEnd(){
		switch(type){
			case JSONPrintStream -> this.printStream.print("]");
			case CSVPrintStream -> this.printStream.println();
			case DataBase -> throw new RuntimeException("Can't output to DB yet");
		}
	}

	private void writeJSON(State state) throws IOException {
		if(!state.isEnd()){
			printStream.print(",");
		}
		printStream.write(objectMapper.writeValueAsBytes(state));
	}

	private void writeCSVColumns(List<String> elements){
		ArrayList<String> columns = new ArrayList<>(elements);
		columns.add("time");
		columns.add("volume");
		columns.add("repeatNum");
		printStream.println(SerializationHelper.toCSVFormat(columns));
	}

	private void writeCSV(State state){
		List<String> elements = state.getState().keySet().stream().sorted().collect(Collectors.toList());
		ArrayList<String> line = new ArrayList<>(elements.size()+3);
		elements.forEach(element -> line.add(state.getState().get(element).toString()));
		line.add(state.getTime().toString());
		line.add(state.getVolume().toString());
		line.add(state.getRepeatNum().toString());
		printStream.println(SerializationHelper.toCSVFormat(line));
	}

	public ArrayBlockingQueue<State> getQueue() {
		return queue;
	}

	public void add(State state) throws InterruptedException {
		this.queue.put(state);
	}

}
