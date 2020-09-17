package com.shahriar.xenaecosystem;

import android.os.Build;

import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class notification_sync_service extends Thread {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {

        Socket server = conect_to_server();

        String output;
        while (true) {

        }
    }


    Socket conect_to_server() {
        Socket sock = null;

        try {
            sock = new Socket("3.1.5.104", 4422);
            send(sock, "hello");

            String text = recv(sock);
            Log.d("from server", text);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sock;
    }

    void send(Socket sock, String text) {
        String text_size = (String.valueOf(text.length()));

        try {
            PrintWriter outs = new PrintWriter(sock.getOutputStream(), true);
            outs.println("buffer: " + text_size);
            recv(sock);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PrintWriter outs = new PrintWriter(sock.getOutputStream(), true);

            outs.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    String recv(Socket sock) {
        String str = "null";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            str = br.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

}
