
import java.io.File;
import java.io.IOException;

/* LocalSide */
public class FTPLocalSide {

  private File directory;

  /* Class constructor */
  public FTPLocalSide(String localPath) {
    directory = new File(localPath);
  }

  /* List directories and files on the local directory */
  public void displayLocal() throws IOException {
    File[] listOfFiles = directory.listFiles();

    if (listOfFiles != null && listOfFiles.length > 0) {
      for (int i = 0; i < listOfFiles.length; ++i) {
        if (listOfFiles[i].isFile()) {
          System.out.println("File: " + listOfFiles[i].getName());
        } else if (listOfFiles[i].isDirectory()) {
          System.out.println("Directory: " + listOfFiles[i].getName());
        } else {
          System.out.println("Unknown: " + listOfFiles[i].getName());
        }
      }
    } else {
      System.out.println("Directory is empty!");
    }
  }

  /*
   * Search for a local file and return a list of all the files that matches the
   * key name of the file
   */
  public File[] findLocalFiles(String keyName, String localPath) {
    File[] filesList = directory.listFiles();
    File[] foundList = null;

    if (filesList != null && filesList.length > 0) {
      foundList = new File[filesList.length];
      int counter = 0;
      for (int i = 0; i < filesList.length; ++i)
        if (filesList[i].getName() == keyName)
          foundList[counter++] = filesList[i];
    }
    return foundList;
  }
} /* END */
