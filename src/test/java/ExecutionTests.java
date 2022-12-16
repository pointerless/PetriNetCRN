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

	private static File doubleBFile;

	private static File triFile;

	@BeforeAll
	public static void init(){
		objectMapper = SerializationHelper.getObjectMapper();

		String resourceName = "examplenet.json";
		ClassLoader classLoader = ExecutionTests.class.getClassLoader();
		exampleNetFile = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

		resourceName = "tri-molecular.json";
		triFile = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

		resourceName = "double-b.json";
		doubleBFile = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
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
		Double t = q.actionNext(0.0, petriNet.getVolume(), random);
		System.out.println("T="+t.toString());
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
		t = q.actionNext(t, petriNet.getVolume(), random);
		System.out.println("T="+t.toString());
		q.stream().forEach(System.out::println);
		places.forEach(System.out::print);
		System.out.println();
	}

	@Test
	void executionTest() throws IOException, InterruptedException {
		PetriNet petriNet = objectMapper.readValue(exampleNetFile, PetriNet.class);
		StateOutput stateOutput = new StateOutput();
		stateOutput.csvOutput(System.out);
		PetriNetExecutor petriNetExecutor = new PetriNetExecutor(petriNet, 1, stateOutput);
		Random random = new Random();

		boolean fired = true;
		while(fired) {
			fired = petriNetExecutor.step(random, Double.POSITIVE_INFINITY);
		}

		assert(!petriNet.canFire());
		petriNet.getPlaces().forEach(place -> {
			switch (place.getElement()) {
				case "A" -> {
					assert(!place.getContains().equals(0L));
				}
				case "B" -> {
					assert(place.getContains().equals(0L) || place.getContains().equals(1L));
				}
				case "C" ->{
					assert(place.getContains().equals(0L));
				}
			}
		});

	}

}
