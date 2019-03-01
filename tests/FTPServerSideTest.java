import org.junit.*;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class FTPServerSideTest {

    /*   Test it an available remote server */
    @Test
    public void connectToServer() throws Exception {

        FTPServerSide testServer = new FTPServerSide("10.0.0.0",2121);// hostaddress/port number goes hre;


        assertEquals(1,testServer.ConnectToServer());
    }

    @Test
    public void uploadToServer() throws Exception {

        FTPServerSide testServer = new FTPServerSide("10.0.0.0",2121);
        testServer.ConnectToServer();

        // ALL OF THESE FILES EXIST, SO I EXPECT THE FUNCTION TO RETURN NULL

        ArrayList<File> testlist = new ArrayList<File>();


        File testfile1 = new File("/Users/cemonder/Desktop/test13.png");
        File testfile2 = new File("/Users/cemonder/Desktop/test14.png");
        File testfile3 = new File("/Users/cemonder/Desktop/test15.png");
        File testfile4 = new File("/Users/cemonder/Desktop/test16.png");

        testlist.add(testfile1);
        testlist.add(testfile2);
        testlist.add(testfile3);
        testlist.add(testfile4);

        ArrayList<File> failedList0 = testServer.uploadToServer(testlist);


        assertEquals(0,failedList0.size());



        // 2 OF THESE FILES DO NOT EXIST IN THE PATH, SO I EXPECT THE RETURN FAILED LIST TO HAVE SIZE 2


        ArrayList<File> testlist2 = new ArrayList<File>();


        File testfile5 = new File("/Users/cemonder/Desktop/test17.png");
        File testfile6 = new File("/Users/cemonder/Desktop/test14dsf.png"); //WRONG PATH
        File testfile7 = new File("/Users/cemonder/Desktop/test18.png");
        File testfile8 = new File("/Users/cemonder/Desktop/test1fasf.png"); //WRONG PATH

        testlist2.add(testfile5);
        testlist2.add(testfile6);
        testlist2.add(testfile7);
        testlist2.add(testfile8);

        ArrayList<File> failedList = testServer.uploadToServer(testlist2);

        assertEquals(2,failedList.size());

        // 3 OF THESE FILES DO NOT EXIST IN THE PATH, SO I EXPECT THE RETURN FAILED LIST TO HAVE SIZE 3


        ArrayList<File> testlist3= new ArrayList<File>();


        File testfile9 = new File("/Users/cemonder/Desktop/test19FD.png");
        File testfile10 = new File("/Users/cemonder/Desktop/test1DFS.png");
        File testfile11 = new File("/Users/cemonder/Desktop/test15.GDSpng");
        File testfile12 = new File("/Users/cemonder/Desktop/test19.png"); //ONLY THIS IS THE CORRECT PATH

        testlist3.add(testfile9);
        testlist3.add(testfile10);
        testlist3.add(testfile11);
        testlist3.add(testfile12);

        ArrayList<File> failedList2 = testServer.uploadToServer(testlist3);

        assertEquals(3,failedList2.size());

        // NO CORRECT PATH. I EXPECT ALL 4 TO BE IN THE RETURNED LIST

        ArrayList<File> testlist4= new ArrayList<File>();


        File testfile13 = new File("/Users/cemonder/Desktop/test1FD9FD.png");
        File testfile14 = new File("/Users/cemonder/Desktop/test1DDSFS.png");
        File testfile15 = new File("/Users/cemonder/Desktop/test1FSD5.GDSpng");
        File testfile16 = new File("/Users/cemonder/Desktop/test1FD9.png");

        testlist4.add(testfile13);
        testlist4.add(testfile14);
        testlist4.add(testfile15);
        testlist4.add(testfile16);

        ArrayList<File> failedList3 = testServer.uploadToServer(testlist4);

        assertEquals(4,failedList3.size());

    }


    // PASSES FOR MULTIPLE SCENARIOS



    /* MAKE SURE TO INCLUDE YOUR OWN LOCAL FILES AND SERVER WHEN TESTING */
    // THIS TEST IS CURRENTLY USELESS, BECAUSE THIS FUNCTION HAS CHANGED TO ACCOMMODATE MULTIPLE FILES

    /*
    @Test
    public void uploadToServer() throws Exception {

        FTPServerSide testServer = new FTPServerSide("10.1.1.1",2121);  // host address/port number goes here
        testServer.ConnectToServer();

        File myfile = new File("/Users/cemonder/Desktop/menu.py");
        File myfile2 = new File("/Users/cemonder/Desktop/menu.h");
        File myfile3 = new File("/Users/cemonder/Desktop/data_test.pdf");

        assertEquals(true,testServer.uploadToServer(myfile));

        assertEquals(true,testServer.uploadToServer(myfile2));

        assertEquals(true,testServer.uploadToServer(myfile3));

    }
    */



    /* ALL TESTS PASS UP TO THIS POINT -CEM ONDER-   */
}