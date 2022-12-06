package org.pointerless.PetriNetCRN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.State;
import org.pointerless.PetriNetCRN.containers.serialization.PetriNetDeserializer;
import org.pointerless.PetriNetCRN.containers.serialization.StateSerializer;

public class SerializationHelper {

	public static ObjectMapper getObjectMapper(){
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(PetriNet.class, new PetriNetDeserializer());
		module.addSerializer(State.class, new StateSerializer());
		objectMapper.registerModule(module);
		return objectMapper;
	}

	public static void registerWithObjectMapper(ObjectMapper objectMapper) {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(PetriNet.class, new PetriNetDeserializer());
		module.addSerializer(State.class, new StateSerializer());
		objectMapper.registerModule(module);
	}

}
