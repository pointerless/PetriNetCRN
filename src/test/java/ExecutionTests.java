import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pointerless.PetriNetCRN.SerializationHelper;
import org.pointerless.PetriNetCRN.StateOutput;
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

	private static File triFile;

	@BeforeAll
	public static void init(){
		objectMapper = SerializationHelper.getObjectMapper();

		String resourceName = "examplenet.json";
		ClassLoader classLoader = ExecutionTests.class.getClassLoader();
		exampleNetFile = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

		resourceName = "net2.json";
		net2File = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

		resourceName = "tri-molecular.json";
		triFile = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
	}

	@Test
	void queueTest() throws IOException {
		PetriNet petriNet = objectMapper.readValue(triFile, PetriNet.class);

		TransitionPriorityQueue q = petriNet.getTransitions();
		ArrayList<Place> places = petriNet.getPlaces();
		Random random = new Random();
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
		q.actionNext(1.25, petriNet.getVolume(), random);
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
		q.actionNext(1.25, petriNet.getVolume(), random);
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
	}

	@Test
	void executionTest() throws IOException {
		PetriNet petriNet = objectMapper.readValue(triFile, PetriNet.class);
		StateOutput stateOutput = new StateOutput();
		stateOutput.csvOutput(System.out);
		PetriNetExecutor petriNetExecutor = new PetriNetExecutor(petriNet, 1, stateOutput);
		Random random = new Random();

		boolean fired = true;
		while(fired) {
			fired = petriNetExecutor.step(random, 1000.0);
		}
	}

}
