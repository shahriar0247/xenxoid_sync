package com.shahriar.xenaecosystem;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import org.w3c.dom.Text;

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

import java.util.List;
import java.util.concurrent.Executor;


public class Activity1 extends AppCompatActivity {


    private String current_location = "null";
    public List<String> files_to_sync = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);

        connect thread = new connect();
        thread.start();

        LinearLayout main_layout = getmainlayout();
        List<String> all_files = ls(Environment.getExternalStorageDirectory());
        List<folder> all_folders = create_folders(all_files, main_layout);
        //files_to_sync = get_files_to_sync(all_folders);

        Runnable r = new getting_files_to_sync_runnable(all_folders);
        new Thread(r).start();

    }

    List<String> ls(File directory) {
        Log.d("filesofdir", "Directory: " + directory.getAbsolutePath() + "\n");

        final File[] files = directory.listFiles();
        List<String> files2 = new ArrayList<String>();
        ;
        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    Log.d("filesofdir", file.getName() + "\n");
                    files2.add(file.getName());
                }
            }
            java.util.Collections.sort(files2);
            return files2;
        }

        return null;
    }

    ArrayList<folder> create_folders(List<String> files, LinearLayout main_layout){

        ArrayList<folder> all_folders = new ArrayList<folder>();
        for (String filename: files){


            // setting main layout
            FrameLayout.LayoutParams main_layout_params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            main_layout_params.setMargins(32, 32, 32, 32);
            main_layout.setLayoutParams(main_layout_params);

            // setting layout 1
            ConstraintLayout layout1 = new ConstraintLayout(this);
            layout1.setId(View.generateViewId());

            // setting layout 2
            ConstraintLayout layout2 = new ConstraintLayout(this);
            layout2.setBackground(ContextCompat.getDrawable(this, R.drawable.folder_back));
            layout2.setId(View.generateViewId());
            ConstraintLayout.LayoutParams layout2_params = new ConstraintLayout.LayoutParams(
                   400,
                  100
            );
            layout2.setLayoutParams(layout2_params);
            layout1.addView(layout2);

            // setting upload_check
            CheckBox upload_check = new CheckBox(this);
            upload_check.setId(View.generateViewId());
            layout1.addView(upload_check);

            // setting sync text
            TextView sync = new TextView(this);
            sync.setText("Sync");
            sync.setId(View.generateViewId());
            layout1.addView(sync);

            // setting layout 1 constrains
            ConstraintSet layout1set = new ConstraintSet();
            layout1set.clone(layout1);
            layout1set.connect(layout2.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 32);
            layout1set.connect(upload_check.getId(), ConstraintSet.RIGHT, layout1.getId(), ConstraintSet.RIGHT, 32);
            layout1set.connect(sync.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 15);
            layout1set.connect(sync.getId(), ConstraintSet.RIGHT, upload_check.getId(), ConstraintSet.LEFT, 16);
            layout1set.applyTo(layout1);





            TextView file_title = new TextView(this);
            file_title.setText(filename);
            file_title.setTextColor(Color.rgb(22,22,22));
            file_title.setId(View.generateViewId());
            layout2.addView(file_title);



            ConstraintSet cs = new ConstraintSet();
            cs.clone(layout2);

            cs.connect(file_title.getId(), ConstraintSet.TOP, layout2.getId(), ConstraintSet.TOP,8);
            cs.connect(file_title.getId(), ConstraintSet.BOTTOM, layout2.getId(), ConstraintSet.BOTTOM,8);
            cs.connect(file_title.getId(), ConstraintSet.LEFT, layout2.getId(), ConstraintSet.LEFT, 32);
            cs.connect(file_title.getId(), ConstraintSet.RIGHT, layout2.getId(), ConstraintSet.RIGHT, 32);
            cs.applyTo(layout2);


            main_layout.addView(layout1);
            all_folders.add(new folder(filename, upload_check));

        }
        return  all_folders;
    }

    List<String> get_files_to_sync(List<folder> all_folders){
        ArrayList<String> files_to_sync = new ArrayList<String>();
        for (folder one_folder: all_folders){

                if (one_folder.upload_check.isChecked()) {
                    files_to_sync.add(one_folder.filename);
                }


        }
        return files_to_sync;
    }

    LinearLayout getmainlayout(){
        LinearLayout main_layout = findViewById(R.id.main_layout);
        return main_layout;
    }
    class folder{
        String filename;
        CheckBox upload_check;
        folder(String filename, CheckBox upload_check){
            this.filename = filename;
            this.upload_check = upload_check;
        }
    }

    public class getting_files_to_sync_runnable implements Runnable {

        public List<folder> all_folders;

        public getting_files_to_sync_runnable(List<folder> local_all_folders) {
            all_folders = local_all_folders;
            // store parameter for later user
        }

        public void run() {
            while (true) {
                files_to_sync = get_files_to_sync(all_folders);
            }
        }
    }

    class connect extends Thread {

        String currentpath = Environment.getExternalStorageDirectory().toString();


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {

            Socket server = conect_to_server();

            String output;
            while (true) {
                if (files_to_sync != null) {
                    for (String file_to_sync : files_to_sync) {
                        download2(server, file_to_sync);
                    }
                }
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
            Log.d("text size", text_size);
            try {
                PrintWriter outs = new PrintWriter(sock.getOutputStream(), true);
                outs.println("buffer: " + text_size);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("recv", recv(sock));
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

            String file_to_download_loc = currentpath + "/" + requested_file;
            File file = new File(file_to_download_loc);


            if (file.isDirectory()) {

                List<String> all_files = getallfiles(file);
                send(server, "dir");

                send(server, requested_file);

                send(server, "number_of_files: " + all_files.size());

                for (String file1 : all_files) {

                    download2(server, requested_file + "/" + file1);

                }
            } else if (file.isFile()) {
                send(server, "file");
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
                    send(server,"ok");
                    byte[] image_array = Files.readAllBytes(Paths.get(file_to_download));
                    DataOutputStream dos = new DataOutputStream(server.getOutputStream());

                    dos.writeInt(image_array.length);
                    dos.write(image_array);
                }
                else if(server_send.startsWith("dont")){
                    send(server,"ok");
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

        }

        private List<String> getallfiles(File directory) {
            Log.d("filesofdir", "Directory: " + directory.getAbsolutePath() + "\n");

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
            send(server, "currentpath: " + currentpath);
            String output = recv(server);
            currentpath = output.split("newpath:")[1];
            return "current path set to:" + currentpath;
        }

        String list_all_apps() {
            try {
                final PackageManager pm = getPackageManager();

                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                String all_apps = null;


                for (ApplicationInfo packageInfo : packages) {
                    ApplicationInfo appinfo = pm.getApplicationInfo(packageInfo.packageName, 0);
                    all_apps = all_apps + "\n" + appinfo.loadLabel(pm);
                }
                return all_apps;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return "failed to load apps";
        }

        String shell(String cmd) {

            Process process = null;
            try {
                process = Runtime.getRuntime().exec(cmd);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = bufferedReader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                bufferedReader.close();

                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                int read2;
                char[] buffer2 = new char[4096];
                StringBuffer output2 = new StringBuffer();
                while ((read2 = bufferedReader2.read(buffer2)) > 0) {
                    output2.append(buffer2, 0, read2);
                }
                bufferedReader2.close();
                // Waits for the command to finish.
                process.waitFor();
                String output3 = output.toString() + output2.toString();
                Log.d("shell_command ", output3);

                return output3;

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return "error";
        }




        String loc_call(String action){
            if (action.contains("scan")) {
                if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    Runnable r = new Runnable() {
                        public void run() {
                            scan_loc();
                        }
                    };

                    Activity1.this.runOnUiThread(r);
                    return "Scanning";
                } else {
                    return "Permission denied";
                }
            }
            else if (action.contains("stop")){

                Runnable r = new Runnable() {
                    public void run() {
                        stop_loc();
                    }
                };

                Activity1.this.runOnUiThread(r);
            }
            return "Stopped";
        }

        String getlocation2(){
            return current_location;
        }

    }
    LocationManager locationManager;
        @SuppressLint("MissingPermission")
        void scan_loc() {


                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,loc_listen);




        }

        void stop_loc(){
            locationManager.removeUpdates(loc_listen);
        }
    LocationListener loc_listen=new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            current_location = "Latitude: " + location.getLatitude() + "Longitude: " + location.getLongitude();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    String ask_permission(String cmd) {
        String output = "null";
        if (cmd.contains("storage")) {

            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                output = "Permission already granted";

            } else {

                output = "Permission asked";
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }
        } else if (cmd.contains("location")) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                output = "Permission already granted";

            } else {

                output = "Permission asked";
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                output = "Permission already granted";

            } else {

                output = "Permission asked";
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }

        }

        return output;
    }

}

