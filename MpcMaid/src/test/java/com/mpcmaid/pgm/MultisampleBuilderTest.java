package com.mpcmaid.pgm;

import java.io.File;
import java.lang.System.*;
import java.lang.System.Logger.*;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.mpcmaid.pgm.MultisampleBuilder.Slot;

public class MultisampleBuilderTest extends TestCase {


	public final static Logger logger = System.getLogger(MultisampleBuilderTest.class.getName());

	public void testNoteName() {
		final List<String> filenames = collectFileNames(1);

		final int commonIndex = MultisampleBuilder.longestPrefix(filenames);
		// logger.log(Level.INFO, commonIndex);

		int i = 35;
        for (String word : filenames) {
            final String variablePart = word.substring(commonIndex);
            final int note = MultisampleBuilder.extractNote(variablePart);
            // logger.log(Level.INFO, note);
            assertEquals(i++, note);
        }
	}

	public void testExtractNote() {
		final int note = MultisampleBuilder.extractNote("ROH 40 E 1");
		assertEquals(40, note);
	}

	public void testAssignment() {
		final List<String> filenames = collectFileNames(4);
		final List<Sample> samples = toSamples(filenames);

		final MultisampleBuilder builder = new MultisampleBuilder();
		final Slot[] multisample = builder.assign(samples);

        for (Slot slot : multisample) {
            logger.log(Level.INFO, slot);
        }
	}

	private static List<String> collectFileNames(int step) {
		final List<String> list = new ArrayList<>();
		for (int i = 0; i < 64; i += step) {
			final int k = 35 + i;
			final String noteName = MultisampleBuilder.noteName(k);

			final String spacer = noteName.length() == 2 ? " " : "";
			final String fileName = "WLS C" + k + noteName + (spacer) + "#" + i;
			// logger.log(Level.INFO, fileName);
			list.add(fileName);
			final int note = MultisampleBuilder.extractNote(fileName);
			assertEquals(k, note);
		}
		return list;
	}

	protected static List<Sample> toSamples(final List<String> fileNames) {
		final List<Sample> list = new ArrayList<>();
        for (String fileName : fileNames) {
            final Sample sample = toSample(fileName);
            logger.log(Level.INFO, sample);
            list.add(sample);
        }
		return list;
	}

	protected static Sample toSample(final String fileName) {
		return Sample.importFile(new File(fileName + ".WAV"), 16, Sample.OK, false, 0);
	}
}
