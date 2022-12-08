package org.pointerless.PetriNetCRN.containers.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.pointerless.PetriNetCRN.containers.State;

import java.io.IOException;

public final class StateSerializer extends StdSerializer<State> {

	public StateSerializer() {
		this(null);
	}

	public StateSerializer(Class<State> s) {
		super(s);
	}

	@Override
	public void serialize(State state, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeNumberField("time", state.getTime());
		jsonGenerator.writeNumberField("volume", state.getVolume());
		jsonGenerator.writeNumberField("repeatNum", state.getRepeatNum());
		for(String element : state.getState().keySet()){
			jsonGenerator.writeNumberField(element, state.getState().get(element));
		}
		jsonGenerator.writeEndObject();
	}
}