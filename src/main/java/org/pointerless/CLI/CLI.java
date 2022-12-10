package org.pointerless.CLI;


import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pointerless.PetriNetCRN.PetriNetExecutor;
import org.pointerless.PetriNetCRN.SerializationHelper;
import org.pointerless.PetriNetCRN.StateOutput;
import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.Volume;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CLI {

	public static class Args {

		private static class VolumeListConverter implements IStringConverter<List<Long>> {

			@Override
			public List<Long> convert(String s) {
				String[] volumeStrings = s.split(",");
				List<Long> out = new ArrayList<>();
				for(String volume : volumeStrings){
					out.add(Long.valueOf(volume));
				}
				return out;
			}
		}

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

		@Parameter(names = {"--volumes", "-v"}, description = "Override volumes in file", listConverter = VolumeListConverter.class)
		private List<Long> volumes = null;
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

		String JSONNet = args.crnData;
		if(!args.crnPath.isEmpty()) {
			try {
				JSONNet = String.join("\n", Files.readAllLines(Path.of(args.crnPath)));
			}catch(IOException e){
				System.err.println("ERROR: Could not read '" + args.crnPath + "'");
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}

		ObjectMapper objectMapper = SerializationHelper.getObjectMapper();
		PetriNet petriNet = null;
		try {
			petriNet = objectMapper.readValue(JSONNet, PetriNet.class);
			if(args.volumes != null){
				petriNet.setVolume(new Volume(args.volumes));
			}
		}catch (Exception e){
			System.err.println("ERROR: Could not deserialize");
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		StateOutput stateOutput = null;

		if(!args.outputJSON.isEmpty()) {
			try {
				File outputFile = new File(args.outputJSON);
				if (!outputFile.canWrite()) {
					throw new IllegalArgumentException("Cannot write to file '" + args.outputJSON + "'");
				}
				FileOutputStream outputFileStream = new FileOutputStream(outputFile);
				stateOutput = new StateOutput();
				stateOutput.jsonOutput(new PrintStream(outputFileStream, true));
			} catch (Exception e) {
				System.err.println("Could not open output file: " + e.getMessage());
				System.exit(-1);
			}
		}else if(!args.outputCSV.isEmpty()){
			try {
				File outputFile = new File(args.outputCSV);
				if (!outputFile.canWrite()) {
					throw new IllegalArgumentException("Cannot write to file '" + args.outputCSV + "'");
				}
				FileOutputStream outputFileStream = new FileOutputStream(outputFile);
				stateOutput = new StateOutput();
				stateOutput.csvOutput(new PrintStream(outputFileStream, true));
			} catch (Exception e) {
				System.err.println("Could not open output file: " + e.getMessage());
				System.exit(-1);
			}
		}else{
			stateOutput = new StateOutput();
			stateOutput.jsonOutput(System.out);
		}

		PetriNetExecutor petriNetExecutor = new PetriNetExecutor(petriNet, args.repeats, stateOutput);

		boolean fired = true;
		while(fired) {
			try {
				fired = petriNetExecutor.step(random, args.tMax);
			} catch (IOException e) {
				System.err.println("Could not run step: "+e.getMessage());
				System.exit(-1);
			}
		}

	}

}
