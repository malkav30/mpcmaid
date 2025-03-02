package com.mpcmaid.midi;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.lang.System.Logger;

/**
 * A builder to create a MIDI sequence that can be played or exported to file.
 * 
 * @author cyrille martraire
 */
public final class MidiSequenceBuilder {

	private static final Logger logger = System.getLogger(MidiSequenceBuilder.class.getName());

	private Sequence sequence;

	public MidiSequenceBuilder(final int ppq) {
		try {
			sequence = new Sequence(Sequence.PPQ, ppq);
		} catch (InvalidMidiDataException e) {
			logger.log(Level.ERROR, e::getMessage, e);
		}
	}

	public Track getTrack() {
		return sequence.createTrack();
	}

	final int channel = 0;

	final int noteLength = 64; // quarter note

	final int velocity = 70;

	final int key = 60;

	public void addNote(Track track, int startTick, int tickLength, int key) {
		addNote(track, 0, startTick, tickLength, key, 127);
	}

	public void addNote(Track track, int channel, int startTick, int tickLength, int key, int velocity) {
		try {
			final ShortMessage on = new ShortMessage();
			on.setMessage(ShortMessage.NOTE_ON, channel, key, velocity);
			final ShortMessage off = new ShortMessage();
			off.setMessage(ShortMessage.NOTE_OFF, channel, key, velocity);
			track.add(new MidiEvent(on, startTick));
			track.add(new MidiEvent(off, startTick + tickLength));
		} catch (InvalidMidiDataException e) {
			logger.log(Level.ERROR, e::getMessage, e);
		}
	}

	public Sequence getSequence() {
		return sequence;
	}

	public void save(File file) throws IOException {
		MidiSystem.write(sequence, 0, file);
	}

}
