package com.mpcmaid.pgm;

/**
 * Represents a range of values [low...high]
 *
 * @author cyrille martraire
 */
public record Range(int low, int high) {


	public boolean isReversed() {
		return high < low;
	}

	public Range reverse() {
		return new Range(high, low);
	}

	public boolean contains(final double value) {
		return low <= value && value <= high;
	}

	/**
	 * @return true if this Range is equal to the given Range
	 */
	public boolean equals(Object arg0) { //FIXME probably useless in a record
		if (!(arg0 instanceof Range other)) {
			return false;
		}
		if (this == other) {
			return true;
		}
		return other.low == low && other.high == high;
	}

	public String toString() {
		return low + ".." + high;
	}
}