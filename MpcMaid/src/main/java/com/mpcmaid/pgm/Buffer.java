package com.mpcmaid.pgm;

public interface Buffer {

	String getString(final int offset);

	void setString(final int offset, String string);

	short getShort(final int index);

	void setShort(final int index, final short value);

	byte getByte(final int index);

	void setByte(final int index, final int value);

	Range getRange(final int index);

	void setRange(final int index, final Range value);

}