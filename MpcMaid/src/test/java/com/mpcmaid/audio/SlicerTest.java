package com.mpcmaid.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger.Level;
import java.lang.System.Logger;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import junit.framework.TestCase;

public class SlicerTest extends TestCase {

	private static final Logger logger = System.getLogger(SlicerTest.class.getName());

	private static final int AVERAGE_ENERGY_WINDOW = 43;

	private static final int OVERLAP_RATIO = 1;

	private static final int WINDOW_SIZE = 1024;

	private static final int MIDI_PPQ = 96;

	protected void setUp() throws Exception {
		InputStream is = getClass().getResourceAsStream("myLoop.WAV");
		InputStream bis = new BufferedInputStream(is);
		final Sample sample = Sample.open(bis);

		slicer = new Slicer(sample, WINDOW_SIZE, OVERLAP_RATIO, AVERAGE_ENERGY_WINDOW);
		slicer.extractMarkers();
		markers = slicer.getMarkers();
		logger.log(Level.INFO, slicer.toString());
	}

	private Slicer slicer;

	private Markers markers;

	public void testMarkers() throws IOException {
		assertEquals("Slicer: 3.8424037s (169450 samples), 9 markers", slicer.toString());
		logger.log(Level.INFO, markers.toString());

		assertEquals(9, markers.size());

		assertFalse(markers.isUnset());

		Marker marker = markers.getSelectedMarker();
		assertEquals(0, marker.getLocation());
		logger.log(Level.INFO, marker.toString());

		markers.selectMarker(4);
		assertEquals(4, markers.getSelectedMarkerIndex());
		assertEquals(79872, markers.getSelectedMarkerLocation());
		assertEquals(79872, markers.getSelectedMarker().getLocation());

		assertEquals(0, markers.getRangeFrom(0).from());

		final LocationRange range3 = markers.getRangeFrom(3);
		logger.log(Level.INFO, range3.toString());
		final LocationRange range4 = markers.getRangeFrom(4);
		logger.log(Level.INFO, range4.toString());

		assertEquals(3.8424037, markers.getDuration(), 0.000001);
		assertEquals(124.92, markers.getTempo(), 0.01);

		// remove
		markers.deleteSelectedMarker();
		assertEquals(8, markers.size());
		assertEquals(3, markers.getSelectedMarkerIndex());

		final LocationRange range3bis = markers.getRangeFrom(3);
		assertEquals(range3.from(), range3bis.from());
		assertEquals(range4.to(), range3bis.to());

		// last range extends up to the Frame Length
		final LocationRange range7 = markers.getRangeFrom(7);
		assertEquals(169450, range7.to());
		logger.log(Level.INFO, range7.toString());

		final int[] midiTicks = { 0, 32, 97, 129, 190, 222, 288, 320, 381, 413, 478, 510, 575, 607, 673, 705, 705 };
		final Sequence midiSequence = markers.exportMidiSequence(null, MIDI_PPQ);
		final Track track = midiSequence.getTracks()[0];
		assertEquals(17, track.size());
		for (int i = 0; i < track.size(); i++) {
			// logger.log(Level.INFO, track.get(i).getTick());
			assertEquals(midiTicks[i], track.get(i).getTick());
		}

		assertEquals(3, markers.getSelectedMarkerIndex());
		markers.insertMarker();
		assertEquals(4, markers.getSelectedMarkerIndex());
		assertEquals(73727, markers.getLocation(4));
	}

}
