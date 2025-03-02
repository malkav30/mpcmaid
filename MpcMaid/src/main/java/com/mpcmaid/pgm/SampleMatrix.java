package com.mpcmaid.pgm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a matrix of sample (pads, layers).
 * 
 * @see SampleCommand Commands operate on this matrix
 * 
 * @author cyrille martraire
 */
public class SampleMatrix {

	private final Sample[][] cells = new Sample[64][4];

	public void set(final Layer layer, final Sample sample) {
		cells[layer.getPad().getElementIndex()][layer.getElementIndex()] = sample;
	}

	public Sample get(final Layer layer) {
		return cells[layer.getPad().getElementIndex()][layer.getElementIndex()];
	}

	public int size() {
		int size = 0;
        for (final Sample[] layers : cells) {
            for (Sample layer : layers) {
                if (layer != null) {
                    size++;
                }
            }
        }
		return size;
	}

	public void clear() {
        for (final Sample[] layers : cells) {
            Arrays.fill(layers, null);
        }
	}

	public List<Sample> collectAll() {
		final List<Sample> list = new ArrayList<>();
        for (final Sample[] layers : cells) {
            for (final Sample sample : layers) {
                if (sample != null) {
                    list.add(sample);
                }
            }
        }
		return list;
	}

	public String toString() {
		return "SampleMatrix: " + size() + " samples";
	}

}
