import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.serialization.PetriNetDeserializer;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SerializeTests{

	@Test
	void serialize() throws NullPointerException, IOException {
		String resourceName = "examplenet.json";

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
		String absolutePath = file.getAbsolutePath();

		System.out.println("Got "+resourceName+" at "+absolutePath);

		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(PetriNet.class, new PetriNetDeserializer());
		objectMapper.registerModule(module);

		PetriNet petriNet = objectMapper.readValue(file, PetriNet.class);

		assert(petriNet != null);

		System.out.println(objectMapper.writeValueAsString(petriNet));
	}
}
