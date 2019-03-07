
import org.apache.commons.net.ftp.FTP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainMenu {

    FTPLocalSide myLocal;
    FTPServerSide myftp;

    public static void main(String[] args) {

        int portNum;
        String hostAddress;
        FTPLocalSide mylocal = new FTPLocalSide();
        // FTPLocalSide mylocal2 = new FTPLocalSide(""); //
        FTPServerSide myftp = new FTPServerSide();

        Scanner scanner = new Scanner(System.in);
        String selection = "";
        int ret = 0;
        int exceptionCounter;
        boolean connectionCheck = false;

        do {
            System.out.println();
            System.out.println("*-Group 6 FTPClient-*");
            System.out.println("Type 'connect' to establish connection to the server ");
            System.out.println("Type 'connectSaved' to reconnect with previous credentials");
            System.out.println("Type 'displaylocal' to display directories and files on local disk");
            System.out.println("Type 'rename' to rename a file on local disk");
            System.out.println("Type 'checkequal' to check if two files are the same ");
            System.out.println("Type 'quit' to exit the program");

            selection = scanner.nextLine();

            System.out.println("You chose... " + selection);

            if (selection.equals("connect") || selection.equalsIgnoreCase("connectSaved")) {
                exceptionCounter = 0;
                if(selection.equals("connect")) {
                    System.out.println("Enter host address");
                    hostAddress = scanner.nextLine();

                    System.out.println("Enter port number");
                    portNum = scanner.nextInt();
                    scanner.nextLine();

                    myftp = new FTPServerSide(hostAddress, portNum);
                }
                else {
                    try {
                        myftp = new FTPServerSide("Test");
                    } catch (IOException e){
                        System.out.println("Error");
                    }
                }
                while (exceptionCounter < 2) {
                    try {
                        if (selection.equals("connect")) {
                            ret = myftp.ConnectToServer();
                            if (!myftp.LoginToServer())
                                return;
                        }
                        else
                            ret = 1;
                        if (ret == 1) {
                            System.out.println("Connected to the Server");
                            connectionCheck = true;

                            exceptionCounter = 2;

                            do {
                                System.out.println();
                                System.out.println("-*Group 6 FTPClient-* ");
                                System.out.println("Type 'upload' to upload a file to the server");
                                System.out.println("Type 'download' to retrieve file from the server");
                                System.out.println("Type 'display' to display the contents of the directory.");
                                System.out.println("Type 'displaylocal' to display the  conents of the local directories");
                                System.out.println("Type 'rename' to rename a file on the remote server");
                                System.out.println("Type 'newdirectory' to create a new directory.");
                                System.out.println("Type 'cpydirlocal' to copy a directory and all its  nested sub "
                                        + "directories and files.");
                                System.out.println("Type 'cpydirremote' to copy a remote directory");
                                System.out.println("Type 'deldirectory' to delete a directory and all its sub files.");
                                System.out.println("Type 'disconnect' to stop the current session");
                                System.out.println("Type 'quit' to exit the program");

                                selection = scanner.nextLine();

                                if (selection.equals("upload")) {

                                    ArrayList<File> myFiles = new ArrayList<File>();
                                    String choice = "";
                                    String choice2 = "";
                                    int directcount = 0;

                                    do {
                                        myftp.displayRemote();
                                        System.out.println("Is this the directory do you want to upload to ? (yes/no/exit)");
                                        choice2 = scanner.nextLine();

                                        if (choice2.equals("no")) {
                                            String targetdirectory = "";
                                            System.out.println("Type in the directory you want to upload to");
                                            targetdirectory = scanner.nextLine();

                                            boolean someret = myftp.ChangeDirectory(targetdirectory);

                                            if (someret) {
                                                directcount++;
                                            }
                                        }

                                    } while (!choice2.equals("yes") && !choice2.equals("exit"));

                                    if(choice2.equals("yes")) {

                                        do {

                                            String filePath;
                                            System.out.println("Enter File Path:  ");
                                            filePath = scanner.nextLine();
                                            File myFile = new File(filePath);
                                            myFiles.add(myFile);
                                            System.out.println("Do you want to add more ? (yes/no)");
                                            choice = scanner.nextLine();
                                        } while (!choice.equals("no"));

                                        try {

                                            ArrayList<File> failedFiles;
                                            failedFiles = myftp.uploadToServer(myFiles);

                                            if (failedFiles.size() == 0) {

                                                System.out.println("All files uploaded successfully");
                                            } else {

                                                System.out.println("WARNING: One or more file transfer(s) failed!");
                                                System.out.println("Failed to transfer: ");
                                                int i = 1;

                                                for (File x : failedFiles) {

                                                    System.out.println(i + ". " + x.getName());
                                                    i++;
                                                }

                                                System.out.println("TIP: Make sure you typed the correct file path");

                                            }
                                        } catch (IOException e) {

                                            System.out.println("File upload failed");
                                        }
                                    }
                                    myftp.ChangeToMainDirectory(directcount);
                                } else if (selection.equals("download")) {

                                    String downloadSelection = "";
                                    String fileSelection = "";
                                    String multipleSelection = "";
                                    String localPathToDownload = "";
                                    //myftp.displayRemote();
                                    int countdirectories = 0;
                                    ArrayList<String> FilesToDownload = new ArrayList<String>();

                                    do {
                                        myftp.displayRemote();
                                        System.out.println("Is this the directory do you want to download from ?(yes/no/exit)");
                                        downloadSelection = scanner.nextLine();

                                        if (downloadSelection.equals("no")) {
                                            String directorySelection = "";

                                            System.out.println("Type the name of the directory you want to navigate to:");
                                            directorySelection = scanner.nextLine();

                                            boolean someret = myftp.ChangeDirectory(directorySelection);

                                            if (someret) {
                                                countdirectories++;
                                            }
                                        }

                                    } while (!downloadSelection.equals("yes") && !(downloadSelection.equals("exit")));

                                    if(downloadSelection.equals("yes")) {


                                        do {

                                            System.out.println("Enter the file name you want to download: ");
                                            fileSelection = scanner.nextLine();

                                            FilesToDownload.add(fileSelection);

                                            System.out.println("Do you want to add more ? (yes/no");
                                            multipleSelection = scanner.nextLine();
                                        } while (multipleSelection.equals("yes"));

                                        System.out.println("Please enter the full local path to Download these files: ");
                                        localPathToDownload = scanner.nextLine();

                                        try {
                                            ArrayList<String> failedFiles;

                                            failedFiles = myftp.getRemoteFile(FilesToDownload, localPathToDownload);

                                            if (failedFiles.size() == 0) {
                                                System.out.println("All files have been downloaded successfully");
                                            } else {

                                                System.out.println("WARNING: One or more file transfer(s) failed!");
                                                System.out.println("Failed to download: ");
                                                int i = 1;

                                                for (String x : failedFiles) {

                                                    System.out.println(i + ". " + x);
                                                    i++;
                                                }

                                                System.out.println("TIP: Make sure you typed the correct file path");

                                            }
                                        } catch (IOException e) {
                                            System.out.println("File or Directory does not exist");
                                        }
                                    }

                                    myftp.ChangeToMainDirectory(countdirectories);

                                } else if (selection.equals("disconnect") || selection.equals("quit")) {

                                    if (connectionCheck) {

                                        try {
                                            System.out.println("Disconnecting....");
                                            myftp.disconnect();
                                            myftp.logout();

                                            connectionCheck = false;
                                        } catch (IOException ioe) {

                                            System.out.println("Disconnect Attempt failed. Exiting the  System...");

                                            System.exit(1);
                                        }
                                    } else {
                                        System.out.println("Not even connected bruh");
                                    }
                                } else if (selection.equals("display")) {
                                    myftp.displayRemote();
                                    String directorychoice = "";
                                    String directoryname = "";
                                    int countDirectoryDisntace = 0;

                                    do {
                                        System.out.println("Do you want to Display Files in Certain Directory ? (yes/no)");
                                        directorychoice = scanner.nextLine();
                                        if (directorychoice.equals("yes")) {

                                            System.out.println("Type in the name of the directory");
                                            directoryname = scanner.nextLine();

                                            myftp.ChangeDirectory(directoryname);
                                            myftp.displayRemote();

                                            countDirectoryDisntace++;

                                            // myftp.displayCertainDirectory(directoryname);
                                        }
                                    } while (!directorychoice.equals("no"));

                                    myftp.ChangeToMainDirectory(countDirectoryDisntace);
                                }
                                else if (selection.equals("rename")){

                                    myftp.displayRemote();
                                    String rChoice = "";
                                    String rName = "";
                                    int countDirectoryDist = 0;

                                    do{
                                        System.out.println("Do you want to rename a file in this directory ? (yes/no/exit)");
                                        rChoice = scanner.nextLine();

                                        if(rChoice.equals("yes")){

                                            String oldie= "";
                                            String newie = "";
                                            boolean theret;

                                            System.out.println("Enter the current name of the file you want to rename:");
                                            oldie = scanner.nextLine();

                                            System.out.println("Enter the new name you want to give to the file: ");
                                            newie = scanner.nextLine();

                                            theret = myftp.renameFile(oldie,newie);

                                            if(theret){
                                                System.out.println("Succesfully renamed!");
                                            }
                                            else{
                                                System.out.println("No renaming occured");
                                            }

                                        }
                                        else if (rChoice.equals("no")){

                                            System.out.println("Type in the directory you want to rename (no / at front)");
                                            rName = scanner.nextLine();

                                            myftp.ChangeDirectory(rName);
                                            myftp.displayRemote();

                                            countDirectoryDist++;
                                        }

                                    }while(!rChoice.equals("exit"));


                                    myftp.ChangeToMainDirectory(countDirectoryDist);

                                }
                                else if (selection.equals("newdirectory")) {
                                    String directoryPath;
                                    System.out.println("Enter The New Directory Path:  ");
                                    directoryPath = scanner.nextLine();
                                    myftp.newDirectory(directoryPath);
                                } else if (selection.equals("cpydirlocal")) {
                                    String directoryPath = null;
                                    String localDirectory = null;
                                    String directoryName;
                                    boolean remoteDirectoryExists = false;
                                    while (!remoteDirectoryExists) {
                                        System.out.println("Where do you want to copy this directory on server:  ");
                                        directoryPath = scanner.nextLine();
                                        remoteDirectoryExists = myftp.directoryExists(directoryPath);
                                        if (!remoteDirectoryExists) {
                                            System.out.println("There is no such a directory with this path");
                                        }
                                    }
                                    boolean exists = false;
                                    while (!exists) {
                                        System.out.println("Enter The Path of the local directory being copied:  ");
                                        localDirectory = scanner.nextLine();
                                        File dir = new File(localDirectory);
                                        exists = dir.exists();
                                        if (!exists) {
                                            System.out.println("There is no such a directory with this path");
                                        }
                                    }
                                    System.out.println("Enter The Name You want to give on server: ");
                                    directoryName = scanner.nextLine();
                                    System.out.println("Copying...");
                                    if (myftp.copyDirectory(directoryPath, localDirectory, directoryName)) {
                                        System.out.println("All files and folders successfully copied to the remote server.");
                                    } else {
                                        System.out.println("Some files and folders may not have"
                                                + " been copied to the remote server successfully.");
                                    }
                                }
                                else if(selection.equals("cpydirremote")){

                                    System.out.println("Enter The folder name:  ");
                                    String fileName = scanner.nextLine();
                                    System.out.println("Enter The destination path:  ");
                                    String pathName = scanner.nextLine();
                                    if(myftp.copyDirectoryFtp(pathName, fileName,fileName)){
                                        System.out.println("The directory has been copied successfully");
                                    }
                                    else{
                                        System.out.println("Some files and folders may not have" +
                                                " been copied successfully.");
                                    }

                                }
                                else if (selection.equals("deldirectory")) {
                                    String directoryPath = null;
                                    boolean directoryExists = false;
                                    while (!directoryExists) {
                                        System.out.println("Enter The Path of directory you want to delete:  ");
                                        directoryPath = scanner.nextLine();
                                        directoryExists = myftp.directoryExists(directoryPath);
                                        if (!directoryExists) {
                                            System.out.println("There is no such a directory with this path");
                                        }
                                    }
                                    System.out.println("Deleting...");
                                    if (myftp.deleteDirectory(directoryPath, "")) {
                                        System.out.println("The Directory deleted successfully.");
                                    } else {
                                        System.out.println("Some files and folders may not have"
                                                + " been deleted successfully.");
                                    }
                                }

                                else if (selection.equals("displaylocal")){

                                    String ldisplayselection = "";
                                    String somepath = "";

                                    System.out.println("Enter the local disk path you want to display");
                                    somepath = scanner.nextLine();

                                    FTPLocalSide mylocalftp = new FTPLocalSide(somepath);

                                    do{
                                        try {

                                            mylocalftp.displayLocal();

                                            System.out.println("Do you want to go further in the directory? (yes/no)");

                                            ldisplayselection = scanner.nextLine();

                                            if(ldisplayselection.equals("yes")){

                                                String furtherString = "";
                                                boolean someret = false;
                                                System.out.println("Type in the folder/directory name");
                                                furtherString = scanner.nextLine();
                                                someret =  mylocalftp.changeDirectory(furtherString);

                                                if(!someret){
                                                    System.out.println("Directory doesn't exist");
                                                }
                                            }
                                        }
                                        catch (IOException e){

                                            System.out.println("Problem encountered");

                                        }
                                    }while (!ldisplayselection.equals("no"));
                                }
                            } while (connectionCheck && (!selection.equals("quit")));
                        }
                    } catch (IOException e) {

                        System.out.println(" Connection Attempt Failed ...");
                        exceptionCounter++;

                        if (exceptionCounter < 2) {

                            System.out.println("Retrying to establish the connection...");
                        } else {
                            System.out.println("Retry also failed.Quitting Connection Attempt...");
                        }
                    }
                }
            } else if (selection.equals("rename")) {

                String oldpath = "";
                String newname = "";
                boolean retstat = false;
                System.out.println("Type the path of the file you want to rename");
                oldpath = scanner.nextLine();

                System.out.println("Type the new name you want to give to this file");
                newname = scanner.nextLine();

                try {


                    retstat = mylocal.ChangeFileName(oldpath, newname);

                    if (retstat) {
                        System.out.println("Sucessfully renamed the file to: " + newname);
                    } else {
                        System.out.println("Renaming failed");
                    }
                }catch (StringIndexOutOfBoundsException e){
                    System.out.println("Something went wrong no renaming!");
                }

            }
            else if(selection.equals("checkequal")){

                String filename1 = "";
                String filename2 = "";

                System.out.println("Enter file1 path: ");
                filename1 = scanner.nextLine();

                System.out.println("Enter file2 path: ");
                filename2 = scanner.nextLine();

                try {


                    int somereturn = mylocal.diff(filename1, filename2);

                    if(somereturn == 1){
                        System.out.println("The two files are the SAME!");
                    }
                    else if(somereturn == 0){
                        System.out.println("The two files are DIFFERENT!");
                    }
                    else{
                        System.out.println("One or more files do no exist!");
                    }
                }
                catch (IOException e){

                    System.out.println("Diff did not complete!");
                }

            }

            else if (selection.equals("displaylocal")){

                String ldisplayselection = "";
                String somepath = "";

                System.out.println("Enter the local disk path you want to display");
                somepath = scanner.nextLine();

                FTPLocalSide mylocalftp = new FTPLocalSide(somepath);

                do{
                    try {

                        mylocalftp.displayLocal();

                        System.out.println("Do you want to go further in the directory? (yes/no)");

                        ldisplayselection = scanner.nextLine();

                        if(ldisplayselection.equals("yes")){

                            String furtherString = "";
                            boolean someret = false;
                            System.out.println("Type in the folder/directory name");
                            furtherString = scanner.nextLine();
                            someret =  mylocalftp.changeDirectory(furtherString);

                            if(!someret){
                                System.out.println("Directory doesn't exist");
                            }
                        }
                    }
                    catch (IOException e){

                        System.out.println("Problem encountered");

                    }
                }while (!ldisplayselection.equals("no"));
            }
        } while (!selection.equals("quit"));
        try {
            myftp.finalizeHistory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
