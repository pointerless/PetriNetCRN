import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pointerless.PetriNetCRN.SerializationHelper;
import org.pointerless.PetriNetCRN.containers.*;
import org.pointerless.PetriNetCRN.PetriNetExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ExecutionTests {

	private static ObjectMapper objectMapper;
	private static File exampleNetFile;

	private static File net2File;

	@BeforeAll
	public static void init(){
		objectMapper = SerializationHelper.getObjectMapper();

		String resourceName = "examplenet.json";
		ClassLoader classLoader = ExecutionTests.class.getClassLoader();
		exampleNetFile = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

		resourceName = "net2.json";
		net2File = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
	}

	@Test
	void queueTest() throws IOException {
		PetriNet petriNet = objectMapper.readValue(exampleNetFile, PetriNet.class);

		TransitionPriorityQueue q = petriNet.getTransitions();
		ArrayList<Place> places = petriNet.getPlaces();
		Random random = new Random();
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
		q.actionNext(1.25, random);
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
		q.actionNext(1.25, random);
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
	}

	@Test
	void executionTest() throws IOException {
		PetriNet petriNet = objectMapper.readValue(exampleNetFile, PetriNet.class);
		PetriNetExecutor petriNetExecutor = new PetriNetExecutor(petriNet, 0.1);
		Random random = new Random();

		boolean fired = true;
		while(fired) {
			fired = petriNetExecutor.step(random);
		}

		String fileName = "/home/harry/IdeaProjects/crn-attempt1/graphing/out" + (System.currentTimeMillis() / 1000L);

		System.out.println("Outputting to: "+fileName+".csv");

		PetriNetExecutor.writeStateHistoryToCSV(new File(fileName+".csv"), petriNetExecutor.getStateHistory());

		System.out.println("Outputting to: "+fileName+".json");

		objectMapper.writeValue(new File(fileName+".json"), petriNetExecutor.getStateHistory());

	}

}
