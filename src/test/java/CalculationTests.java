import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pointerless.PetriNetCRN.containers.Place;
import org.pointerless.PetriNetCRN.containers.Transition;
import org.pointerless.PetriNetCRN.containers.Volume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CalculationTests {

	private static ArrayList<Place> places = new ArrayList<>();

	@BeforeAll
	public static void init(){
		places.add(new Place("X", 0.5));
		places.add(new Place("Y", 0.5));
		places.add(new Place("O", 0.0));
	}

	@Test
	public void transitionTest(){
		HashMap<Place, Long> input = new HashMap<>();
		HashMap<Place, Long> output = new HashMap<>();
		Volume volume = new Volume(100L);
		places.forEach(place -> {
			place.updateContainsFromVolume(volume);
			switch (place.getElement()) {
				case "X", "Y" -> input.put(place, 1L);
				case "O" -> output.put(place, 1L);
			}
		});

		Transition t = new Transition(input, output, 1.0);
		Random random = new Random();

		t.updateTimeRemaining(0.0, volume, random);
		System.out.println(t.getC());
		System.out.println(t.getPropensity());
		System.out.println(t.getNextOccurrence());
	}


}
