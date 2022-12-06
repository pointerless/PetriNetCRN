package org.pointerless.CLI;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pointerless.PetriNetCRN.PetriNetExecutor;
import org.pointerless.PetriNetCRN.SerializationHelper;
import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.State;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CLI {

	public static class Args {
		@Parameter(names = {"--crnPath", "-p"}, description = "Chemical Reaction Network JSON file path")
		private String crnPath = "";

		@Parameter(names = {"--crnData", "-d"}, description = "Chemical Reaction Network JSON data")
		private String crnData = "";

		@Parameter(names = {"--seed", "-s"}, description = "Seed for random generation")
		private long seed = 0L;

		@Parameter(names = {"--help", "-h"}, description = "Print this help dialogue", help = true)
		private boolean help = false;

		@Parameter(names = {"--outJSON", "-oJ"}, description = "Output JSON file path")
		private String outputJSON = "";

		@Parameter(names = {"--outCSV", "-oC"}, description = "Output CSV file path")
		private String outputCSV = "";

		@Parameter(names = {"--repeats", "-r"}, description = "Number of repeats to do")
		private int repeats = 1;

		@Parameter(names = {"--tMax", "-tM"}, description = "Maximum allowed tick time for reaction network")
		private Double tMax = 1000.0;

		@Parameter(names = {"--tick", "-t"}, description = "Tick step time")
		private Double tick = 0.1;
	}


	public static void main(String[] argv){
		Args args = new Args();
		JCommander commander = JCommander.newBuilder()
				.addObject(args)
				.build();
		commander.setProgramName("PetriNetCRN");
		commander.parse(argv);

		if(args.help || (args.crnPath.isEmpty() && args.crnData.isEmpty())
		   || (!args.crnPath.isEmpty() && !args.crnData.isEmpty())){
			commander.usage();
			return;
		}

		if(args.repeats < 1){
			System.err.println("Negative or zero repeats not possible");
			System.exit(-1);
		}

		Random random = new Random();
		if(args.seed == 0L){
			args.seed = random.nextLong();
			random.setSeed(args.seed);
		}

		HashMap<Integer, ArrayList<State>> finalStates = new HashMap<>();
		String JSONNet = args.crnData;
		if(!args.crnPath.isEmpty()) {
			System.out.println("Attempting to read from path '" + args.crnPath + "'...");
			try {
				JSONNet = String.join("\n", Files.readAllLines(Path.of(args.crnPath)));
			}catch(IOException e){
				System.err.println("ERROR: Could not read '" + args.crnPath + "'");
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}

		ObjectMapper objectMapper = SerializationHelper.getObjectMapper();
		for(int repeatNum = 0; repeatNum < args.repeats; repeatNum++) {
			if(args.repeats > 1) System.out.println("Executing repeat: "+repeatNum);
			PetriNet petriNet = null;
			try {
				petriNet = objectMapper.readValue(JSONNet, PetriNet.class);
			}catch (Exception e){
				System.err.println("ERROR: Could not deserialize");
				System.err.println(e.getMessage());
				System.exit(-1);
			}
			PetriNetExecutor petriNetExecutor = new PetriNetExecutor(petriNet, args.tick);

			boolean fired = true;
			while(fired && petriNetExecutor.getT() <= args.tMax) {
				fired = petriNetExecutor.step(random);
			}
			finalStates.put(repeatNum, petriNetExecutor.getStateHistory());
		}

		if(!args.outputJSON.isEmpty() || args.outputCSV.isEmpty()){
			try {
				if(args.repeats == 1) {
					if(args.outputJSON.isEmpty()) {
						String output = objectMapper.writeValueAsString(finalStates.get(0));
						System.out.println(output);
					}else {
						objectMapper.writeValue(new File(args.outputJSON), finalStates.get(0));
					}
				}else {
					if(args.outputJSON.isEmpty()) {
						String output = objectMapper.writeValueAsString(finalStates);
						System.out.println(output);
					}else {
						objectMapper.writeValue(new File(args.outputJSON), finalStates);
					}
				}
			}catch (Exception e){
				System.err.println("Could not serialize!");
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}

		if(!args.outputCSV.isEmpty()){
			if(args.repeats == 1){
				PetriNetExecutor.writeStateHistoryToCSV(new File(args.outputCSV), finalStates.get(0));
			}else{
				PetriNetExecutor.writeRepeatStateHistoryToCSV(new File(args.outputCSV), finalStates);
			}
		}

	}

}
