import org.apache.commons.net.ftp.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        if (!LoginToServer())
            return -1;
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion((reply))) {
            return -1;
        }
        return 1;
    }

    /*  This function attempts to get valid login credentials from the users IO.
    If invalid input is received, the function should log the anonymous user connection
    out, reestablish connection and try again.
    */
    public boolean LoginToServer() throws IOException{
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";
        int reply;
        boolean cont = true;
        String selection;
        System.out.println("Would you like to use a saved connection? (y/n)");
        selection = sc.nextLine();
        if (selection.equals("y")){
            useSavedConnection();
        }
        else {
            while (cont) {
                System.out.println("Please Enter Username and Password (or quit as either to exit");
                System.out.println("Username: \t");
                username = sc.nextLine();
                System.out.println("Password");
                password = sc.nextLine();
                if (username.equals("quit") || password.equals("quit"))
                    return false;
                ftp.login(username, password);
                reply = ftp.getReplyCode();
                if (reply != 230) {
                    System.out.println("Invalid User Login");
                    ftp.logout();
                    ftp.connect(serverAddress, port);
                } else {
                    System.out.println("Login Successful: Logged in as " + username);
                    cont = false;
                }
            }
        }
        saveUserConnection(username, password);
        return true;
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

    /*saves username and password of last login.*/
    public void saveUserConnection(String username, String password) throws IOException{
        List<String> lines = new ArrayList<>();
        lines.add(username);
        lines.add(password);
        Path file = Paths.get("savedCred.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    /*reads from last used login to establish a new connection*/
    public void useSavedConnection() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("savedCred.txt"));
        String username = reader.readLine();
        String password = reader.readLine();
        System.out.println(username + password);
        ftp.login(username, password);
        int reply = ftp.getReplyCode();
        if (reply != 230){
            System.out.println("Error in Saved Connection");
            ftp.logout();
        }
        else{
            System.out.println("Connected to FTP user: "+username);
        }
    }
} /* END */
