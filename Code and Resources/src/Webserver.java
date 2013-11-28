/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Marc
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Decoder;

public class Webserver {
    //Defining Variables
    private int port; //= 2050;
    public static int start;
    public String user = "";
    int totaldata=0;
    int totalrequests=0;
    public String pass = "";
    public String directory;
    private ServerSocket serverSocket;
    int statuscode = 0;
    public String currentdate = "";

    public Webserver() throws ClassNotFoundException {
        try {
            //Reading the directory path
            BufferedReader in = new BufferedReader(new FileReader("directory.txt"));
            directory = in.readLine();


        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        try {
            //Reading the port number, username, and password
           BufferedReader in1 = new BufferedReader(new FileReader("port.txt"));
            String portstring = in1.readLine();
            //System.out.println(portstring);
            port = Integer.parseInt(portstring.trim());
            //System.out.println(port);
            in1.close();
            BufferedReader in2 = new BufferedReader(new FileReader("userfile.txt"));
            BufferedReader in3 = new BufferedReader(new FileReader("passfile.txt"));
            user = in2.readLine();
            pass = in3.readLine();
            //System.out.println(user);
            //System.out.println(pass);
            in2.close();
            in3.close();


        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void acceptConnections() {

        try {
            //Creating Socket
            serverSocket = new ServerSocket(port);
            System.out.println("Creating Socket");
            System.out.println("Listening to Port:" + port);

        } catch (IOException e) {
            System.err.println("ServerSocket instantiation failure");
            System.exit(0);
        }


             while (true) {
            try {
                //Waiting for client request and establishing connection
                System.out.println("Waiting for Client Request");
                Socket newConnection = serverSocket.accept();
                totalrequests++;
                System.out.println("Connection Established");
                ServerThread st = new ServerThread(newConnection);
                new Thread(st).start();

            } catch (IOException ioe) {
                System.err.println("Connection Failed");
            }
        }

    }

    public static void main(String[] args) {
        //String[] trys =new String[5];


        NewJFrame f=new NewJFrame();
        f.setVisible(true);

        Webserver server = null;

       
        while(start!=1)
        {
            System.out.print("");
        }     
       // NewJFrame.main(trys);
 try {
            server = new Webserver();
        } catch (ClassNotFoundException e) {
        }
       server.acceptConnections();

       
    }

    class ServerThread implements Runnable {

        //Initializing the socket and input and output data streams
        private Socket socket;
        private DataInputStream datain;
        private DataOutputStream dataout;
        // PrintStream dataout;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }


        public void run() {


              // Here is where the processing of a new connection occurs
            try {
                // This timeout will allow us to have a persisting connection
                socket.setSoTimeout(5000);
            } catch (SocketException e) {
                System.out.println("Got an SocketException" + e.getMessage());
            }
            try {

         while (true) {

                      //Initializing variables of ip, path, browser type, and logdate
            FileWriter fw;
            InetAddress ip;
            String path = " ";
            String browserlong = " ";
            String browser = " ";
            String logdate = " ";
            String connex="";
            BufferedWriter bw;

            try {

                String filename = "Log File.txt";
                boolean append = true;
                fw = new FileWriter(filename, append);
                bw = new BufferedWriter(fw);

                //  DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

                Date date = new Date();
                // Defining the format of the logdate
                logdate = dateFormat.format(date);
                //Getting ip address
                ip = socket.getInetAddress();


                //Initializing the input and output streams
                datain = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                // dataout = new PrintStream(socket.getOutputStream());
                dataout =
                        new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                return;
            }


            String stringHeader = "";
            String instr = "";
            boolean check = true;

            String temporaryDatain = null;

            // Reading and converting the input data into string "instr"
            while (check == true) {

              //  try {
                    temporaryDatain = datain.readLine();
                    stringHeader = stringHeader + temporaryDatain + "\r\n";
              //  } catch (IOException ex) {
              //      Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
              //  }
                if (temporaryDatain != null) {
                    if (temporaryDatain.isEmpty()) {
                        check = false;

                    }
                }

            }

            instr = stringHeader;
            // System.out.println(instr);

            //Getting first string of request
            int index1 = instr.indexOf("\n");
            String frstline = instr.substring(0, index1 - 1);
            //   System.out.println(frstline);

            System.out.println("\r\n" + "Client Request:" + "\r\n" + instr);
            //Getting index of GET and HTTP/1.1
            int getindex = instr.indexOf("GET ");
            int htmlindex = frstline.indexOf("/");
            int httpindex = frstline.indexOf("HTTP/1.1");

            GET://Get lable used for breaking
                //Checking if request contains GET and HTTP/1.1. If yes, then valid request
                // If not, bad request
            if (getindex == 0 && htmlindex == 4 && httpindex != -1 && htmlindex < httpindex) {

                int connectind= instr.indexOf("Connection:");
                int endlinecon = instr.indexOf("\n", connectind);
                connex=instr.substring(connectind+12, endlinecon);
               // System.out.println(connex);


                //Getting the Browser type
                int brwserindex = instr.indexOf("User-Agent");
                if (brwserindex == -1) {
                    browser = "No Broswer Type Specified";
                } else {
                    int endline1 = instr.indexOf("\n", brwserindex);
                    browserlong = instr.substring(brwserindex + 12, endline1);

                    //Internet Explorer
                    if (browserlong.contains("MSIE")) {
                        int indexbrow = browserlong.indexOf("MSIE");
                        indexbrow = indexbrow + 5;
                        int indexcolon = browserlong.indexOf(";", indexbrow);
                        String version = browserlong.substring(indexbrow, indexcolon);
                        browser = "Microsoft Internet Explorer Version " + version;


                    } //Firefox
                    else if (browserlong.contains("Firefox")) {
                        int indexbrow = browserlong.indexOf("Firefox");
                        indexbrow = indexbrow + 8;
                        int indexcolon = browserlong.indexOf(" ", indexbrow);
                        String version = browserlong.substring(indexbrow, indexcolon);
                        browser = "Mozilla Firefox Version " + version;



                    } //Chrome
                    else if (browserlong.contains("Chrome")) {
                        int indexbrow = browserlong.indexOf("Chrome");
                        indexbrow = indexbrow + 7;
                        int indexcolon = browserlong.indexOf(" ", indexbrow);
                        String version = browserlong.substring(indexbrow, indexcolon);
                        browser = "Google Chrome " + version;



                    } //Safari
                    else if (!browserlong.contains("Chrome") && browserlong.contains("Safari")) {
                        int indexbrow = browserlong.indexOf("Version/");
                        indexbrow = indexbrow + 8;
                        int indexcolon = browserlong.indexOf(" ", indexbrow);
                        String version = browserlong.substring(indexbrow, indexcolon);
                        browser = "Safari " + version;



                    } else {
                        browser = "Unkown Browser Type and Version";
                    }

                }


                int space = httpindex - 1;

                path = instr.substring(4, space);

                //checking if path is simply and forward slash, hence go to index.html
                if (path.compareTo("/") == 0) {

                    path = "/index.html";
                    path = path.replace('/', File.separatorChar);
                } else {

                    path = path.replace('/', File.separatorChar);
                }
                //  System.out.println(path);


///////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////// DYNAMIC DYNAMIC DYNAMIC////////////////
                ///////////////////////////////////////////////////////////////////////////////////////


                ///LINKS
                if ((path.contains("?links=") && !(path.contains("mix?")))) //Checking if path is a dynamic link
                {
                    statuscode = 200;
                    Random ran = new Random();

                    int questionindex = path.indexOf("?");
                    String category = path.substring(1, questionindex);//Checking Category
                    if ((category.contentEquals("News")) || (category.contentEquals("Search")) || (category.contentEquals("Games"))) {

                        //Getting and parsing the m integer. If m is not an integer, go to exception and send error
                        int m = 0;
                        String numberm = path.substring(questionindex + 7, path.length());
                        if (numberm.isEmpty()) {
                            m = 0;
                        } else {
                            try {
                                m = Integer.parseInt(numberm);//parse m
                            } catch (Exception e) {
                                statuscode = 404;
                                String error = construct_http_header(statuscode, 0, logdate, "", "");
                                System.out.println("Server Response:" + "\r\n" + error);
                                error = error + "\r\n" + "<h2><U>Invalid Number of Links Entered</U></h2>"
                                        + "<h2><U>Please enter an integer between 0 and 10 in the URL as follows:</U></h2>"
                                        + "<h3>" + path.substring(0, path.indexOf("=") + 1) + "[Enter Integer Here]</h3>"
                                        + "</html>";
                                try {
                                    dataout.writeBytes(error);
                                } catch (IOException ex) {
                                    Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                //Logging, used repetativley throughout the code
                                try {
                                    String log;
                                    log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                                    bw.write(log);
                                    bw.newLine();
                                    bw.close();
                                    System.out.println(log);

                                } catch (Exception lda) {
                                }

                                break GET;
                            }
                        }
                        if (m > 10) {
                            m = 10;
                        }
                        if (m < 0) {
                            m = 0;
                        }
                        String linkdir = category + ".txt";
                        try {

                            //Setting bufferreaders to read from text file
                            BufferedReader reader = new BufferedReader(new FileReader(directory + "/protected/" + linkdir));
                            BufferedReader reader2 = new BufferedReader(new FileReader(directory + "/protected/" + linkdir));
                            String strLine;
                            String strLine2;
                            int counter = 0;
                            //Reading number of links in text file
                            while ((strLine2 = reader2.readLine()) != null) {
                                counter++;
                            }

                           // System.out.println(counter);

                            //Place html strings from text file in an array whose size is set by the number of links in the file
                            String[] linkarray;
                            linkarray = new String[counter];
                            int location = 0;
                            while ((strLine = reader.readLine()) != null) {
                                linkarray[location] = strLine;
                                location++;

                            }

                            //Construct header
                            String sendout = construct_http_header(statuscode, 0, logdate, "", "");
                            System.out.println("Server Response:\r\n");

                            sendout =sendout        + "\r\n"
                                    + "<head>\r\n"
                                    + "<title> EECE 450 Project: Dynamic Links</title>\r\n"
                                    + "</head>\r\n"
                                    + "<body bgcolor=\"#CACAFF\">\r\n"
                                    + "<h1><U>" + category + ":</U></h1>\r\n"
                                    + "<p><strong>Click on a link to go to that page.</strong></p>\r\n"
                                    + "<ul>\r\n";

                            //Shuffle function, used to generate random numbers

                            int[] shuffle = new int[counter];
                            for (int i = 0; i < shuffle.length; i++) {
                                shuffle[i] = i;
                            }


                            for (int i = 0; i < shuffle.length; i++) {
                                int randomPosition = ran.nextInt(shuffle.length);
                                int temp = shuffle[i];
                                shuffle[i] = shuffle[randomPosition];
                                shuffle[randomPosition] = temp;
                            }

                            // Write the retrieved strings into the send out string
                            for (int i = 0; i < m; i++) {
                                sendout = sendout + linkarray[shuffle[i]] + "\r\n";
                                // System.out.println(ran.nextInt(m));
                                // sendout=sendout+linkarray[ran.nextInt(m)]+"\r\n";

                            }


//        for( int i=0;i<10;i++)
//        {
//            System.out.println(shuffle[i]);
//        }


                            sendout = sendout + "</ul>"
                                    + "</body>\r\n"
                                    + "</html>\r\n";
                           // System.out.println(sendout);
                            dataout.writeBytes(sendout);//Write out the string to browser

                        } catch (IOException ex) {
                            Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //Logging
                        try {
                            String log;
                            log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                            bw.write(log);
                            bw.newLine();
                            bw.close();
                            System.out.println(log);

                        } catch (Exception e) {
                        }


                    } else {
                        //If category not found
                        statuscode = 404;
                        String sendout = construct_http_header(statuscode, 0, logdate, "", "");
                        System.out.println("Server Response:"+"\r\n"+sendout);
                        sendout = sendout
                                + "\r\n"
                                + "<html>"
                                + "<h1>404</h1>"
                                + ""
                                + "<h2><U>Requested Category Not Found</U></h2>"
                                + "<h2><U>The Accepted Categories are:</U></h2>"
                                + "<h3>-Search</h3>"
                                + "<h3>-News</h3>"
                                + "<h3>-Games</h3>"
                                + "</html>";


                        try {
                            dataout.writeBytes(sendout);//send string to browser
                            dataout.close();
                        } catch (Exception e) {
                        }
                        //Logging
                        try {
                            String log;
                            log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                            bw.write(log);
                            bw.newLine();
                            bw.close();
                            System.out.println(log);

                        } catch (Exception e) {
                        }


                    }


                }

                ////EMAIL
                else if (path.contains("emailto?="))// Check if email request
                {
                    statuscode = 200;
                    int equalindex = path.indexOf("=");
                    //Retreive mail address specified in URL
                    String mailadress = path.substring(equalindex + 1, path.length());
                    //Construct headers
                    String sendout = construct_http_header(statuscode, 0, logdate, "", "");
                    System.out.println("Server Response:" + "\r\n" + sendout);
                    sendout = sendout + "\r\n"
                            + "<head>\r\n"
                            + "<title> EECE 450 Project: Email</title>\r\n"
                            + "</head>\r\n"
                            + "<body bgcolor=\"#CACAFF\">\r\n"
                            + "<h1><U>Email Sending:</U></h1>\r\n"
                            + "<p><strong>Click on the link to send an email.</strong></p>\r\n"
                            + "<a href = " + "\"mailto:" + mailadress + "\">" + mailadress + "</a>";
                    try {
                        dataout.writeBytes(sendout);//Send
                    } catch (IOException ex) {
                        Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Logging
                    try {
                        String log;
                        log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                        bw.write(log);
                        bw.newLine();
                        bw.close();
                        System.out.println(log);

                    } catch (Exception e) {
                    }



                } ////IMAGES
                else if (path.contains("images?number=")) //Check if Dynamic images
                {
                    statuscode = 200;
                    int n = 0;
                    //Getting integer n
                    String numbern = path.substring(path.indexOf("=") + 1, path.length());
                    if (numbern.isEmpty()) {
                        n = 0;
                    } else {


                        try {
                            n = Integer.parseInt(numbern);//Parsing n

                        } catch (Exception e) {
                            //If n not integer
                            statuscode = 404;
                            String error = construct_http_header(statuscode, 0, logdate, "", "");
                            System.out.println("Server Response:" + "\r\n" + error);
                            error = error + "\r\n" + "<h2><U>Invalid Number of Images Entered</U></h2>"
                                    + "<h2><U>Please enter a valid integer in the URL as follows:</U></h2>"
                                    + "<h3>" + path.substring(0, path.indexOf("=") + 1) + "[Enter Integer Here]</h3>"
                                    + "</html>";
                            try {
                                dataout.writeBytes(error);
                            } catch (IOException ex) {
                                Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            //Logging
                            try {
                                String log;
                                log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                                bw.write(log);
                                bw.newLine();
                                bw.close();
                                System.out.println(log);

                            } catch (Exception lda) {
                            }

                            break GET;//Break out of GET loop

                        }
                    }
                    if (n < 0) {
                        n = 0;
                    }

//     if(n>20)
//     {
//         n=20;
//     }
//

                    try {

                        //Initializing readers to read from text file
                        BufferedReader reader = new BufferedReader(new FileReader(directory + "/protected/" + "images.txt"));
                        BufferedReader reader2 = new BufferedReader(new FileReader(directory + "/protected/" + "images.txt"));
                        String strLine;
                        String strLine2;
                        //Getting number of lines in text file
                        int counter = 0;
                        while ((strLine2 = reader2.readLine()) != null) {
                            counter++;
                        }
                        //Creating array to store lines in

                       // System.out.println(counter);
                        String[] linkarray;
                        linkarray = new String[counter];
                        int location = 0;
                        while ((strLine = reader.readLine()) != null) {
                            linkarray[location] = strLine;
                            location++;

                        }

                        //Constructing header
                        String sendout = construct_http_header(statuscode, 0, logdate, "", "");
                        System.out.println("Server Response:\r\n" + sendout);
                        sendout =sendout       + "\r\n"
                                + "<head>\r\n"
                                + "<title> EECE 450 Project: Images</title>\r\n"
                                + "</head>\r\n"
                                + "<body bgcolor=\"#CACAFF\">\r\n"
                                + "<h1><U>Images:</U></h1>\r\n"
                                + "<p><strong>Click on an image to go to its link.</strong></p>\r\n";
                        // Shuffle function to generate random integers
                        int shufflelong = 0;
//                        if (3 * n > counter) {
//                            shufflelong = counter / 3;
//                        } else {
//                            shufflelong = n;
//                        }
                      if ( n > counter) {
                            shufflelong = counter;
                        } else {
                            shufflelong = counter;
                        }
//                        int[] shuffle = new int[shufflelong];
//                        for (int i = 0; i < shufflelong; i++) {
//                            shuffle[i] = 3 * i;
//                          //  System.out.println(shuffle[i]);
//                        }
                         int[] shuffle = new int[shufflelong];
                        for (int i = 0; i < shufflelong; i++) {
                            shuffle[i] =  i;
                          //  System.out.println(shuffle[i]);
                        }

                        Random ran = new Random();
                        for (int i = 0; i < shuffle.length; i++) {
                            int randomPosition = ran.nextInt(shuffle.length);
                            int temp = shuffle[i];
                            shuffle[i] = shuffle[randomPosition];
                            shuffle[randomPosition] = temp;
                            //System.out.println(temp);

                        }

//                        for(int i=0; i<shuffle.length;i++)
//                        {
//                            System.out.println(shuffle[i]);
//                        }


                        //Writing strings into send out string randomly
                      int il=0;
                         for (int i = 0; i < n; i = i + 1) {
                           // if (i == counter) {
                            if((i % counter)==0)
                             {   il=0;
                               // System.out.println(i);
                                //System.out.println("Exceeded 6");
                                for (int j = 0; j < shuffle.length; j++) {
                                    int randomPosition = ran.nextInt(shuffle.length);
                                    int temp = shuffle[j];
                                    shuffle[j] = shuffle[randomPosition];
                                    shuffle[randomPosition] = temp;


                                }

                            }

                            sendout = sendout + linkarray[shuffle[il]] + "\r\n";

il=il+1;
                        }

                        sendout = sendout + "</body>\r\n"
                                + "</html>\r\n";
                       // System.out.println(sendout);
                        dataout.writeBytes(sendout);// send to data output

                    } catch (IOException ex) {
                        Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Logging
                    try {
                        String log;
                        log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                        bw.write(log);
                        bw.newLine();
                        bw.close();
                        System.out.println(log);

                    } catch (Exception e) {
                    }






                } ////MIX
                //Check to see if mix dynamic page
                else if ((path.contains("mix?")) && (path.contains("paragraphs=")) && (path.contains("links=")) && (path.contains("images="))) {
                    statuscode = 200;
                    //Retrieving integers, refer to methods used for links and images
                    int n = 0;
                    int m = 0;
                    int p = 0;
                    int indexpar = path.indexOf("paragraphs=");
                    int indexlinks = path.indexOf("links=");
                    int indeximage = path.indexOf("images=");
                    int indexcomp = path.indexOf(",", indexpar + 11);
                    int indexcomm = path.indexOf(",", indexlinks + 6);
                    int indexcomn = path.indexOf(",", indeximage + 7);
                    if (indexcomp == -1) {
                        indexcomp = path.length();
                    } else if (indexcomn == -1) {
                        indexcomn = path.length();
                    } else {
                        indexcomm = path.length();
                    }


                    String numberp = path.substring(indexpar + 11, indexcomp);
                    String numberm = path.substring(indexlinks + 6, indexcomm);
                    String numbern = path.substring(indeximage + 7, indexcomn);

                    //Parsing the intgers and checking for exceptions
                    //Refer to links and images above
                    if (numbern.isEmpty()) {
                        System.out.println("emtpy");
                        n = 0;

                    } else {

                        try {
                            n = Integer.parseInt(numbern);
                            System.out.println("bala");
                        } catch (Exception jljle) {
                            statuscode = 404;
                            String errorshow = path.replace(numbern, "[Enter Integer Here]");
                            String error = construct_http_header(statuscode, 0, logdate, "", "");
                            System.out.println("Server Response:" + "\r\n" + error);
                            error = error + "\r\n" + "<h2><U>Invalid Number of Images Entered</U></h2>"
                                    + "<h2><U>Please enter a valid integer in the URL as follows:</U></h2>"
                                    + "<h3>" + errorshow + "</h3>"
                                    + "</html>";
                            try {
                                dataout.writeBytes(error);
                            } catch (IOException ex) {
                                Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                String log;
                                log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                                bw.write(log);
                                bw.newLine();
                                bw.close();
                                System.out.println(log);

                            } catch (Exception lda) {
                            }

                            break GET;

                        }
                    }

                    if (numberm.isEmpty()) {
                        m = 0;
                    } else {
                        try {
                            m = Integer.parseInt(numberm);
                        } catch (Exception e) {
                            statuscode = 404;
                            String errorshow = path.replace(numberm, "[Enter Integer Here]");
                            String error = construct_http_header(statuscode, 0, logdate, "", "");
                            System.out.println("Server Response:" + "\r\n" + error);
                            error = error + "\r\n" + "<h2><U>Invalid Number of Links Entered</U></h2>"
                                    + "<h2><U>Please enter a valid integer between 1 and 10 in the URL as follows:</U></h2>"
                                    + "<h3>" + errorshow + "</h3>"
                                    + "</html>";
                            try {
                                dataout.writeBytes(error);
                            } catch (IOException ex) {
                                Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                String log;
                                log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                                bw.write(log);
                                bw.newLine();
                                bw.close();
                                System.out.println(log);

                            } catch (Exception lda) {
                            }

                            break GET;

                        }
                    }

                    if (numberp.isEmpty()) {
                        p = 0;
                    } else {
                        try {
                            p = Integer.parseInt(numberp);
                        } catch (Exception e) {
                            statuscode = 404;
                            String errorshow = path.replace(numberp, "[Enter Integer Here]");
                            String error = construct_http_header(statuscode, 0, logdate, "", "");
                            System.out.println("Server Response:" + "\r\n" + error);
                            error = error + "\r\n" + "<h2><U>Invalid Number of Paragraphs Entered</U></h2>"
                                    + "<h2><U>Please enter a valid integer between 1 and 10 in the URL as follows:</U></h2>"
                                    + "<h3>" + errorshow + "</h3>"
                                    + "</html>";
                            try {
                                dataout.writeBytes(error);
                            } catch (IOException ex) {
                                Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                String log;
                                log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                                bw.write(log);
                                bw.newLine();
                                bw.close();
                                System.out.println(log);

                            } catch (Exception lda) {
                            }

                            break GET;

                        }


                    }

                    if (n < 0) {
                        n = 0;
                    }
                    if (m < 0) {
                        m = 0;
                    }
                    if (p < 0) {
                        p = 0;
                    }
                    if (m > 10) {
                        m = 10;
                    }
                    if (p > 10) {
                        p = 10;
                    }
                    //Construct Header
                    String sendout = construct_http_header(statuscode, 0, logdate, "", "");
                        System.out.println("Server Response:\r\n");
                     sendout=sendout + "\r\n"
                            + "<head>\r\n"
                            + "<title> EECE 450 Project: Paragraphs, Links, and Images</title>\r\n"
                            + "</head>\r\n"
                            + "<body bgcolor=\"#CACAFF\">\r\n";


         /////////////mix image/////////////////////
                    try {
                        //Retrieving images, refer to dynamic images above
                        BufferedReader reader = new BufferedReader(new FileReader(directory + "/protected/" + "images.txt"));
                        BufferedReader reader2 = new BufferedReader(new FileReader(directory + "/protected/" + "images.txt"));
                        String strLine;
                        String strLine2;
                        int counter = 0;
                        while ((strLine2 = reader2.readLine()) != null) {
                            counter++;
                        }
                       // System.out.println(counter);
                        String[] linkarray;
                        linkarray = new String[counter];
                        int location = 0;
                        while ((strLine = reader.readLine()) != null) {
                            linkarray[location] = strLine;
                            location++;

                        }


                        sendout = sendout + "<h1><U>Images:</U></h1>\r\n"
                                + "<p><strong>Click on an image to go to its link.</strong></p>\r\n";

                        int shufflelong = counter;
//                        if (3 * n > counter) {
//                            shufflelong = counter / 3;
//                        } else {
//                            shufflelong = n;
//                        }
                        int[] shuffle = new int[shufflelong];
                        for (int i = 0; i < shufflelong; i++) {
                            shuffle[i] =  i;
                         //   System.out.println(shuffle[i]);
                        }

                        Random ran = new Random();
                        for (int i = 0; i < shuffle.length; i++) {
                            int randomPosition = ran.nextInt(shuffle.length);
                            int temp = shuffle[i];
                            shuffle[i] = shuffle[randomPosition];
                            shuffle[randomPosition] = temp;
                            //System.out.println(temp);
                        }

int il=0;
                        for (int i=0; i < n; i = i + 1) {
                           // if (i == counter) {
                            if((i%counter)==0)
                            {  il=0;
                               // System.out.println(i);
                                //System.out.println("Exceeded 6");
                                for (int j = 0; j < shuffle.length; j++) {
                                    int randomPosition = ran.nextInt(shuffle.length);
                                    int temp = shuffle[j];
                                    shuffle[j] = shuffle[randomPosition];
                                    shuffle[randomPosition] = temp;


                                }

                            }

                            sendout = sendout + linkarray[shuffle[il]] + "\r\n";
il=il+1;
                        }


                    } catch (IOException ex) {
                        Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    ///////////// MIX Links/////////

                    try {
                        //Generating mixed links, refer to dynamic links above
                        Random ran = new Random();
                        BufferedReader reader3 = new BufferedReader(new FileReader(directory + "/protected/" + "Games.txt"));
                        BufferedReader reader4 = new BufferedReader(new FileReader(directory + "/protected/" + "Games.txt"));
                        BufferedReader reader5 = new BufferedReader(new FileReader(directory + "/protected/" + "News.txt"));
                        BufferedReader reader6 = new BufferedReader(new FileReader(directory + "/protected/" + "News.txt"));
                        BufferedReader reader7 = new BufferedReader(new FileReader(directory + "/protected/" + "Search.txt"));
                        BufferedReader reader8 = new BufferedReader(new FileReader(directory + "/protected/" + "Search.txt"));
                        String strLine;
                        String strLine2;
                        String strLine3;
                        String strLine4;
                        String strLine5;
                        String strLine6;
                        int counter = 0;
                        //Combining strings from all category text files
                        while ((strLine2 = reader3.readLine()) != null) {
                            counter++;
                        }
                        while ((strLine4 = reader5.readLine()) != null) {
                            counter++;
                        }
                        while ((strLine6 = reader7.readLine()) != null) {
                            counter++;
                        }
                        // reader.
                      //  System.out.println(counter);
                        String[] linkarray;
                        linkarray = new String[counter];
                        int location = 0;
                        while ((strLine = reader4.readLine()) != null) {
                            linkarray[location] = strLine;
                            location++;

                        }
                        while ((strLine = reader6.readLine()) != null) {
                            linkarray[location] = strLine;
                            location++;

                        }
                        while ((strLine = reader8.readLine()) != null) {
                            linkarray[location] = strLine;
                            location++;

                        }

                        sendout = sendout
                                + "<h1><U>" + "Mixed Links" + ":</U></h1>\r\n"
                                + "<p><strong>Click on a link to go to that page.</strong></p>\r\n"
                                + "<ul>\r\n";

                        int[] shuffle = new int[counter];
                        for (int i = 0; i < shuffle.length; i++) {
                            shuffle[i] = i;
                        }


                        for (int i = 0; i < shuffle.length; i++) {
                            int randomPosition = ran.nextInt(shuffle.length);
                            int temp = shuffle[i];
                            shuffle[i] = shuffle[randomPosition];
                            shuffle[randomPosition] = temp;
                        }


                        for (int i = 0; i < m; i++) {
                            sendout = sendout + linkarray[shuffle[i]] + "\r\n";
                            // System.out.println(ran.nextInt(m));
                            // sendout=sendout+linkarray[ran.nextInt(m)]+"\r\n";

                        }



                        sendout = sendout + "</ul>" + "\r\n";

                    } catch (IOException ex) {
                        Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                    }


/////////////////////// Mix Paragraphs  ///////
                    try {
                        //Generating mixed paragraphs
                        //Reading from text file
                        BufferedReader reader3 = new BufferedReader(new FileReader(directory + "/protected/" + "Paragraphs.txt"));
                        BufferedReader reader4 = new BufferedReader(new FileReader(directory + "/protected/" + "Paragraphs.txt"));
                        String strLine;
                        String strLine2;
                        //checking number of lines in text file
                        int counter = 0;
                        while ((strLine2 = reader4.readLine()) != null) {
                            counter++;
                        }

                     //   System.out.println("counter:" + counter);
                        String[] linkarray;
                        linkarray = new String[counter];
                        int location = 0;
                        //creating array to store strings in
                        while ((strLine = reader3.readLine()) != null) {
                            linkarray[location] = strLine;
                            location++;

                        }
//                        for (int i = 0; i < linkarray.length; i++) {
//                            System.out.println(linkarray[i]);
//                        }
                        sendout = sendout
                                + "<h1><U>" + "Paragraphs" + ":</U></h1>\r\n"
                                + "<p><strong><U>Showing " + p + " Paragraphs:</U></strong></p>\r\n"
                                + "<ul>\r\n";

                        //Creating shuffle function for random paragraphs
                        int[] shuffle = new int[counter];
                        for (int i = 0; i < shuffle.length; i++) {
                            shuffle[i] = i;
                        }

                        Random ran = new Random();
                        for (int i = 0; i < shuffle.length; i++) {
                            int randomPosition = ran.nextInt(shuffle.length);
                            int temp = shuffle[i];
                            shuffle[i] = shuffle[randomPosition];
                            shuffle[randomPosition] = temp;
                        }
                        //Dividing strings if a - character appears. This is done to organize the strings in the text file as titles and paragraphs
                        String test = "";
                        for (int i = 0; i < p; i++) {
                            test = linkarray[shuffle[i]];
                            int find = 0;
                            int counts = 0;
                            while (find != -1) {

                                find = test.indexOf("-", find + 1);
                               // System.out.println(find);

                                if (find != -1) {
                                    counts++;
                                }
                            }
                            //write strings into send out
                            String[] newtest = new String[counts];
                            newtest = test.split("-");
                            for (int j = 0; j <= counts; j++) {
                                sendout = sendout + newtest[j] + "\r\n";
                            }


                        }

                        sendout = sendout + "</body>\r\n"
                                + "</html>\r\n";

                        // System.out.println(sendout);
                        dataout.writeBytes(sendout);// send out to data output stream



                        //System.out.println("Councts"+counts);

                    } catch (IOException ex) {
                        Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Logging
                    try {
                        String log;
                        log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                        bw.write(log);
                        bw.newLine();
                        bw.close();
                        System.out.println(log);

                    } catch (Exception e) {
                    }


                } ///////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////
                else {



                    File requestfile = new File(directory, path);
/////////////////////////////////////////////////////////
                    /////////////NOT FOUND//////////////
                    ///////////////////////////////////////////////////
                    //Checking to see if file not found
                    if (!requestfile.exists()) {
                        statuscode = 404;

                        //Get file type, used repetativley throughout code
                        int type_is = 0;
                        //find out what the filename ends with,
                        //so you can construct a the right content type
                        if (path.endsWith(".zip") || path.endsWith(".exe")
                                || path.endsWith(".tar")) {
                            type_is = 3;
                        }
                        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                            type_is = 1;
                        }
                        if (path.endsWith(".gif")) {
                            type_is = 2;
                            //write out the header, 200 ->everything is ok we are all happy.
                        }
                        if (path.endsWith(".html") || path.endsWith(".txt")) {
                            type_is = 5;
                        }
                        if (path.endsWith(".avi")) {
                            type_is = 6;
                        }
                            //write out the header, 200 ->everything is ok we are all happy.



                        //Send a 404 error message
                        String stringnull = "";
                        String nwstring = construct_http_header(statuscode, type_is, logdate, stringnull, stringnull);
                        System.out.println("Not Found");
                        System.out.println(nwstring);
                        nwstring = nwstring
                                + "\r\n"
                                + "<html>"
                                + "<h1>404</h1>"
                                + ""
                                + "<h2>NOT FOUND</h2>"
                                + "</html>";

                        try {
                            dataout.writeBytes(nwstring);//send to dataoutput stream
                            dataout.close();
                        } catch (Exception e) {
                        }

                        //Logging
                        try {
                            String log;
                            log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                            bw.write(log);
                            bw.newLine();
                            bw.close();
                            System.out.println(log);

                        } catch (Exception e) {
                        }
                        {
                        }


                    } else {


  ////////////////////////////////////////////////////////////////////////////////////////
                        //PROTECTED//
   ////////////////////////////////////////////

                        protect://Label used for breaking
                            //checking to see if entered protected directory
                        if (path.contains(File.separatorChar + "protected" + File.separatorChar)) {
                            // FileInputStream requestedfile3 = null;
                            //File file2 = new File(directory+ path);

                            //if request does not contain authorization, send authenticate header
                            String usernamepass = "";
                            int privateind = instr.indexOf("Authorization");
                            if (privateind == -1) {
                                String nwstring = null;

                                nwstring = "HTTP/1.1 401 Authorization Required"
                                        + "\r\n"
                                        + "WWW-Authenticate: Basic realm=\"Secure Area\""
                                        + "\r\n"
                                        + "\r\n"
                                        + "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""
                                        + "\"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\">"
                                        + "<HTML>"
                                        + "<HEAD>"
                                        + "<TITLE>Error</TITLE>"
                                        + "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=ISO-8859-1\">"
                                        + "</HEAD>"
                                        + "<BODY bgcolor=\"#CACAFF\"><H1>401 Unauthorized.</H1></BODY>"
                                        + "</HTML>";
                                try {
                                    dataout.writeBytes(nwstring);
                                    dataout.close();
                                } catch (Exception e) {
                                }
                                break GET;

                            } else {
                                //If authenticate exists, then check user name and password supplied
                                int i = privateind + 21;
                                while (instr.charAt(i) != '\n') {
                                    usernamepass += instr.charAt(i);
                                    i++;
                                }
                                //  System.out.println(usernamepass);
                                //decode retrieved username and password, then seperate
                                String x = "";
                                BASE64Decoder decoder = new BASE64Decoder();
                                try {
                                    byte[] decodedBytes = decoder.decodeBuffer(usernamepass);
                                    String user1 = "";
                                    String pass1 = "";
                                    usernamepass = "";
                                    for (i = 0; i < decodedBytes.length; i++) {
                                        x = new Character((char) decodedBytes[i]).toString();
                                        usernamepass += x;
                                    }
                                    user1 = usernamepass.substring(0, usernamepass.indexOf(":"));
                                    pass1 = usernamepass.substring(usernamepass.indexOf(":") + 1, usernamepass.length());

                                    //Check to see if username and password are correct, if yes , continue to
                                    //200, if no, resend authorization response
                                    if (user1.equals(user)) {
                                        if (pass1.equals(pass)) {

                                            break protect;
                                        }
                                    }

                                    //Sending authorization response
                                    String serverresponse = "HTTP/1.1 401 Authorization Required"
                                            + "\r\n"
                                            + "WWW-Authenticate: Basic realm=\"Secure Area\""
                                            + "\r\n"
                                            + "\r\n";


                                    statuscode = 401;
                                    System.out.println("Server Response: \r\n" + serverresponse);
                                    System.out.println("Unauthorized");
                                    dataout.writeBytes("HTTP/1.1 401 Authorization Required"
                                            + "\r\n"
                                            + "WWW-Authenticate: Basic realm=\"Secure Area\""
                                            + "\r\n"
                                            + "\r\n"
                                            + "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""
                                            + "\"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\">"
                                            + "<HTML>"
                                            + "<HEAD>"
                                            + "<TITLE>Error</TITLE>"
                                            + "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=ISO-8859-1\">"
                                            + "</HEAD>"
                                            + "<BODY bgcolor=\"#CACAFF\"><H1>401 Unauthorized.</H1></BODY>"
                                            + "</HTML>");
                                    //Logging
                                    try {
                                        String log;
                                        log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                                        bw.write(log);
                                        bw.newLine();
                                        bw.close();
                                        System.out.println(log);

                                    } catch (Exception e) {
                                    }

                                    break GET;

                                } catch (Exception e) {
                                }
                            }
                        }
////////////////////////////////////////////////////////////////////////////////
                        ///////////////Directory///////////////
  ///////////////////////////////////////////////////////////////////////////////

                        //Check to see if file requested is directory
                        if (requestfile.isDirectory()) {
                            System.out.println("Directory");
                            // String[] directory =requestfile.list();

                            //call listDirectory function, defined at end of code
                            try {
                                listDirectory(requestfile, dataout);
                            } catch (IOException ex) {
                                Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
/////////////////////////////////////////////////////////////////////////////
                        // 304 304 304//
////////////////////////////////////////////////////////////////////////

                        else {
                            loop2:
                                //Check to see if file last modified
                            if (instr.contains("If-Modified-Since")) {

                                int indexmod = instr.indexOf("If-Modified-Since");
                                indexmod = indexmod + 19;
                                int endlineindex = instr.indexOf("\n", indexmod);
                                String lastmodifieddate = instr.substring(indexmod, endlineindex);
                                //  System.out.println(lastmodifieddate);

                                //Defining date format
                                DateFormat moddate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");


                                Date datemodo = new Date();
                                //changing retrieved date to type date to compare with current file date
                                try {
                                    datemodo = moddate.parse(lastmodifieddate);
                                 //   System.out.println("SUCEESS");
                                } catch (Exception e) {
                                    System.out.println("Unsupported time format");

                                }
                                //Get current time and requested time into long integers
                                long clienttime = datemodo.getTime();
                                long servertime = requestfile.lastModified();

                                String clienttimestring = Long.toString(clienttime);
                                String servertimestring = Long.toString(servertime);
                                //Deleting last 3 digits for precision purposes
                                clienttimestring = clienttimestring.substring(0, clienttimestring.length() - 3);
                                servertimestring = servertimestring.substring(0, servertimestring.length() - 3);
//System.out.println(clienttimestring);
//System.out.println(servertimestring);

                                clienttime = Long.parseLong(clienttimestring);
                                servertime = Long.parseLong(servertimestring);

//System.out.println("Client Time:" + clienttime);
//System.out.println("Servertime:" + servertime);

                                //Comparing times to check if file modified or not
                                if (servertime > clienttime) {
                                    //File modified, go to 200
                                    System.out.print("File has been modified\n\n");
                                    break loop2;
                                } else {
                                    //File not modified, send 304 message to browser
                                    statuscode = 304;
                                    try {

                                        dataout.writeBytes("HTTP/1.1 304 Not Modified" + "\r\n" + "\r\n");

                                        System.out.println("304 Not Modified");
                                    } catch (IOException ex) {
                                        Logger.getLogger(Webserver.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    //Logging
                                    try {
                                        String log;
                                        log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                                        bw.write(log);
                                        bw.newLine();
                                        bw.close();
                                        System.out.println(log);

                                    } catch (Exception e) {
                                    }



                                    break GET;


                                }



                            }
                        }



///////////////////////////////////////////////////////////////////////////////
                        ///200 OK//
   //////////////////////////////////////////////////////////////////////////

                        //If file acceptable and not modified, send 200 message then data
                        statuscode = 200;
                        System.out.println("200 OK" + '\n');
                        String path2 = directory + path;
                        // System.out.println(path2);
                        String nwstring = null;
                        //  File requestedfile = null;
                        //  requestedfile = new File(path2);
                        FileInputStream requestedfile2 = null;

                        // retrieving information for the header such as
                        // date and content length...
                        int len = (int) requestfile.length();
                        totaldata=totaldata+len;
                        String length;
                        length = Integer.toString(len);
                        long datemodified = requestfile.lastModified();
                        Date datemod = new Date(datemodified);
                        DateFormat dateFormat2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                        String lastmodified = dateFormat2.format(datemod);

                        //Checking file type
                        int type_is = 0;
                        //find out what the filename ends with,
                        //so you can construct a the right content type
                        if (path2.endsWith(".zip") || path.endsWith(".exe")
                                || path2.endsWith(".tar")) {
                            type_is = 3;
                        }
                        if (path2.endsWith(".jpg") || path.endsWith(".jpeg")) {
                            type_is = 1;
                        }
                        if (path2.endsWith(".gif")) {
                            type_is = 2;
                            //write out the header, 200 ->everything is ok we are all happy.
                        }
                        if (path2.endsWith(".html") || path.endsWith(".txt")) {
                            type_is = 5;
                        }
                        if (path2.endsWith(".avi")) {
                            type_is = 6;
                        }


                        //construct header
                        nwstring = construct_http_header(statuscode, type_is, logdate, lastmodified, length);
                        System.out.println("Server Response:");
                        System.out.println(nwstring);
                        

                        try {

                            dataout.writeBytes(nwstring);//Send out response
                            //dataout.close();
                        } catch (Exception e) {
                        }

                        try {
                            requestedfile2 = new FileInputStream(path2);
                        } catch (Exception e) {
                        }


                        try {
                            while (true) {
                                //read the file from filestream, and print out through the
                                //client-outputstream on a byte per byte base.

                                int b = requestedfile2.read();
                                if (b == -1) {
                                    break; //end of file
                                }
                                dataout.writeByte(b);
                            }
                            dataout.close();
                            requestedfile2.close();


                        } catch (Exception e) {
                        }

                        //Logging
                        try {
                            String log;
                            log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode + '\n';
                            bw.write(log);
                            bw.newLine();
                            bw.close();
                            System.out.println(log);

                        } catch (Exception e) {
                        }
                        
                        

                    }

                }
            } else {
 ////////////////////////////////////////////////////////////////
          ///      //400/////////
  ////////////////////////////////////////////////////////////

                //If request not acceptable, send bad request error 400
                statuscode = 400;
                System.out.println("Bad Request");

                //Construct header
                String stringnull = "";
                String nwstring = construct_http_header(statuscode, 0, logdate, stringnull, stringnull);
                System.out.println(nwstring);
                nwstring = nwstring
                        + "\r\n"
                        + "<html>"
                        + "<h1>400</h1>"
                        + ""
                        + "<h2>Bad Request</h2>"
                        + "</html>";
                try {

                    dataout.writeBytes(nwstring);//Send to dataoutput stream
                    dataout.close();
                } catch (Exception e) {
                }




                //Logging
                try {
                    String log;
                    log = logdate + '\t' + ip.getHostAddress() + '\t' + browser + '\t' + path + '\t' + statuscode;
                    bw.write(log);
                    bw.newLine();

                    bw.close();
                    System.out.println(log);

                } catch (Exception e) {
                }
                {
                }




            }

             if(connex.contentEquals("close")){
                System.out.println("Closing Socket");}
 else{
               // System.out.println("keeeeep alliiiivee");
 }
            //close socket and input and output streams
          //  try {


               // datain.close();
               // dataout.close();
             //   System.out.println("sooocket sitrring"+socket.toString());
               // System.out.println("Closing Socket");
              //  socket.close();

           // } catch (IOException e) {
           //     System.out.println("Fail");
           // }
                }
            } catch (IOException e) {

                // We catched an exception like the socket timeout; close connection
               // System.out.println("connection at " + socket.toString() + " is closed: " + e.getMessage());
                try {
                 System.out.println("Closing Socket");
               //  System.out.println("Total requests: " +totalrequests);
                 socket.close();
                 datain.close();
                 dataout.close();
                } catch (IOException ee) {
                    System.out.println("got an IO exception: " + ee.getMessage());
                }
                return;
            }


///

//            System.out.println("Total Data transfered: "+ totaldata);
        }
    }

    //header construction function, constructs a string containing the following information
    private String construct_http_header(int return_code, int file_type, String dte, String lstmd, String cnt) {
        String s = "HTTP/1.1 ";
        //you probably have seen these if you have been surfing the web a while
        switch (return_code) {
            case 200:
                s = s + "200 OK";
                break;
            case 400:
                s = s + "400 Bad Request";
                break;
            case 401:
                s = s + "401 Authorization Required";
                break;
            case 404:
                s = s + "404 Not Found";
                break;
            case 304:
                s = s + "304 Not Modified";
                break;
        }

        s = s + "\r\n";
       // s = s + "Connection: close\r\n";
        // s=  s+ "Cache-Control: max-age=30, must-revalidate\r\n";

        s = s + "Date: " + dte + "\r\n";
        s = s + "Server: ECE 450 Server \r\n";
        if (!lstmd.equals("")) {
            s = s + "Last-Modified: " + lstmd + "\r\n";
        }

        if (!cnt.equals("")) {
            s = s + "Content-Length: " + cnt + "\r\n";
        }


        switch (file_type) {

            case 0:
                break;
            case 1:
                s = s + "Content-Type: image/jpeg\r\n";
                break;
            case 2:
                s = s + "Content-Type: image/gif\r\n";
                break;
            case 3:
                s = s + "Content-Type: application/zip\r\n";
                break;
            case 5:
                s = s + "Content-Type: text/html\r\n";
                break;
            case 6:
                s = s + "Content-Type: avi\r\n";
                break;
            default:
                s = s + "Content-Type: text/html\r\n";
                break;
        }

        ////so on and so on......
        s = s + "\r\n"; //this marks the end of the httpheader
        //and the start of the body
        //ok return our newly created header!
        return s;
    }

    //List directory function, returns required html strings to output for a directory request
    void listDirectory(File dir, DataOutputStream ps) throws IOException {
        ps.writeBytes("<TITLE>Directory listing</TITLE><P>\n");
        ps.writeBytes("<A HREF=\"..\">Parent Directory</A><BR>\n");
        //  System.out.println("in function");

        String[] list = dir.list();

        for (int i = 0; list != null && i < list.length; i++) {
            File f = new File(dir, list[i]);
            if (f.isDirectory()) {
                ps.writeBytes("<A HREF=\"" + list[i] + "/\">" + list[i] + "/</A><BR>");
            } else {
                ps.writeBytes("<A HREF=\"" + list[i] + "\">" + list[i] + "</A><BR");
            }
        }
        ps.writeBytes("<P><HR><BR><I>" + "</I>");
    }
}
