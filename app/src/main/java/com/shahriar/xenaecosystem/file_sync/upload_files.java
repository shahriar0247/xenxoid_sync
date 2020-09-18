package com.shahriar.xenaecosystem.file_sync;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.shahriar.xenaecosystem.connect.Connect_To_Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class upload_files implements Runnable {

    String ip;
    String password;

    public upload_files(String temp_ip, String temp_password) {
        ip = temp_ip;
        password = temp_password;
    }

    public List<String> files_to_sync = new ArrayList<>();
    String current_location = Environment.getExternalStorageDirectory().toString();
    String local_location = "";
    List<File_sync.folder> all_folders;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {

        Socket server = conect_to_server();

        String output;
        while (true) {
            if (files_to_sync != null) {
                sync_once(server);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void sync_once(Socket server){
        try {
            for (String file_to_sync : files_to_sync) {
                download2(server, file_to_sync);
            }
        }
        catch (ConcurrentModificationException e){
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            sync_once(server);
        }
    }

    Socket conect_to_server() {
        Socket sock = null;

        try {
            sock = new Socket(ip, 4422);
            send(sock, password);
            String pass_from_server = recv(sock);

            if (pass_from_server == password){
                return sock;
            }
            else{
               return null;
            }

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    void download2(Socket server, String requested_file) {
        String output = "null";

        String file_to_download_loc = current_location + "/" + requested_file;
        File file = new File(file_to_download_loc);


        if (file.isDirectory()) {

            List<String> all_files = getallfiles(file);
            send(server, "dir");
            recv(server);
            send(server, requested_file);
            recv(server);
            send(server, "number_of_files: " + all_files.size());

            for (String file1 : all_files) {

                download2(server, requested_file + "/" + file1);

            }
        } else if (file.isFile()) {
            send(server, "file");
            recv(server);
            downloadfile(server, file_to_download_loc, requested_file);
            recv(server);
            send(server, "ok");


        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void downloadfile(Socket server, String file_to_download, String requested_file) {
        try {
            File file = new File(file_to_download);
            send(server, "downloading " + requested_file);

            String server_send = recv(server);

            if (server_send.startsWith("send")) {
                send(server, "ok");
                byte[] image_array = Files.readAllBytes(Paths.get(file_to_download));
                DataOutputStream dos = new DataOutputStream(server.getOutputStream());

                dos.writeInt(image_array.length);
                dos.write(image_array);
            } else if (server_send.startsWith("dont")) {
                send(server, "ok");
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private List<String> getallfiles(File directory) {
        final File[] files = directory.listFiles();
        List<String> files2 = new ArrayList<String>();
        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    if (file.isDirectory()) {
                        files2.add(file.getName());
                    } else {
                        files2.add(file.getName());

                    }
                }
            }
        }
        return files2;
    }

    String cd(Socket server, String cmd) {
        send(server, "current_location: " + current_location);
        String output = recv(server);
        current_location = output.split("newpath:")[1];
        return "current path set to:" + current_location;
    }
}
