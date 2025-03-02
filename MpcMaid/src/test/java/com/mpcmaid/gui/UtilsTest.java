package com.mpcmaid.gui;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {

	public static String escapeSampleName(final String name, int length) {
		final String noExt = Utils.noExtension(name);

        return Utils.escapeName(noExt, length, false);
	}

	public void testEscapeName() {
		assertEquals("123456789123456", escapeSampleName("123456789123456", 16));

		assertEquals("1", escapeSampleName("1", 16));

		assertEquals("1234567890123456", escapeSampleName("1234567890123456.", 16));
		assertEquals("1234567890123456", escapeSampleName("1234567890123456.wav", 16));
		assertEquals("123456789012345", escapeSampleName("123456789012345.abcd.wav", 16));
		assertEquals("123456789012345", escapeSampleName("123456789012345", 16));
		assertEquals("123456789012345 ", escapeSampleName("123456789012345 ", 16));
		assertEquals("123456789012345", escapeSampleName("123456789012345.", 16));

		assertEquals("1234567890123456", escapeSampleName("123456789012345678.wav", 16));
		assertEquals("123456789012345", escapeSampleName("123456789012345_78.wav", 16));
	}

	public void testPathToListing() {
		assertEquals("12345678901234", escapeSampleName("1234567890123456", 14));

		assertEquals("1", escapeSampleName("1", 14));

		assertEquals("1234567890123", escapeSampleName("1234567890123.", 14));
		assertEquals("123456789012", escapeSampleName("123456789012", 13));
		assertEquals("123456789012 ", escapeSampleName("123456789012 ", 13));
		assertEquals("123456789012_", escapeSampleName("123456789012_", 13));
		assertEquals("123456789012", escapeSampleName("123456789012 4", 13));
		assertEquals("123456789012", escapeSampleName("123456789012.4", 13));

		assertEquals("1234567890123", escapeSampleName("1234567890123.12345", 14));
		assertEquals("1234567890123", escapeSampleName("1234567890123_13245", 14));
	}

	public void testMultipleRenaming() {
		final String[] names = { "TicTacShutUp_click_1_d.wav", "TicTacShutUp_click_1_off_click.wav",
				"TicTacShutUp_light_1.wav" };
		for (int j = 0; j < 4; j++) {
            for (String name : names) {
                final String noExt = Utils.noExtension(name);
                Utils.escapeName(noExt, 16, true);
            }
		}
	}

}
