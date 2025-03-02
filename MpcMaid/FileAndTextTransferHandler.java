 /**
* @See http://www.davidgrant.ca/drag_drop_from_linux_kde_gnome_file_managers_konqueror_nautilus_to_java_applications
*
* @See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4899516
*/
public class FileAndTextTransferHandler extends TransferHandler {
    private static final long serialVersionUID = 1L;
 
    private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
 
  private DataFlavor fileFlavor, stringFlavor;
  private DataFlavor uriListFlavor;
 
  private Utilisateur user = null;
 
  public FileAndTextTransferHandler() {
 
    fileFlavor = DataFlavor.javaFileListFlavor;
    stringFlavor = DataFlavor.stringFlavor;
 
    try {
      uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
    } catch (ClassNotFoundException e) {
      logger.log(Level.ERROR, e, e::getMessage);
    }
  }
 
  public FileAndTextTransferHandler(Utilisateur u) {
 
    fileFlavor = DataFlavor.javaFileListFlavor;
    stringFlavor = DataFlavor.stringFlavor;
 
    try {
      uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
    } catch (ClassNotFoundException e) {
      logger.log(Level.ERROR, e, e::getMessage);
    }
 
    this.user = u;
  }
 
  @Override
  public boolean importData(JComponent c, Transferable t) {
 
    if (!canImport(c, t.getTransferDataFlavors())) {
      return false;
    }
 
    try {
      // Windows
      if (hasFileFlavor(t.getTransferDataFlavors())) {
 
        final java.util.List files = (java.util.List) t
            .getTransferData(fileFlavor);
 
        process(files);       
 
        return true;
 
      // Linux
      }else if(hasURIListFlavor(t.getTransferDataFlavors())){
 
          final List<File> files = textURIListToFileList((String) t.getTransferData(uriListFlavor));
 
        if(files.size()>0){
 
            process(files);
        }
 
      }else if (hasStringFlavor(t.getTransferDataFlavors())) {
 
        String str = ((String) t.getTransferData(stringFlavor));
 
        logger.log(Level.INFO, str);
 
        return true;
      }
    } catch (UnsupportedFlavorException ufe) {
      logger.log(Level.ERROR, "importData: unsupported data flavor", ufe);
    } catch (IOException ieo) {
      logger.log(Level.ERROR, "importData: I/O exception", ieo);
    }
    return false;
  }
 
  @Override
  public int getSourceActions(JComponent c) {
    return COPY;
  }
 
  @Override
  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    if (hasFileFlavor(flavors)) {
      return true;
    }
    if (hasStringFlavor(flavors)) {
      return true;
    }
    return false;
  }
 
  private boolean hasFileFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (fileFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
 
  private boolean hasStringFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (stringFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
 
  private boolean hasURIListFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (uriListFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
 
  /** Your helpfull function */
  private static List<File> textURIListToFileList(String data) {
    List<File> list = new ArrayList<File>(1);
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
 
}