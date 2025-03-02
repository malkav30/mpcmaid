package com.mpcmaid.audio;

/**
 * Represents a location range
 *
 * @author cyrille martraire
 */
public record LocationRange(int from, int to) {


	public int getMidLocation() {
		return (from + to) / 2;
	}

	public String toString() {
		return "LocationRange [" + from + " - " + to + "]";
	}

}
