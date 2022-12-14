package org.pointerless.PetriNetCRN.containers.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.pointerless.PetriNetCRN.containers.PetriNet;
import org.pointerless.PetriNetCRN.containers.Place;
import org.pointerless.PetriNetCRN.containers.Transition;
import org.pointerless.PetriNetCRN.containers.Volume;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * PetriNetDeserializer class, creates PetriNet and all other
 * components from json. Use SerializationHelper.getObjectMapper()
 * to get an object mapper with this module loaded.
 */
public class PetriNetDeserializer extends StdDeserializer<PetriNet> {

	public PetriNetDeserializer(){
		this(null);
	}

	public PetriNetDeserializer(Class<?> vc){
		super(vc);
	}

	@Override
	public PetriNet deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		Volume volume;
		if(node.get("volume").isLong() || node.get("volume").isNumber())
			volume = new Volume(node.get("volume").asLong());
		else if(node.get("volume").isArray()) {
			try {
				List<Long> volumes = new ArrayList<>();
				for(final JsonNode item : node.get("volume")){
					volumes.add(item.asLong());
				}
				volume = new Volume(volumes);
			}catch(Exception e){
				throw new IOException("Could not evaluate volume array: "+e.getMessage());
			}
		}else{
			throw new IOException("Volume is of unusable type: '"+node.get("volume").getNodeType().toString()+"'");
		}

		ArrayList<Place> places = new ArrayList<>();
		HashMap<String, Place> placeLookup = new HashMap<>();
		for(JsonNode place : node.get("places")){
			Place newPlace;
			if(place.has("volumeRatio") && place.get("volumeRatio").isNumber()){
				newPlace = new Place(place.get("element").asText(), place.get("volumeRatio").asDouble());
			}else if(place.has("generator") && place.get("generator").isTextual()){
				newPlace = new Place(place.get("element").asText(), place.get("generator").asText());
			}else{
				throw new IOException("No system of generating place quantity from volume for place '"
						+place.get("element").asText()+"'");
			}
			newPlace.updateContainsFromVolume(volume);
			places.add(newPlace);
			placeLookup.put(newPlace.getElement(), newPlace);
		}

		Place consensusElement = null;
		if(node.has("consensusElement") && node.get("consensusElement").isTextual()){
			if(!placeLookup.containsKey(node.get("consensusElement").asText())){
				throw new IOException("Consensus element '"+node.get("consensusElement").asText()+"' not in places");
			}
			consensusElement = placeLookup.get(node.get("consensusElement").asText());
		}else if(node.has("consensusElement")){
			throw new IOException("Invalid consensus element");
		}

		ArrayList<Transition> transitions = new ArrayList<>();
		for(JsonNode transition : node.get("transitions")){
			HashMap<Place, Long> inputPlaces = new HashMap<>();
			HashMap<Place, Long> outputPlaces = new HashMap<>();
			for(JsonNode iPlace : transition.get("inputPlaces")){
				String elementName = iPlace.get("element").asText();
				if(!placeLookup.containsKey(elementName)){
					throw new IOException("Place for '"+elementName+"' could not be found");
				}
				inputPlaces.put(placeLookup.get(elementName), iPlace.get("amount").longValue());
			}
			for(JsonNode oPlace : transition.get("outputPlaces")){
				String elementName = oPlace.get("element").asText();
				if(!placeLookup.containsKey(elementName)){
					throw new IOException("Place for '"+elementName+"' could not be found");
				}
				outputPlaces.put(placeLookup.get(elementName), oPlace.get("amount").longValue());
			}
			transitions.add(new Transition(inputPlaces, outputPlaces, transition.get("c").asDouble()));
		}
		return new PetriNet(places, transitions, volume, consensusElement);
	}
}
