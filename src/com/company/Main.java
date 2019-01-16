package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {

        int portNumber = 7788;
        startListening(portNumber);
    }

    public static void startListening(int portNumber) throws IOException {
        ServerSocket serverSocket;
        Socket clientSocket;
        serverSocket = new ServerSocket(portNumber);
        while (true) {
            System.out.println("I listen");
            try {
                clientSocket = serverSocket.accept();
                System.out.println("There is a request from client");
                new ClientThread(clientSocket).start();
            } catch (Exception e) {
                e.printStackTrace();
                startListening(portNumber);
            }
        }

    }
}