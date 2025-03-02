package com.mpcmaid.pgm;

import junit.framework.TestCase;

import java.io.File;
import java.lang.System.Logger.Level;
import java.lang.System.Logger;

public class ProgramTest extends TestCase {

	private static final Logger logger = System.getLogger(ProgramTest.class.getName());

	public void testRead() {
		Program pgm = Program.open(getClass().getResourceAsStream("test.pgm"));

		// read sample name
		Layer padLayer = pgm.getPad(4).getLayer(0);
		padLayer.getSampleName();

		final int padNumber = pgm.getPadNumber();
		for (int i = 0; i < padNumber; i++) {
			final Pad pad = pgm.getPad(i);
			logger.log(Level.INFO, pad + " fxSendLevel=" + pad.getMixer().get(PadMixer.FX_SEND_LEVEL) + " note: "
					+ pad.getPadMidiNote());

            final int sampleNumber = Pad.LAYER_NUMBER;
			for (int j = 0; j < sampleNumber; j++) {
				final Layer sample = pad.getLayer(j);

				final String playModeLabel = (sample.isOneShot()) ? "One Shot" : "Note On";
				logger.log(Level.INFO, sample + ": " + sample.getSampleName() + " level=" + sample.getLevel()
						+ " playMode=" + playModeLabel + " tuning=" + sample.getTuning());
			}

		}

		// change the tuning
		assertEquals(0, padLayer.getTuning(), 0);
		padLayer.setTuning(-0.5);
		final String sampleName = "tomlow";
		padLayer.setSampleName(sampleName);
		final String sampleName2 = padLayer.getSampleName();
		logger.log(Level.INFO, sampleName + "--" + sampleName2 + "--");
		assertEquals(sampleName.trim(), sampleName2);
		assertEquals(-0.5, padLayer.getTuning(), 0);

		final Slider slider1 = pgm.getSlider(0);
		slider1.setSliderParameter(4);
		assertEquals(new Range(9, 99), slider1.get(Slider.ATTACK_RANGE));
		slider1.set(Slider.ATTACK_RANGE, new Range(22, 78));// attack
		assertEquals(new Range(22, 78), slider1.get(Slider.ATTACK_RANGE));

		// save
		pgm.save(new File("test_midified.PGM"));
	}
}
