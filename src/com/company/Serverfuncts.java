package com.company;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Serverfuncts {
    ////////////////////********************------FUNCTIONS--------********////////////////////////////////////////////////////
    /* --THIS FUNCTION HANDLES THE LOGIN REQUESTS. INCOMING MESSAGE INCLUDES USERNAME AND PASSWORD INFORMATION
       --THE STRING COMES FROM CLIENT INCLUDES A HEADER LIKE LogIn: AND A SPLITTER '///'
       --FIRSTLY, I AM REMOVING THE HEADER AND SPLITTING STRING INTO 2 PART. USERNAME AND PASSWORD
       --THEN I CHECK USERNAME FROM MYSQL SERVER. IF USERS EXISTS AND PASSWORD IS TRUE, THEN I AM SENDING BACK AN ACKNOWLEDGEMENT TO CLIENT
         WITH A HEADER 'true' AND SENDING THE WHOLE USER'S USERNAME LIST IN THIS STRING
     */
    public void LogIn(PrintWriter out, String str) throws ClassNotFoundException, SQLException {
        String[] Str;
        str = str.substring(6);
        Str = str.split("///"); //STR[0] = USERNAME
        boolean usercontrol = false; // WE WILL USE THIS WHILE CHECKING USER
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = null;
        try {
            connection = (Connection) DriverManager.getConnection(MySQLinfo.url, MySQLinfo.username, MySQLinfo.password);
            System.out.println("flag");
            SQLfuncts sqlfun = new SQLfuncts();
            ResultSet rs;
            rs = sqlfun.SearchForUser(connection, Str[0]);
            while (rs.next()) {
                usercontrol = true; // USER IS EXISTS
                if (rs.getString("Password").trim().equals(Str[1])) // CHECK IF PASSWORD EQUALS TO THE PASSWORD IN THE TABLE
                {
                    String message = "";
                    message = sqlfun.getUsers(connection, Str[0]); // GET ALL USERS IN DATABASE
                    message = message.substring(0, message.length() - 3);
                    out.println("true:" + rs.getString("Name") + "///" + rs.getString("Surname") + "///" + message); //GIVE A RETURN TO CLIENT
                }
                else
                {
                    out.println("error");
                }
            }
            if (!usercontrol) // IF USER NOT FOUND
            {
                out.println("unf");
            }
            connection.close();
        } // CONNECTION TRY BLOCK
        catch (Exception e) {
            if (connection != null)
                connection.close();
            e.printStackTrace();
            return;
        }
    }

    /* --THIS FUNCTION HANDLES THE REGISTER USER REQUESTS. INCOMING MESSAGE INCLUDES USERNAME, PASSWORD, PHONE NUMBER AND ETC.
      --THE STRING COMES FROM CLIENT INCLUDES A HEADER LIKE InsertUser: AND A SPLITTER '///'
      --FIRSTLY, I AM REMOVING THE HEADER AND SPLITTING STRING INTO PARTS.
      --THEN I CHECK USERNAME FROM MYSQL SERVER IF ITS ALREADY EXISTS. IF IT IS, I AM SENDING AN ACKNOWLEDGEMENT TO CLIENT
        WITH A HEADER 'AnotherUserFound'. IF NOT, I AM SENDING ACKNOWLEDGEMENT AS 'UserRegistered'.
    */
    public void InsertNewUser(PrintWriter out, String str) throws SQLException, ClassNotFoundException {
        String[] Str;
        str = str.substring(11);
        Str = str.split("///");
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = null;
        try {
            connection = (Connection) DriverManager.getConnection(MySQLinfo.url, MySQLinfo.username, MySQLinfo.password)
            boolean control = false;
            SQLfuncts sqlfun = new SQLfuncts();
            ResultSet rs;
            rs = sqlfun.SearchForUser(connection, Str[3]);
            while (rs.next()) {
                control = true;
            }
            if (control) {
                out.println("AnotherUserFound");
                return;
            }
            sqlfun.InsertNewUser(connection, Str[0], Str[1], Str[2], Str[3], Str[4], Str[5], Str[6]);
            out.println("UserRegistered");
            connection.close();
        } catch (Exception e)
        {

        }
    }
    /*   --THIS FUNCTION HANDLES THE SEND MESSAGE REQUESTS. INCOMING STRING INCLUDES SENDER USER ID, RECEIVER USER ID AND MESSAGE BODY
         --THE STRING COMES FROM CLIENT INCLUDES A HEADER LIKE SendMessage: AND A SPLITTER '///'
         --FIRSTLY, I AM REMOVING THE HEADER AND SPLITTING STRING INTO PARTS.
         --THEN I PRINT MESSAGE TO FILES AND SENDING A ACKNOWLEDGENEMENT AS 'true:'
       */
    public void SendMessage(PrintWriter out, String str) throws ClassNotFoundException, SQLException {
        String[] Str;
        int[] IDs = new int[2];
        str = str.substring(12);
        Str = str.split("///");
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = null;
        try {
            connection = (Connection) DriverManager.getConnection(MySQLinfo.url, MySQLinfo.username, MySQLinfo.password)
            SQLfuncts sqlfun = new SQLfuncts();
            IDs = sqlfun.getIdOfTwouser(connection, Str);
        }
        catch (Exception e)
        {
            if (connection != null)
                connection.close();
            e.printStackTrace();
            return;
        }
        try {
            HandleFiles(IDs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // WRITE MESSAGE TO SENDER'S OLDMESSAGES RECORD. OLD MESSAGES CONTAINS THE INFORMATION OF HISTORY OF THE CURRENT CHAT.
        // SENT FROM ME TAG INDICATES THAT MESSAGES THAT SENT FROM THE CURRENT USER SHOULD BE LOCATED ON RIGHT
        // SENT FROM OTHER TAG INDICATES THAT MESSAGES THAT SENT FROM OTHER USER SHOULD BE LOCATED ON LEFT
        PrintWriter pw2 = null;
        try {
            pw2 = new PrintWriter(new BufferedWriter(new FileWriter(
                    "C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0] + "/" + IDs[1] + ".txt", true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // MESSAGE IS SENT FROM SENDER. I MEAN THIS MESSAGE SHOULD BE LOCATED ON RIGHT OF THE SENDERS CLIENT SCREEN.
        pw2.println("SentFromMe:" + Str[2]);/* SentFromMe: header gives us the information of whether the message should be located on left or right. SentFromMe headed messages
        are on left*/
        pw2.flush();
        pw2.close();

        // WRITE MESSAGE TO RECEIVER'S NEWMESSAGES RECORD.
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(
                    "C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[1] + "/" + IDs[0] + ".txt", true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // MESSAGE IS SENT FROM SENDER. I MEAN ANOTHER USER. THIS MESSAGE SHOULD BE LOCATED ON RIGHT OF THE RECEIVERS CLIENT SCREEN.
        pw.println("SentFromOther:" + Str[2]); /* SentFromOther: header gives us the information of whether the message should be located on left or right. SentFromOther headed messages
        are on left*/
        pw.flush();
        pw.close();

        out.println("true:");

    }

    /*   --THIS FUNCTION HANDLES THE SEND MESSAGE REQUESTS. INCOMING STRING INCLUDES SENDER USER ID, RECEIVER USER ID AND MESSAGE BODY
         --THE STRING COMES FROM CLIENT INCLUDES A HEADER LIKE SendMessage: AND A SPLITTER '///'
         --FIRSTLY, I AM REMOVING THE HEADER AND SPLITTING STRING INTO PARTS.
         --THEN I PRINT MESSAGE TO FILES AND SENDING A ACKNOWLEDGENEMENT AS 'true:'
       */
    public void DrawMessages(PrintWriter out, String str) throws SQLException, ClassNotFoundException, IOException, InterruptedException {

        String[] Str;
        int[] IDs = new int[2];

        str = str.substring(13);
        Str = str.split("///");
        //  System.out.println("File flag: now here 1");
        //  for (String Str1 : Str)
        // {
        //  System.out.println(Str1); //STR[0] = ME STR[1] = OTHER;
        //  }
        Class.forName("com.mysql.jdbc.Driver");
        try (Connection connection = (Connection) DriverManager.getConnection(MySQLinfo.url, MySQLinfo.username, MySQLinfo.password)) {
            SQLfuncts sqlfun = new SQLfuncts();
            IDs = sqlfun.getIdOfTwouser(connection, Str);
        }
        String messagestosend = "";
        HandleFiles(IDs);
        Thread.sleep(5);
        BufferedReader b = new BufferedReader(new FileReader("C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[0] + "/" + IDs[1] + ".txt"));
        String readLine = "";
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0] + "/" + IDs[1] + ".txt", true)));
        while ((readLine = b.readLine()) != null) {
            // System.out.println(readLine);
            pw.println(readLine);
            pw.flush();
        }
        pw.close();
        b.close();
        FileReader fr = new FileReader("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0] + "/" + IDs[1] + ".txt");
        BufferedReader b2 = new BufferedReader(fr);
        readLine = "";
        Thread.sleep(5);
        while ((readLine = b2.readLine()) != null) {
            //  System.out.println(readLine);
            messagestosend += readLine + "///";
        }

        b2.close();
        fr.close();
        out.println("true:" + messagestosend);
        FileWriter writer = new FileWriter("C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[0] + "/" + IDs[1] + ".txt");
        writer.write("");
        writer.close();


    }

    public void CheckForMessages(PrintWriter out, String str) throws InterruptedException, SQLException, IOException, ClassNotFoundException {
        String[] Str;
        int[] IDs = new int[2];

        str = str.substring(14);
        Str = str.split("///");
        //  for (String Str1 : Str)
        //  {
        //  System.out.println(Str1); //STR[0] = ME STR[1] = OTHER;
        //  }
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/chat";
        String username = "javaapp";
        String password = "javaapp";
        try (Connection connection = (Connection) DriverManager.getConnection(url, username, password)) {
            SQLfuncts sqlfun = new SQLfuncts();
            IDs = sqlfun.getIdOfTwouser(connection, Str);
        }

        boolean nmf = false;
        HandleFiles(IDs);
        Thread.sleep(5);
        BufferedReader b = new BufferedReader(new FileReader("C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[0] + "/" + IDs[1] + ".txt"));
        String readLine = "";
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0] + "/" + IDs[1] + ".txt", true)));
        while ((readLine = b.readLine()) != null) {
            nmf = true;
        }
        if (nmf)
            out.println("true:");
        else
            out.println("false:");

    }
    ////////////////////********************------SECONDARY FUNCTIONS--------********////////////////////////////////////////////////////

    public void HandleFiles(int[] IDs) throws IOException // CHAT FILES TO OPEN
    {
        // System.out.println("File flag: now here 2");
        File sender2;
        File sender = new File("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0]);
        if (!sender.exists()) {
            sender.mkdirs();
        }
        sender = new File("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[1]);
        if (!sender.exists()) {
            sender.mkdirs();
        }
        sender = new File("C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[1]);
        if (!sender.exists()) {
            sender.mkdirs();
        }

        sender = new File("C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[0]);
        if (!sender.exists()) {
            sender.mkdirs();
        }
        sender2 = new File("C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[0] + "/" + IDs[1] + ".txt");
        if (!sender2.exists()) {
            sender2.createNewFile();
        }
        sender2 = new File("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[1] + "/" + IDs[0] + ".txt");
        if (!sender2.exists()) {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[1] + "/" + IDs[0] + ".txt", true)));
            pw.println("SentFromOther:START CHATTING");
            pw.flush();
            pw.close();
            sender2.createNewFile();
        }
        sender2 = new File("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0] + "/" + IDs[1] + ".txt");
        if (!sender2.exists()) {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0] + "/" + IDs[1] + ".txt", true)));
            pw.println("SentFromOther:START CHATTING");
            pw.flush();
            pw.close();
            sender2.createNewFile();
        }
        sender2 = new File("C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[1] + "/" + IDs[0] + ".txt");
        if (!sender2.exists()) {
            sender2.createNewFile();
        }
    }

    /* buffered reader opener*/
    public BufferedReader OpenBuffReader(Socket clientSocket) throws IOException {
        InputStream IS = clientSocket.getInputStream();
        InputStreamReader ISR = new InputStreamReader(IS);
        return new BufferedReader(ISR);
    }
    /* bufferedwriter opener */
    public BufferedWriter OpenBuffWriter(Socket clientSocket) throws IOException {
        OutputStream OS = clientSocket.getOutputStream();
        OutputStreamWriter OSR = new OutputStreamWriter(OS);
        return new BufferedWriter(OSR);
    }

}

