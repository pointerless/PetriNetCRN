package org.pointerless.PetriNetCRN;

import org.pointerless.PetriNetCRN.containers.State;


/**
 * OutputThread class, outputs the states as configured
 * in StateOutput.
 */
public class OutputThread implements Runnable{

	private final StateOutput stateOutput;

	public OutputThread(StateOutput stateOutput){
		this.stateOutput = stateOutput;
	}

	@Override
	public void run() {
		try {
			while (true) {
				State state = stateOutput.getQueue().take();
				if (state.isEnd()) {
					stateOutput.write(state);
					stateOutput.writeEnd();
					return;
				}
				stateOutput.write(state);
			}
		} catch (Exception e) {
			System.err.println("Thread interrupted: "+e.getMessage());
			Thread.currentThread().interrupt();
		}
	}
}
