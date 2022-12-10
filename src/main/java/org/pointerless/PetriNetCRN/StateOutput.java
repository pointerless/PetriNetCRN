package org.pointerless.PetriNetCRN;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pointerless.PetriNetCRN.containers.State;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StateOutput {

	public enum Type {
		JSONPrintStream,
		CSVPrintStream,
		DataBase
	}

	private Type type;

	private String dbURL;

	private PrintStream printStream;

	private final ObjectMapper objectMapper = SerializationHelper.getObjectMapper();

	private boolean jsonIsFirst = true;

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

	public void start(List<String> elements){
		switch(type){
			case JSONPrintStream -> this.printStream.print("[");
			case CSVPrintStream -> this.writeCSVColumns(elements);
			case DataBase -> throw new RuntimeException("Can't output to DB yet");
		}
	}

	public void end(){
		switch(type){
			case JSONPrintStream -> this.printStream.print("]");
			case CSVPrintStream -> this.printStream.println();
			case DataBase -> throw new RuntimeException("Can't output to DB yet");
		}
	}

	private void writeJSON(State state) throws IOException {
		if(!jsonIsFirst){
			printStream.print(",");
		}else{
			jsonIsFirst = false;
		}
		printStream.write(objectMapper.writeValueAsBytes(state));
	}

	private void writeCSVColumns(List<String> elements){
		ArrayList<String> columns = new ArrayList<>(elements);
		columns.add("time");
		columns.add("volume");
		columns.add("repeatNum");
		printStream.println(toCSVFormat(columns));
	}

	private void writeCSV(State state){
		List<String> elements = state.getState().keySet().stream().sorted().collect(Collectors.toList());
		ArrayList<String> line = new ArrayList<>(elements.size()+3);
		elements.forEach(element -> line.add(state.getState().get(element).toString()));
		line.add(state.getTime().toString());
		line.add(state.getVolume().toString());
		line.add(state.getRepeatNum().toString());
		printStream.println(toCSVFormat(line));
	}

	private static <E, T extends Collection<E>> String  toCSVFormat(T values){
		return values.stream().map(Object::toString).collect(Collectors.joining(","));
	}

}
