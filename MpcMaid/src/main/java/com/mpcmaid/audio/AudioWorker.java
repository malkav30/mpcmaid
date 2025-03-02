package com.mpcmaid.audio;

import javax.sound.sampled.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.lang.System.Logger.Level;
import java.lang.System.Logger;

/**
 * A sample-playing thread which uses single dataline to play samples.
 */
public class AudioWorker extends Thread {

	private final BlockingQueue<Sample> clipQueue;

	private static final Logger logger = System.getLogger(AudioWorker.class.getName());

	AudioWorker(BlockingQueue<Sample> queue) {
		this.clipQueue = queue;
	}

	/**
	 * Plays the queued clips, closing the
	 * data line if no new AudioClips are fetched within a certain (short)
	 * period of time.
	 */
	public void run() {
		SourceDataLine dataLine = null;
        //noinspection
        while (true) {
			try {
				Sample sample;
				try {
					sample = clipQueue.poll(5, TimeUnit.SECONDS);
					if (sample == null) {
						if (dataLine != null && dataLine.isOpen() && !dataLine.isRunning()) {
							dataLine.close();
							dataLine = null;
						}
						continue;
					}
				} catch (InterruptedException e) {
					if (dataLine != null && dataLine.isOpen() && !dataLine.isRunning()) {
						dataLine.close();
						dataLine = null;
					}
					continue;
				}
				AudioFormat format = sample.format();
				if (dataLine == null) {
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					dataLine = (SourceDataLine) AudioSystem.getLine(info);
				}

				if (!format.matches(dataLine.getFormat())) {
					dataLine.close();
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					dataLine = (SourceDataLine) AudioSystem.getLine(info);
				}

				if (!dataLine.isOpen())
					dataLine.open(format);

				if (!dataLine.isRunning())
					dataLine.start();

				dataLine.write(sample.bytes(), 0, sample.bytes().length);
				//dataLine.close();
			} catch (LineUnavailableException | IllegalArgumentException e) {
				logger.log(Level.ERROR, e::getMessage, e);
			}
		}
	}
}
