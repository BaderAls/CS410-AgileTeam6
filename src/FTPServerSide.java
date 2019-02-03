import org.apache.commons.net.ftp.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/* ServerSide, as opposed to localside */
public class FTPServerSide extends FTP {

    private FTPClient ftp;
    private String serverAddress;
    private int port;

    public FTPServerSide(String serverAdd, int connection_port ){

        serverAddress = serverAdd;
        ftp = new FTPClient();
        port = connection_port;

    }

    /*Connects to the server  */
    public int ConnectToServer() throws  IOException{

        int reply;   // local variable to check initial connection status.

        /* Connect to the specified server on specified port, need ftp.login() even for anon connections */
        System.out.println("Connecting too..." + serverAddress);
        ftp.connect(serverAddress,port);
        ftp.login("anonymous","");

        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion((reply))) {

            return -1;
        }

        return 1;
    }

    /*Uploads file to the server */
    public boolean uploadToServer(File myFile) throws IOException{

        boolean uploaded = false;

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);

        InputStream is = new FileInputStream(myFile);

        String fileName = myFile.getName();

        uploaded = ftp.storeFile(fileName, is);

        is.close();

        return uploaded;
    }

    public boolean logout(){

        try {

            ftp.logout();
            return true;
        }catch(IOException e){

            System.out.println("Log out unsuccessful");
        }

        return false;
    }

}
