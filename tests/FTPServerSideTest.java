import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FTPServerSideTest {

    /*   Test it an available remote server
    @Test
    public void connectToServer() throws Exception {

        FTPServerSide testServer = new FTPServerSide()// hostaddress/port number goes hre;


        assertEquals(1,testServer.ConnectToServer());
    }

    */


    /* MAKE SURE TO INCLUDE YOUR OWN LOCAL FILES WHEN TESTING
    @Test
    public void uploadToServer() throws Exception {

        //FTPServerSide testServer = new FTPServerSide();  // host address/port number goes here
        testServer.ConnectToServer();

        File myfile = new File("/Users/cemonder/Desktop/menu.py");
        File myfile2 = new File("/Users/cemonder/Desktop/menu.h");
        File myfile3 = new File("/Users/cemonder/Desktop/data_test.pdf");

        assertEquals(true,testServer.uploadToServer(myfile));

        assertEquals(true,testServer.uploadToServer(myfile2));

        assertEquals(true,testServer.uploadToServer(myfile3));

    }

    */

    /* ALL TESTS PASS UPTO THIS POINT -CEM ONDER-   */
}