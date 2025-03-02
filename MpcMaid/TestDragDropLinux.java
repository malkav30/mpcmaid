package ca.davidgrant.ui;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Working drag and drop on Windows and Linux with either Nautilus (Gnome) or Konqueror (KDE)
 */
public class TestDragDropLinux extends JList implements DropTargetListener {
// ------------------------------ FIELDS ------------------------------

  DropTarget dropTarget = null;
  private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";

// -------------------------- STATIC METHODS --------------------------

  private static List textURIListToFileList(String data) {
    List list = new ArrayList(1);
    for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
      String s = st.nextToken();
      if (s.startsWith("#")) {
        // the line is a comment (as per the RFC 2483)
        continue;
      }
      try {
        URI uri = new URI(s);
        File file = new File(uri);
        list.add(file);
      } catch (URISyntaxException e) {
        logger.log(Level.ERROR, e, e::getMessage);
      } catch (IllegalArgumentException e) {
        logger.log(Level.ERROR, e, e::getMessage);
      }
    }
    return list;
  }

// --------------------------- CONSTRUCTORS ---------------------------

  public TestDragDropLinux() {
    dropTarget = new DropTarget(this, this);
  }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface DropTargetListener ---------------------

  public void dragEnter(DropTargetDragEvent event) {
    event.acceptDrag(DnDConstants.ACTION_MOVE);
  }

  public void dragOver(DropTargetDragEvent event) {
  }

  public void dropActionChanged(DropTargetDragEvent event) {
  }

  public void dragExit(DropTargetEvent event) {
  }

  public void drop(DropTargetDropEvent event) {
    Transferable transferable = event.getTransferable();
    DefaultListModel model = new DefaultListModel();

    event.acceptDrop(DnDConstants.ACTION_MOVE);

    DataFlavor uriListFlavor = null;
    try {
      uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
    } catch (ClassNotFoundException e) {
      logger.log(Level.ERROR, e, e::getMessage);
    }

    try {
      if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        List data = (List)
                transferable.getTransferData(DataFlavor.javaFileListFlavor);
        for (Object o : data) {
          model.addElement(o);
        }
        logger.log(Level.INFO, data);
      } else if (transferable.isDataFlavorSupported(uriListFlavor)) {
        String data = (String) transferable.getTransferData(uriListFlavor);
        List files = textURIListToFileList(data);
        for (Object o : files) {
          model.addElement(o);
        }
        logger.log(Level.INFO, files);
      }
    } catch (Exception e) {
      logger.log(Level.ERROR, e, e::getMessage);
    }

    setModel(model);
  }

// --------------------------- main() method ---------------------------

  public static void main(String[] args) {
    JFrame frame = new JFrame();

    frame.getContentPane().add(new TestDragDropLinux());
    frame.setSize(400, 200);

    frame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    }
    );

    frame.setVisible(true);
  }
}