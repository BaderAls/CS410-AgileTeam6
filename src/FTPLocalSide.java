
import org.apache.commons.net.io.FileUtils;

import java.io.File;
import java.io.IOException;

/* LocalSide */
public class FTPLocalSide {

    private File directory;
    private String directoryString;

    /* Default Class constructor */
    public FTPLocalSide() {
        directory = null;
    }

    /* Class constructor */
    public FTPLocalSide(String localPath) {
        directory = new File(localPath);
        directoryString = localPath;
    }

    public boolean changeDirectory(String somepath){

        String newpath = directoryString + "/" + somepath;
        File directorywrapper = new File(newpath);

        if(directorywrapper.exists()){

            directory = directorywrapper;
            directoryString = newpath;
            return true;
        }
        else{
            return false;
        }

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
            for (int i = 0; i < filesList.length; ++i) {
                if (filesList[i].getName() == keyName) {
                    foundList[counter++] = filesList[i];
                }
            }
        }
        return foundList;
    }

    public boolean ChangeFileName(String localPath, String renameTo) {
        String location = localPath.substring(0, localPath.lastIndexOf("/"));
        File oldfile = new File(localPath);
        File newfile = new File(location + "/" + renameTo);
        return oldfile.renameTo(newfile);
    }

    /* Compare the content of two local files of the same type */
    public boolean diff(String file1Path, String file2Path) throws IOException {
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);
        return FileUtils.contentEquals(file1, file2);
    }
}
/* END */
