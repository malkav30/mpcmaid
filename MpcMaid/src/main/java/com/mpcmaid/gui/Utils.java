package com.mpcmaid.gui;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public class Utils {

	private static final Logger logger = System.getLogger(Utils.class.getName());

	public static final String EXTENSION = ".WAV";

	/**
	 * @return true if the given file has the expected extension
	 */
	public static boolean hasCorrectExtension(final File file) {
		return file.getName().toUpperCase().endsWith(EXTENSION);
	}

	public static String noExtension(final File file) {
		return noExtension(file.getName());
	}

	/**
	 * @return The given name without its extension (the term after the last dot)
	 */
	public static String noExtension(final String name) {
        final int indexOf = name.lastIndexOf('.');
		if (indexOf != -1) {
			return name.substring(0, indexOf);
		}
		return name;
	}

	/**
	 * Shorten the name if it is too long, then ensure its unicity thanks to a
	 * postfix incremental number
	 */
	public static String escapeName(final String name, final int length, final boolean brutal, final int renameCount) {
		String escaped = escapeName(name, length, brutal);
		if (renameCount != -1 && !escaped.equals(name)) {
			if (renameCount > 0) {
				final String postfix = String.valueOf(renameCount);
				if (escaped.length() + postfix.length() <= length) {
					escaped = escaped + postfix;
				} else {
					escaped = escaped.substring(0, length - postfix.length()) + postfix;
				}
			}
		}
		return escaped;
	}

	/**
	 * Shorten the name if it is too long, either brutally, or with some trim so
	 * that it does not end on a space, underscore or dot
	 */
	public static String escapeName(final String name, final int length, final boolean brutal) {
		String s = name;
		if (s.length() <= length) {
			return s;
		}
		s = s.substring(0, length).trim();
		if (brutal) {
			return s;
		}
		String s2;
		while (true) {
			s2 = escapeEnding(s);
			if (s.equals(s2)) {
				return s.trim();
			}
			s = s2;
		}
	}

	private static String escapeEnding(final String s) {
		if (s.endsWith(".")) {
			return s.substring(0, s.length() - 1);
		}
		if (s.endsWith(" ")) {
			return s.substring(0, s.length() - 1);
		}
		if (s.endsWith("_")) {
			return s.substring(0, s.length() - 1);
		}
		return s;
	}

	// File utils
	public static void copy(final File src, final File dst) throws IOException {
		final InputStream in = Files.newInputStream(src.toPath());
		final OutputStream out = Files.newOutputStream(dst.toPath());

		// Transfer bytes from in to out
		final byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public String toString() {
		return "Utils: ";
	}

	/**
	 * Donwsamples a sample to fit the 16 bits, 44.1kHz requirements of the MPC. In
	 * Jave, this means 44100, pcm_s16le or pcm_u8
	 * 
	 * @param src the source file to convert
	 * @param dst the destination file to write
     */
	public static void resample(final File src, final File dst) throws IOException {
		try (FileInputStream in = new FileInputStream(src); BufferedInputStream bis = new BufferedInputStream(in)){
			AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
			AudioFormat oldFormat = ais.getFormat();
			AudioFormat mpcFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, oldFormat.getChannels(), 2*oldFormat.getChannels(), 44100, false);
			if (isWrongFormat(ais)) {
				logger.log(Level.INFO, "Need to convert sample "+src.getName());
				AudioInputStream converted = AudioSystem.getAudioInputStream(mpcFormat, ais);
				AudioSystem.write(converted, AudioFileFormat.Type.WAVE, dst);
			} else {
				// no need to convert, we copy
				Utils.copy(src, dst);
			}
        } catch (UnsupportedAudioFileException e) {
			// by default, we copy on wrong format error
			Utils.copy(src, dst);
			throw new IOException("Got unsupported audio file, it was copied without modification !");
		}
	}

	/** Returns true if the old format is "less or equal" than the good format, i.e. has lower or equal bit depth and lower or equal samplerate */
	private static boolean isWrongFormat(AudioInputStream stream) {
		return stream.getFormat().getSampleRate() > 44100 || stream.getFormat().getSampleSizeInBits() > 16;
	}

}
