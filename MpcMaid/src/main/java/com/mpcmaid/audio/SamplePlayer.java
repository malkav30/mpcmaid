package com.mpcmaid.audio;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.lang.System.Logger.Level;
import java.lang.System.Logger;

/**
 * A sample player which employs limited number of threads to play clips. Each
 * thread creates its own dataLine.
 *
 * @pattern Singleton We only need one sample player for every window, so that
 *          to control the overall polyphony.
 *
 */
public final class SamplePlayer {

	private final static Logger logger = System.getLogger(SamplePlayer.class.getName());

	private final static SamplePlayer INSTANCE = new SamplePlayer();

	private final static BlockingQueue<Sample> queue = new ArrayBlockingQueue<>(1);

	static {
		// only six sounds can be heard at once
		int numWorkers = 6;
		AudioWorker[] workers = new AudioWorker[numWorkers];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new AudioWorker(queue);
			workers[i].start();
		}
	}

	public static SamplePlayer getInstance() {
		return INSTANCE;
	}

	public void play(Sample sample) {
		if (queue.isEmpty())
			queue.add(sample);
	}

	public void play(File file) {
		try {
			if (queue.isEmpty())
				queue.add(Sample.open(file));
		} catch (Exception e) {
			logger.log(Level.ERROR, e::getMessage, e);
		}
	}

	public String toString() {
		return "SamplePlayer ";
	}
}
