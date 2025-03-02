package com.mpcmaid.pgm.command;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mpcmaid.gui.BaseFrame;
import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleCommand;
import com.mpcmaid.pgm.SampleMatrix;

/**
 * Exports every sample file into the destination directory.
 * 
 * @author cyrille martraire
 */
public final class ExportCommand implements SampleCommand {

	private static final Logger logger = Logger.getLogger(ExportCommand.class.getName());

	private final File dir;

	private int exported = 0;

	private int expected = 0;

	public ExportCommand(File dir) {
		this.dir = dir;
	}

	public Object execute(SampleMatrix matrix) {
        for (Sample sample : matrix.collectAll()) {
            expected++;
            try {
                sample.convertTo(dir);
                exported++;
            } catch (IOException e) {
                logger.log(Level.SEVERE, e, e::getMessage);
            }
        }
		return getReport();
	}

	public Object getReport() {
		if (hasError()) {
			return "Exported " + exported + " sample files out of " + expected + " (invalid files or files not found";
		}
		return "Exported every " + exported + " sample files successfully";
	}

	public boolean hasError() {
		return exported != expected;
	}

}
