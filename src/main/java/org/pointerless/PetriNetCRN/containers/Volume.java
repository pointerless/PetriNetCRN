package org.pointerless.PetriNetCRN.containers;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class Volume {

	private Long volume;


	private final List<Long> volumes;

	private Integer index;


	public Volume(Long volume){
		this.volume = volume;
		this.volumes = List.of(volume);
	}

	public Volume(List<Long> volumes) {
		this.volumes = volumes;
		this.volume = volumes.get(0);
		this.index = 0;
	}

	public Long getVolume() {
		return volume;
	}

	public boolean nextVolume() {
		this.index += 1;
		if (this.index >= this.volumes.size()) return false;
		this.volume = this.volumes.get(index);
		return true;
	}

	public Integer getIndex() {
		return this.index;
	}

	public List<Long> getVolumeRange() {
		return this.volumes;
	}

}
