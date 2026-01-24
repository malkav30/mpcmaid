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
	 * FIXME: this may be enhanced further using true multithreading, because as for now the sample playback is barely
	 * usable, being very slow to trigger
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
						closeUnusedDataline(dataLine);
						continue;
					}
				} catch (InterruptedException e) {
					closeUnusedDataline(dataLine);
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

	private void closeUnusedDataline(SourceDataLine dataLine) {
		if (dataLine != null && dataLine.isOpen() && !dataLine.isRunning()) {
			dataLine.close(); // FIXME we never go there, dataline is always running
		}
	}
}
