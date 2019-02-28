
import org.apache.commons.net.ftp.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/* ServerSide, as opposed to LocalSide */
public class FTPServerSide extends FTP {

  private String localPath;
  private FTPClient ftp;
  private String serverAddress;
  private int port;

  /* allocate the SeverSide data */
  public FTPServerSide(String serverAdd, int connection_port) {
    ftp = new FTPClient();
    serverAddress = serverAdd;
    port = connection_port;
  }

  /* Connects to the server */
  public int ConnectToServer() throws IOException {
    int reply; // local variable to check initial connection status.

    /*
     * Connect to the specified server on specified port, need ftp.login() even for
     * anon connections
     */
    System.out.println("Connecting to..." + serverAddress);
    ftp.connect(serverAddress, port);
    ftp.login("anonymous", "");

    reply = ftp.getReplyCode();
    if (!FTPReply.isPositiveCompletion((reply))) {
      return -1;
    }
    return 1;
  }

  /*
   * *** This function takes an a list of files and returns a list of files have
   * failed the transfer procedure. Function checks each File on the list to make
   * sure they "exist", if the file exists, then it transfers the file to the
   * server. If the file doesn't exist in the given path, the file is added to the
   * failedFiles list.
   *
   * UNIT TEST BY ASSERTING THE RETURN TO BE NULL OR NOT NULL ( failedFiles.size()
   * == {int value} )
   */
  public ArrayList<File> uploadToServer(ArrayList<File> myFiles) throws IOException {

    boolean uploaded = false;
    ArrayList<File> failedFiles = new ArrayList<File>();
    String fileName = "";

    ftp.setFileType(FTP.BINARY_FILE_TYPE);
    ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);

    InputStream is = null;

    for (File file : myFiles) { // go through all the files

      if (file.exists()) { // check if exists. If it does... transfer

        is = new FileInputStream(file);

        fileName = file.getName();

        uploaded = ftp.storeFile(fileName, is);
      } else { // ... If the file doesn't exist, add it to the failed files list.
        failedFiles.add(file);
      }
    }

    if (is != null) {

      is.close();
    }

    return failedFiles;
  }

  public boolean logout() {

    try {

      ftp.logout();
      return true;
    } catch (IOException e) {

      System.out.println("Log out unsuccessful");
    }

    return false;
  }

  /* List directories & files on the remote directory */
  public void displayRemote() throws IOException {
    String[] FileNames = ftp.listNames();

    if (FileNames == null) {
      System.out.println("Error in obtaining file names!");
    } else if (FileNames.length == 0) {
      System.out.printf("No files in current remote directory.");
    } else {
      for (int i = 0; i < FileNames.length; i++) {
        System.out.println(FileNames[i]);
      }
    }
  }

  /* List directories & files on the local directory */
  public void displayLocal() throws IOException {
    File folder = new File(localPath);
    File[] listOfFiles = folder.listFiles();

    if (listOfFiles == null)
      System.out.println("Error in obtaining list of files!");

    for (int i = 0; i < listOfFiles.length; ++i) {
      if (listOfFiles[i].isFile()) {
        System.out.println(listOfFiles[i].getName());
      } else if (listOfFiles[i].isDirectory()) {
        System.out.println(listOfFiles[i].getName());
      }
    }
  }

  /* Get a file from the remote direcotry */
  public void getRemoteFile(String remoteFilePath, String localFilePath) {
    try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
      this.ftp.retrieveFile(remoteFilePath, fos);
    } catch (IOException e) {
      System.err.println("Error!");
    }
  }

} /* END */
