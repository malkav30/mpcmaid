package com.mpcmaid.pgm.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;


import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.MultisampleBuilder;
import com.mpcmaid.pgm.Pad;
import com.mpcmaid.pgm.Program;
import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleMatrix;
import com.mpcmaid.pgm.MultisampleBuilder.Slot;
import com.mpcmaid.pgm.Sample.Status;

/**
 * Imports sample files to create a multisample program
 * 
 * @author cyrille martraire
 */
public class MultiSampleCommand extends ImportCommand {

	private static final Logger logger = System.getLogger(MultiSampleCommand.class.getName());

	private final List<Sample> samples = new ArrayList<>();

	private final Program pgm;

	public MultiSampleCommand(Status errorPolicy, List<File> files, Program pgm) {
		super(errorPolicy, files);
		this.pgm = pgm;
	}

	protected void addSample(Sample sample) {
		samples.add(sample);
	}

	public Collection<Pad> execute(SampleMatrix matrix) {
		// process raw files (rename, reject etc)
		importFiles();

		final MultisampleBuilder builder = new MultisampleBuilder();
		final Slot[] multisample = builder.assign(samples);

		rejectedCount += builder.getWarnings().size();

		// print
        for (Slot slot : multisample) {
            logger.log(Level.INFO, slot);
        }

		// assign
		final Collection<Pad> impactedPads = new ArrayList<>();
		for (int i = 0; i < multisample.length; i++) {
			Slot slot = multisample[i];
			if (slot != null) {
				final Pad pad = pgm.getPad(i);
				final Layer layer = pad.getLayer(0);
				final Sample sample = (Sample) slot.source();

				matrix.set(layer, sample);

				layer.setSampleName(sample.getSampleName());
				layer.setTuning(slot.tuning());
				pad.setPadMidiNote(slot.note());
				layer.setNoteOn();

				impactedPads.add(pad);
			}
		}

		return impactedPads;
	}

}
