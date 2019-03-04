
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {

        int portNum;
        String hostAddress;
        FTPLocalSide mylocal = new FTPLocalSide();
        // FTPLocalSide mylocal2 = new FTPLocalSide(""); //

        Scanner scanner = new Scanner(System.in);
        String selection = "";
        int ret = 0;
        int exceptionCounter;
        boolean connectionCheck = false;

        do {
            System.out.println();
            System.out.println("*-Group 6 FTPClient-*");
            System.out.println("Type 'connect' to establish connection to the server ");
            System.out.println("Type rename to rename a file on local disk");
            System.out.println("Type 'quit' to exit the program");

            selection = scanner.nextLine();

            System.out.println("You chose... " + selection);

            if (selection.equals("connect")) {

                exceptionCounter = 0;

                System.out.println("Enter host address");
                hostAddress = scanner.nextLine();

                System.out.println("Enter port number");
                portNum = scanner.nextInt();
                scanner.nextLine();

                FTPServerSide myftp = new FTPServerSide(hostAddress, portNum);

                while (exceptionCounter < 2) {

                    try {
                        ret = myftp.ConnectToServer();

                        if (ret == 1) {
                            System.out.println("Connected to the Server");
                            connectionCheck = true;

                            exceptionCounter = 2;

                            do {
                                System.out.println();
                                System.out.println("-*Group 6 FTPClient-* ");
                                System.out.println("Type 'upload' to upload a file to the server");
                                System.out.println("Type 'download' to retrieve file from the server");
                                System.out.println("Type 'disconnect' to stop the current session");
                                System.out.println("Type 'quit' to exit the program");
                                System.out.println("Type 'display' to display the contents of the directory.");
                                System.out.println("Type 'newdirectory' to create a new directory.");
                                System.out.println("Type 'cpydirectory' to copy a directory and all its  nested sub "
                                        + "directories and files.");
                                System.out.println("Type 'deldirectory' to delete a directory and all its sub files.");

                                selection = scanner.nextLine();

                                if (selection.equals("upload")) {

                                    ArrayList<File> myFiles = new ArrayList<File>();
                                    String choice = "";
                                    String choice2 = "";
                                    int directcount = 0;

                                    do {
                                        myftp.displayRemote();
                                        System.out.println("Is this the directory do you want to upload to ? (yes/no)");
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

                                    } while (!choice2.equals("yes"));

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

                                    myftp.ChangeToMainDirectory(directcount);
                                } else if (selection.equals("download")) {

                                    String downloadSelection = "";
                                    String fileSelection = "";
                                    String multipleSelection = "";
                                    String localPathToDownload = "";
                                    myftp.displayRemote();
                                    int countdirectories = 0;
                                    ArrayList<String> FilesToDownload = new ArrayList<String>();

                                    do {
                                        myftp.displayRemote();
                                        System.out.println("Is this the directory do you want to download from ?(yes/no)");
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

                                    } while (!downloadSelection.equals("yes"));

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
                                        System.out.println("Serious problems!!");
                                        e.printStackTrace();
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
                                } else if (selection.equals("newdirectory")) {
                                    String directoryPath;
                                    System.out.println("Enter The New Directory Path:  ");
                                    directoryPath = scanner.nextLine();
                                    myftp.newDirectory(directoryPath);
                                } else if (selection.equals("cpydirectory")) {
                                    String directoryPath = null;
                                    String localDirectory = null;
                                    String directoryName;
                                    boolean remoteDirectoryExists = false;
                                    while (!remoteDirectoryExists) {
                                        System.out.println("Enter The Path of the destination directory on the server:  ");
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
                                    System.out.println("Enter The Name of the parent directory for newly copied directories:  ");
                                    directoryName = scanner.nextLine();
                                    System.out.println("Copying...");
                                    if (myftp.copyDirectory(directoryPath, localDirectory, directoryName)) {
                                        System.out.println("All files and folders successfully copied to the remote server.");
                                    } else {
                                        System.out.println("Some files and folders may not have"
                                                + " been copied to the remote server successfully.");
                                    }
                                } else if (selection.equals("deldirectory")) {
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

                retstat = mylocal.ChangeFileName(oldpath, newname);

                if (retstat) {
                    System.out.println("Sucessfully renamed the file to: " + newname);
                } else {
                    System.out.println("Renaming failed");
                }

            }
        } while (!selection.equals("quit"));
    }
}
