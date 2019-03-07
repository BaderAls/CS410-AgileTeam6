
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/* ServerSide, as opposed to LocalSide */
public class FTPServerSide extends FTP {

    private FTPClient ftp;
    private String address;
    private int port;
    private List<String> sessionHistory;

    public FTPServerSide(){
        ftp = new FTPClient();
        sessionHistory = new ArrayList<>();
    }

    /* Class constructor */
    public FTPServerSide(String serverAdd, int connectPort) {
        ftp = new FTPClient();
        address = serverAdd;
        port = connectPort;
        sessionHistory = new ArrayList<>();
    }

    /*Constructor for saved connection*/
    public FTPServerSide(String in) throws IOException {
        ftp = new FTPClient();
        sessionHistory = new ArrayList<>();
        useSavedConnection();
    }

    /*
   * Connects to the server. Connect to the specified server on specified port,
   * need ftp.login() even for anonymous connections
     */
    public int ConnectToServer() throws IOException {
        int reply; // local variable to check initial connection status.
        System.out.println("Connecting to..." + address);
        ftp.connect(address, port);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion((reply))) {
                return -1;
        }
        logHistory(1," ");
        return 1;
    }

    /*  This function attempts to get valid login credentials from the users IO.
     *  If invalid input is received, the function should log the anonymous user connection
     *  out, reestablish connection and try again.
     */
    public boolean LoginToServer() throws IOException {
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";
        int reply;
        while (true) {
                System.out.println("Please Enter Username and Password (or quit as either to exit)");
                System.out.println("Username:");
                username = sc.nextLine();
                if (username.equalsIgnoreCase("quit"))
                    return false;
                System.out.println("Password:");
                password = sc.nextLine();
                if (password.equalsIgnoreCase("quit"))
                    return false;
                ftp.login(username, password);
                reply = ftp.getReplyCode();
                if (reply != 230) {
                    System.out.println("Invalid User Login");
                    ftp.logout();
                    ftp.connect(address, port);
                }
                else {
                    saveUserConnection(username, password);
                    System.out.println("Login Successful: Logged in as " + username);
                    logHistory(2,username);
                    return true;
                }
        }
    }

    /*
     * This function takes an a list of files and returns a list of files have
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
        int successes = myFiles.size() - failedFiles.size();
        logHistory(3,String.valueOf(successes));
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
        logHistory(4,"");
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

    // HELPER FUNCTION THAT I USE THAT IS NOT CALLED ANYWHERE FROM THE PROGRAM)
    public void displayCertainDirectory(String pathname) throws IOException {

        String[] FileNames = ftp.listNames(pathname);

        if (FileNames == null) {
            System.out.println("Error in obtaining file names!");
        } else if (FileNames.length == 0) {
            System.out.printf("No files in specified remote directory.");
        } else {
            for (int i = 0; i < FileNames.length; i++) {
                System.out.println(FileNames[i]);
            }
        }
    }

    public boolean deleteRemoteFile(String pathname) throws IOException {
        if (pathname.length() == 0) {
            return false;
        }
        logHistory(5,pathname);
        return ftp.deleteFile(pathname);
    }


    /* This function Changes directory to be able to download from a certain directory, or display a certain
        directory */
    public boolean ChangeDirectory(String pathname) throws IOException {

        boolean ret = ftp.changeWorkingDirectory(pathname);

        if (!ret) {
            System.out.println("No such directory!");
        }

        return ret;

    }

    /* This function reverts back to the main directory once it is done. Needs the int count value (number of navigated
     * directories) before being called */
    public void ChangeToMainDirectory(int count) throws IOException {

        int counter = 0;

        while (counter < count) {

            ftp.changeToParentDirectory();
            counter++;
        }
    }

    /* This function downloads all the files in the passed list to the localfilepath entered.
     * returns all the file names that have failed to download */
    public ArrayList<String> getRemoteFile(ArrayList<String> remoteFilePaths, String localFilePath) throws IOException {

        OutputStream os = null;
        ArrayList<String> failedFiles = new ArrayList<String>();
        boolean ret;

        for (String filepath : remoteFilePaths) {

            File file = new File(localFilePath + "/" + filepath);
            os = new FileOutputStream(file);
            ret = ftp.retrieveFile(filepath, os);

            if (!ret) {
                failedFiles.add(filepath);
            }
        }

        int successes = remoteFilePaths.size() - failedFiles.size();
        logHistory(10, String.valueOf(successes));
        if (os != null) {
            os.close();
        }

        return failedFiles;
    }


    /*saves username and password of last login.*/
    public void saveUserConnection(String username, String password) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(username);
        lines.add(this.address);
        lines.add(String.valueOf(this.port));
        Path file = Paths.get("savedCred.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    /*reads from last used login to establish a new connection*/
    public void useSavedConnection() throws IOException {
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new FileReader("savedCred.txt"));
        String username = reader.readLine();
        this.address = reader.readLine();
        this.port = Integer.parseInt(reader.readLine());
        System.out.println("Please input password for user "+username+":");
        String password = sc.nextLine();
        System.out.println("Connecting to Server at "+address+" using port "+port);
        ftp.connect(address, port);
        ftp.login(username, password);
        int reply = ftp.getReplyCode();
        if (reply != 230) {
            System.out.println("Error in Saved Connection");
            ftp.logout();
        } else {
            System.out.println("Connected to FTP user: " + username);
            logHistory(1," ");
            logHistory(2,username);
        }
    }

    /*Print the last server's response  */
    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }

    /*Checks if a directory exists on remote server*/
    public boolean directoryExists(String dirPath) throws IOException {
        String currentDirectory = ftp.printWorkingDirectory();
        try {
            return ftp.changeWorkingDirectory(dirPath);
        } finally {
            ftp.changeWorkingDirectory(currentDirectory);
        }
    }

    /*Makes a new directory on the remote server*/
    public void newDirectory(String dirToCreate) throws IOException {
        boolean success = ftp.makeDirectory(dirToCreate);
        if (success) {
            System.out.println("Successfully created directory: " + dirToCreate);
            logHistory(7,dirToCreate);
        } else {
            System.out.println("Failed to create directory. See server's reply.");
        }
    }


    /*Upload a whole directory (including its nested sub directories and files) to a FTP server recursively.*/
    public boolean copyDirectory(String remoteDirPath, String localParentDir, String remoteParentDir)
            throws IOException {

        boolean successful = true;
        String inremoteFilePath = remoteDirPath + "/" + remoteParentDir;
        ftp.makeDirectory(inremoteFilePath);
        File localDir = new File(localParentDir);
        File[] subFiles = localDir.listFiles();
        if (subFiles != null && subFiles.length > 0) {
            for (File item : subFiles) {
                String remoteFilePath = remoteDirPath + "/" + remoteParentDir
                        + "/" + item.getName();
                if (remoteParentDir.equals("")) {
                    remoteFilePath = remoteDirPath + "/" + item.getName();
                }

                if (item.isFile()) {
                    // upload the file
                    String localFilePath = item.getAbsolutePath();
                    File localFile = new File(localFilePath);
                    boolean uploaded;
                    InputStream inputStream = new FileInputStream(localFile);
                    try {
                        ftp.setFileType(FTP.BINARY_FILE_TYPE);
                        uploaded = ftp.storeFile(remoteFilePath, inputStream);
                    } finally {
                        inputStream.close();
                    }
                    if (!uploaded) {
                        successful = false;
                    }
                } else {
                    // create directory on the server
                    boolean created = ftp.makeDirectory(remoteFilePath);
                    if (!created) {
                        successful = false;
                    }

                    // upload the sub directory
                    String parent = remoteParentDir + "/" + item.getName();
                    if (remoteParentDir.equals("")) {
                        parent = item.getName();
                    }

                    localParentDir = item.getAbsolutePath();
                    if (!(copyDirectory(remoteDirPath, localParentDir,
                            parent))) {
                        successful = false;
                    }
                }
            }
        }
        if (successful == true)
            logHistory(8, localParentDir);
        return successful;
    }


    /* Removes a directory and all its sub files and sub directories recursively.*/
    public boolean deleteDirectory(String parentDir, String currentDir) throws IOException {
        String dirToList = parentDir;
        boolean successful = true;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }

        FTPFile[] subFiles = ftp.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/"
                        + currentFileName;
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName;
                }

                if (aFile.isDirectory()) {
                    // remove the sub directory
                    if (!(deleteDirectory(dirToList, currentFileName))) {
                        successful = false;
                    }
                } else {
                    // delete the file
                    boolean deleted = ftp.deleteFile(filePath);
                    if (!deleted) {
                        successful = false;
                    }
                }
            }

            // finally, remove the directory itself
            System.out.println(dirToList);
            boolean removed = ftp.removeDirectory(dirToList);
            if (!removed) {
                successful = false;
            }
        } else {
            boolean removed = ftp.removeDirectory(dirToList);
            if (!removed) {
                successful = false;
            }
        }
        if (successful==true)
            logHistory(9,parentDir);
        return successful;
    }

    /*
   * Search for a remote file and return a list of all the files that matches the
   * key name of the file
     */
    public FTPFile[] findRemoteFiles(String keyName, String remotePath) throws IOException {
        FTPFile[] filesList = ftp.listFiles(remotePath);
        FTPFile[] foundList = null;

        if (filesList != null && filesList.length > 0) {
            foundList = new FTPFile[filesList.length];
            int counter = 0;
            for (int i = 0; i < filesList.length; ++i) {
                if (filesList[i].getName() == keyName) {
                    foundList[counter++] = filesList[i];
                }
            }
        }
        return foundList;
    }

    /*
     * Logs activity completed by the ftp client and the associated reply codes
     */
    public void logHistory(int type, String info) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date = new Date();
        switch (type) {
            case 1:
                this.sessionHistory.add("Connected to "+address+" at port "+port+" - "+dateFormat.format(date));
                break;
            case 2:
                sessionHistory.add("Logged in as "+info+" - "+dateFormat.format(date));
                break;
            case 3:
                sessionHistory.add("Uploaded "+ info +" files from local to remote"+" - "+dateFormat.format(date));
                break;
            case 4:
                sessionHistory.add("Logged out"+" - "+dateFormat.format(date));
                break;
            case 5:
                sessionHistory.add("Deleted file "+ info+" from remote"+" - "+dateFormat.format(date));
                break;
            case 6:
                sessionHistory.add("Deleted "+info +"files from remote"+" - "+dateFormat.format(date));
                break;
            case 7:
                sessionHistory.add("Created directory" + info +" on remote"+" - "+dateFormat.format(date));
                break;
            case 8:
                sessionHistory.add("Uploaded " + info + "directory to remote"+" - "+dateFormat.format(date));
                break;
            case 9:
                sessionHistory.add("Deleted the " + info + " directory from remote"+" - "+dateFormat.format(date));
                break;
            case 10:
                sessionHistory.add("Downloaded "+info+"files from remote to local"+" - "+dateFormat.format(date));
                break;
            default:
                System.out.println("Unexpected input: logHistory");

        }
    }

    public void finalizeHistory() throws IOException{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Date date = new Date();
        String filename = ("Logfile||"+dateFormat.format(date)+".txt");
        Path file = Paths.get(filename);
        if(file==null)
            System.out.println("Error no valid Logfile");
        else
            Files.write(file, this.sessionHistory, Charset.forName("UTF-8"));
    }

    public boolean copyFile(String fileName, String pathName)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ftp.retrieveFile(fileName, outputStream);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        // assuming backup directory is with in current working directory
        ftp.setFileType(FTP.BINARY_FILE_TYPE);//binary files
        ftp.changeWorkingDirectory(pathName);
        //this overwrites the existing file
        return ftp.storeFile(fileName, is);
    }

    public boolean copyDirectoryHelper(String desRemoteDirPath, String sourceParentDir, String remoteParentDir)
            throws IOException {
        boolean successful = true;
        String firstDirectory = ftp.printWorkingDirectory();
        String currentDirectory = ftp.printWorkingDirectory() + "/" + sourceParentDir;
        if(!desRemoteDirPath.startsWith("/") && !desRemoteDirPath.startsWith("\\")){
            desRemoteDirPath = "/" +desRemoteDirPath;
        }
        if(!directoryExists(desRemoteDirPath)){
            System.out.println("The destination path does not exist");
            return false;
        }
        else if(!( ftp.changeWorkingDirectory(currentDirectory))){
            System.out.println("There is no folder with this name");
            return false;
        }
        else{
            String inremoteFilePath = desRemoteDirPath + "/" + remoteParentDir;
            ftp.makeDirectory(inremoteFilePath);
            FTPFile[] subFiles = ftp.listFiles(currentDirectory);
            if (subFiles != null && subFiles.length > 0) {
                for (FTPFile item : subFiles) {
                    String remoteFilePath = desRemoteDirPath + "/" + remoteParentDir;
                    if (remoteParentDir.equals("")) {
                        remoteFilePath = desRemoteDirPath;
                    }
                    if (item.isFile()) {
                        boolean uploaded;
                        uploaded = copyFile(item.getName(), remoteFilePath);
                        ftp.changeWorkingDirectory(currentDirectory);
                        if (!uploaded) {
                            successful = false;
                        }
                    } else {
                        boolean created = ftp.makeDirectory(remoteFilePath+"/"+item.getName());
                        if (!created) {
                            successful = false;
                        }

                        // upload the sub directory
                        String parent = item.getName();
                        if (remoteParentDir.equals("")) {
                            parent = "";
                        }
                        if(!(copyDirectoryHelper(remoteFilePath, item.getName(),
                                parent))){
                            successful = false;
                        }
                        ftp.changeWorkingDirectory(currentDirectory);
                    }
                }
            }
            ftp.changeWorkingDirectory(firstDirectory);
            return successful;
        }
    }

    public boolean copyDirectoryFtp(String desRemoteDirPath, String sourceParentDir, String remoteParentDir) throws IOException {

        if(!( directoryExists(ftp.printWorkingDirectory() + "/" + sourceParentDir))){
            System.out.println("There is no folder with this name");
            return false;
        }
        boolean successful=true;
        ftp.makeDirectory("/asdf1234qwer678");
        if(!copyDirectoryHelper("/asdf1234qwer678",sourceParentDir,remoteParentDir)){
            successful=false;
        }
        String currentDirectory = ftp.printWorkingDirectory();
        ftp.changeWorkingDirectory("/asdf1234qwer678");
        if(!copyDirectoryHelper(desRemoteDirPath,sourceParentDir,remoteParentDir)){
            successful=false;
        }
        deleteDirectory("/asdf1234qwer678","");
        ftp.changeWorkingDirectory(currentDirectory);
        return successful;
    }



    /* Rename file in remote directory */
    public boolean renameFile(String oldName, String replacement) throws IOException{


        return ftp.rename(oldName,replacement);

    }


}
/* END */