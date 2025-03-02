package com.mpcmaid.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.Serial;
import java.util.List;
import java.lang.System.Logger.Level;
import java.lang.System.Logger;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Drag handler to retrieve files. It is intended to be subclassed to process
 * the dragged list of files or each dragged file
 * 
 * @author cyrille martraire
 */
@SuppressWarnings("unchecked")
public class FileDragHandler extends TransferHandler {

	private static final Logger logger = System.getLogger(FileDragHandler.class.getName());

	@Serial
    private static final long serialVersionUID = 2989210654686012401L;

	public boolean importData(JComponent c, Transferable data) {
		if (!canImport(c, data.getTransferDataFlavors())) {
			return false;
		}
		try {
			final List<File> files = (List<File>) data.getTransferData(DataFlavor.javaFileListFlavor);
			process(files);
		} catch (Exception e) {
			logger.log(Level.ERROR, e::getMessage, e);
		}

		return true;
	}

	protected void process(List<File> files) {
        for (File file : files) {
            process(file);
        }
	}

	protected void process(File file) {
		logger.log(Level.INFO, file.getAbsolutePath());
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		final DataFlavor fileFlavor = DataFlavor.javaFileListFlavor;
        for (DataFlavor flavor : flavors) {
            if (fileFlavor.equals(flavor)) {
                return true;
            }
        }
		return false;
	}
}