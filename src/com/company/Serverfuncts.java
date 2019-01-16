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
    public void LogIn(PrintWriter out, String str) throws ClassNotFoundException, SQLException {
        String[] Str;
        str = str.substring(6);
        Str = str.split("///"); //STR[0] = KULLANICI ADI
        /*for (String Str1 : Str)
        {
            System.out.println(Str1);
        }*/
        boolean usercontrol = false;
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/chat";
        String username = "javaapp";
        String password = "javaapp";
        Connection connection = null;
        try {
            connection = (Connection) DriverManager.getConnection(url, username, password);
            System.out.println("flag");
            SQLfuncts sqlfun = new SQLfuncts();
            ResultSet rs;
            rs = sqlfun.SearchForUser(connection, Str[0]);
            while (rs.next()) {
                usercontrol = true; // USER IS EXISTS
                //System.out.println(rs.getString("Username").trim());
                if (rs.getString("Password").trim().equals(Str[1])) // CHECK PASSWORD
                {

                    String message = "";
                    /*message+=rs.getString("Name").trim()+"///";
                    message+=rs.getString("SurnameName").trim();
                   */

                    message = sqlfun.getOnlineUsers(connection, Str[0]);
                    message = message.substring(0, message.length() - 3);
                    out.println("true:" + rs.getString("Name") + "///" + rs.getString("Surname") + "///" + message); //GIVE A RETURN TO CLIENT
                    //   System.out.println("Message Delivered");
                    //   System.out.println("true:"+rs.getString("Name")+"///"+rs.getString("Surname")+"///"+message); //GIVE A RETURN TO CLIENT
                } else {
                    //    System.out.println("Message error"); // OR GIVE AN ERROR THAT PASSWORD IS WRONG
                    out.println("error");
                }
            }
            if (!usercontrol) // IF USER NOT FOUND
            {
                // System.out.println("user not found");
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

    public void InsertNewUser(PrintWriter out, String str) throws SQLException, ClassNotFoundException {
        String[] Str;
        str = str.substring(11);
        Str = str.split("///");
        //   for (String Str1 : Str)
        //  {
        //    System.out.println(Str1);
        //  }
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/chat";
        String username = "javaapp";
        String password = "javaapp";
        try (Connection connection = (Connection) DriverManager.getConnection(url, username, password)) {
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
        } catch (Exception e) {
            //     System.out.println("An error occured while connecting to database.");
        }
    }

    public void SendMessage(PrintWriter out, String str) throws SQLException, ClassNotFoundException, IOException {
        String[] Str;
        int[] IDs = new int[2];

        str = str.substring(12);
        Str = str.split("///");
        //System.out.println("File flag: now here 1");
        // for (String Str1 : Str)
        //{
        // System.out.println(Str1); //STR[0] = ME STR[1] = OTHER;
        //}
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/chat";
        String username = "javaapp";
        String password = "javaapp";
        try (Connection connection = (Connection) DriverManager.getConnection(url, username, password)) {
            SQLfuncts sqlfun = new SQLfuncts();
            IDs = sqlfun.getIdOfTwouser(connection, Str);


        }
        HandleFiles(IDs);

        PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter(
                "C:/Users/Yusuf/Documents/Chats/OldMessages/" + IDs[0] + "/" + IDs[1] + ".txt", true)));
        pw2.println("SentFromMe:" + Str[2]);
        pw2.flush();
        pw2.close();

        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                "C:/Users/Yusuf/Documents/Chats/NewMessages/" + IDs[1] + "/" + IDs[0] + ".txt", true)));
        pw.println("SentFromOther:" + Str[2]);
        pw.flush();
        pw.close();


        out.println("true:");

    }

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
        String url = "jdbc:mysql://localhost:3306/chat";
        String username = "javaapp";
        String password = "javaapp";
        try (Connection connection = (Connection) DriverManager.getConnection(url, username, password)) {
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

    public BufferedReader OpenBuffReader(Socket clientSocket) throws IOException {
        InputStream IS = clientSocket.getInputStream();
        InputStreamReader ISR = new InputStreamReader(IS);
        return new BufferedReader(ISR);
    }

    public BufferedWriter OpenBuffWriter(Socket clientSocket) throws IOException {
        OutputStream OS = clientSocket.getOutputStream();
        OutputStreamWriter OSR = new OutputStreamWriter(OS);
        return new BufferedWriter(OSR);
    }

}

