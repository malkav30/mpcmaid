package com.mpcmaid.pgm;

/**
 * Represents the profile of an MPC
 *
 * @author cyrille martraire
 */
public record Profile(String name, int rowNumber, int colNumber, int sliderNumber, int filterNumber) {

	public final static Profile MPC500 = new Profile("MPC500", 4, 3, 1, 1);

	public final static Profile MPC1000 = new Profile("MPC1000", 4, 4, 2, 2);


	public int getPadNumber() {
		return rowNumber * colNumber;
	}


	public String toString() {
		return "Profile " + name;
	}

	public static Profile getProfile(String name) {
		return "MPC1000".equalsIgnoreCase(name) ? MPC1000 : MPC500;
	}

}
