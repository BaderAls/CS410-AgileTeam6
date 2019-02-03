import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainMenu {


    public static void main (String [] args){

        int portNum;
        String hostAddress;


        Scanner scanner = new Scanner(System.in);
        String selection = "";
        int ret =0;
        int exceptionCounter;
        boolean connectionCheck = false;


        do {
            System.out.println("*-Group 6 FTPClient-*");
            System.out.println("Type 'connect' to establish connection to the server ");
            System.out.println("Type 'quit' to exit the program");

            selection = scanner.nextLine();

            System.out.println("You chose... " + selection);

            if(selection.equals("connect")){

                exceptionCounter = 0;

                System.out.println("Enter host address");
                hostAddress = scanner.nextLine();

                System.out.println("Enter port number");
                portNum = scanner.nextInt();
                scanner.nextLine();

                FTPServerSide myftp = new FTPServerSide(hostAddress,portNum);

                while(exceptionCounter< 2) {

                    try {
                        ret = myftp.ConnectToServer();

                        if (ret == 1) {
                            System.out.println("Connected to the Server");
                            connectionCheck = true;

                            exceptionCounter = 2;

                            do {

                                System.out.println("-*Group 6 FTPClient-* ");
                                System.out.println("Type 'upload' to upload a file to the server");
                                System.out.println("Type 'disconnect' to stop the current session");
                                System.out.println("Type 'quit' to exit the program");

                                selection = scanner.nextLine();

                                if(selection.equals("upload")){

                                    String filePath;

                                    System.out.println("Enter File Path:  ");

                                    filePath = scanner.nextLine();
                                    File myFile = new File(filePath);

                                    try{

                                        boolean response;
                                        response= myftp.uploadToServer(myFile);

                                        if(response == true){

                                            System.out.println("File uploaded successfully");
                                        }
                                    }
                                    catch (IOException e){

                                        System.out.println("File upload failed");
                                    }
                                }
                                else if (selection.equals("disconnect") || selection.equals("quit")){

                                    if (connectionCheck){

                                        try {
                                            System.out.println("Disconnecting....");
                                            myftp.disconnect();
                                            myftp.logout();

                                            connectionCheck = false;
                                        } catch(IOException ioe){

                                            System.out.println("Disconnect Attempt failed. Exiting the  System...");

                                            System.exit(1);
                                        }
                                    }
                                    else{
                                        System.out.println("Not even connected bruh");
                                    }
                                }
                            } while (connectionCheck && (!selection.equals("quit")));
                        }
                    } catch (IOException e) {

                        System.out.println(" Connection Attempt Failed ...");
                        exceptionCounter++;

                        if(exceptionCounter <2){

                            System.out.println("Retrying to establish the connection...");
                        }
                        else{
                            System.out.println("Retry also failed.Quitting Connection Attempt...");
                        }
                    }
                }
            }
        }while(!selection.equals("quit"));
    }
}
