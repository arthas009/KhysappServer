package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

public class ClientThread extends Thread {
    protected Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            respond();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void respond() throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        Serverfuncts sfuncts = new Serverfuncts();
        BufferedReader in = sfuncts.OpenBuffReader(socket);
        BufferedWriter BW = sfuncts.OpenBuffWriter(socket);
        PrintWriter out = new PrintWriter(BW, true);
        while (true) {
            String str = null;
            try {
                str = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
                if (str.startsWith("LogIn:")) {
                    System.out.println("Oh, a Log in request. Checking..");
                    sfuncts.LogIn(out, str);
                    break;
                } else if (str.startsWith("InsertUser:")) {
                    System.out.println("Oh, a Sign in request. Checking..");
                    sfuncts.InsertNewUser(out, str);
                    break;
                } else if (str.startsWith("SendMessage:")) {
                    System.out.println("Oh, a Send Message request. Checking..");
                    sfuncts.SendMessage(out, str);
                    break;
                } else if (str.startsWith("DrawMessages:")) {
                    System.out.println("Oh, a Draw Messages request. Checking..");
                    sfuncts.DrawMessages(out, str);
                    break;
                } else if (str.startsWith("CheckMessages:")) {
                    System.out.println("Oh, a CheckMessages request. Checking..");
                    sfuncts.CheckForMessages(out, str);
                    break;
                }
        }
    }
}
