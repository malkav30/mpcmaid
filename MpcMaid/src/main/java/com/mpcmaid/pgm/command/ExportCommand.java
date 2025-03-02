package com.mpcmaid.pgm.command;

import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleCommand;
import com.mpcmaid.pgm.SampleMatrix;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.lang.System.Logger;

/**
 * Exports every sample file into the destination directory.
 * 
 * @author cyrille martraire
 */
public final class ExportCommand implements SampleCommand {

	private static final Logger logger = System.getLogger(ExportCommand.class.getName());

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
                logger.log(Level.ERROR, e::getMessage, e);
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
