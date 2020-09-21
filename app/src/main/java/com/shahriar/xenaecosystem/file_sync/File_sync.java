package com.shahriar.xenaecosystem.file_sync;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;


import com.shahriar.xenaecosystem.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class File_sync extends AppCompatActivity {

    public static String info = "no";

    public List<String> files_to_sync = new ArrayList<>();
    String current_location = Environment.getExternalStorageDirectory().toString();
    String local_location = "";
    List<folder> all_folders;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_file_sync);

        ask_perm();
        String password = generate_password(6);
        set_password_to_view(password);

        regenarate_button();

        connect_to_server();

        set_help_view();

        Thread thread = new Thread(connect_check);
        thread.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void main(String location) {
        LinearLayout main_layout = getmainlayout();
        List<String> all_files = ls(new File(location));
        all_folders = create_folders(all_files, main_layout);


    }


    List<String> ls(File directory) {

        final File[] files = directory.listFiles();
        List<String> files2 = new ArrayList<String>();
        ;
        if (files != null) {
            for (File file : files) {
                if (file != null) {

                    files2.add(file.getName());
                }
            }
            java.util.Collections.sort(files2);
            return files2;
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    ArrayList<folder> create_folders(List<String> files, final LinearLayout main_layout) {


        main_layout.removeAllViews();

        final ArrayList<folder> all_folders = new ArrayList<folder>();
        if (files.size() == 0) {

            TextView no_file_error = new TextView(this);
            no_file_error.setText("No files in this folder");
            no_file_error.setTextColor(Color.parseColor("#dddddd"));
            main_layout.addView(no_file_error);

        } else {
            for (String filename : files) {

                final String finalFilename = filename;
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

                layout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (local_location != "") {
                            local_location = local_location + "/" + finalFilename;
                        } else {
                            local_location = finalFilename;
                        }
                        main(current_location + "/" + local_location);
                    }
                });
                ConstraintLayout.LayoutParams layout2_params = new ConstraintLayout.LayoutParams(
                        400,
                        120
                );
                layout2.setLayoutParams(layout2_params);
                layout1.addView(layout2);

                // setting upload_check
                final CheckBox upload_check = new CheckBox(this);
                upload_check.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                if (local_location == "") {
                    if (files_to_sync.contains(finalFilename)) {
                        upload_check.setChecked(true);
                    }
                } else {
                    if (files_to_sync.contains(local_location + "/" + finalFilename)) {
                        upload_check.setChecked(true);
                    }
                }
                upload_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b == true) {
                            if (local_location == "") {
                                files_to_sync.add(finalFilename);
                            } else {
                                files_to_sync.add(local_location + "/" + finalFilename);
                            }
                            for (String one_file : files_to_sync) {
                                Log.d("file to sync", one_file);
                            }
                        } else {
                            if (local_location == "") {
                                files_to_sync.remove(finalFilename);
                            } else {
                                files_to_sync.remove(local_location + "/" + finalFilename);
                            }
                        }
                    }
                });
                upload_check.setId(View.generateViewId());
                layout1.addView(upload_check);

                // setting sync text
                TextView sync = new TextView(this);
                sync.setText("Sync");
                sync.setId(View.generateViewId());
                sync.setTextColor(Color.rgb(200, 200, 200));
                layout1.addView(sync);

                ImageView icon = new ImageView(this);
                icon.setImageDrawable(getResources().getDrawable(R.drawable.folder));
                icon.setId(View.generateViewId());

                ConstraintLayout.LayoutParams icon_params = new ConstraintLayout.LayoutParams(
                        80,
                        80
                );

                icon.setLayoutParams(icon_params);

                /*icon.setImageDrawable(getDrawable(R.drawable.file));*/
                layout1.addView(icon);


                // setting layout 1 constrains
                ConstraintSet layout1set = new ConstraintSet();
                layout1set.clone(layout1);

                // left part

                layout1set.connect(layout2.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 32);
                layout1set.connect(upload_check.getId(), ConstraintSet.RIGHT, layout1.getId(), ConstraintSet.RIGHT, 32);

                layout1set.connect(sync.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 15);
                layout1set.connect(sync.getId(), ConstraintSet.RIGHT, upload_check.getId(), ConstraintSet.LEFT, 16);


                layout1set.connect(icon.getId(), ConstraintSet.LEFT, layout1.getId(), ConstraintSet.LEFT, 16);


                layout1set.connect(icon.getId(), ConstraintSet.TOP, layout2.getId(), ConstraintSet.TOP, 0);
                layout1set.connect(icon.getId(), ConstraintSet.BOTTOM, layout2.getId(), ConstraintSet.BOTTOM, 0);

                layout1set.connect(layout2.getId(), ConstraintSet.LEFT, icon.getId(), ConstraintSet.RIGHT, 32);


                layout1set.applyTo(layout1);


                // setting file title
                TextView file_title = new TextView(this);
                file_title.setText(filename);
                file_title.setTextColor(Color.parseColor("#ffffff"));
                file_title.setId(View.generateViewId());
                layout2.addView(file_title);


                ConstraintSet cs = new ConstraintSet();
                cs.clone(layout2);

                cs.connect(file_title.getId(), ConstraintSet.TOP, layout2.getId(), ConstraintSet.TOP, 8);
                cs.connect(file_title.getId(), ConstraintSet.BOTTOM, layout2.getId(), ConstraintSet.BOTTOM, 8);
                cs.connect(file_title.getId(), ConstraintSet.LEFT, layout2.getId(), ConstraintSet.LEFT, 32);
                cs.applyTo(layout2);


                main_layout.addView(layout1);
                filename = local_location + "/" + filename;
                all_folders.add(new folder(filename, upload_check));
            }
        }
        return all_folders;
    }


    void back_button() {
        if (local_location == ""){
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to exit?")
                    .setMessage("Sync will be stopped. To keep sync running press home and dont clear this app from the recents menu")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Exit!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           finish();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            List<String> local_loc_list = new LinkedList<String>(Arrays.asList(local_location.split("/")));
            local_loc_list.remove(local_loc_list.get(local_loc_list.size() - 1));
            String temp_loc = "";
            for (String loc : local_loc_list) {
                if (temp_loc == "") {
                    temp_loc = loc;
                } else {
                    temp_loc = temp_loc + "/" + loc;
                }
            }
            local_location = temp_loc;
            main(current_location + "/" + local_location);
        }

    }

    void set_help_view(){
        Button help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help();
            }
        });
    }

    void help(){
        alert_error("Help");

    }
    LinearLayout getmainlayout() {
        LinearLayout main_layout = findViewById(R.id.main_layout);
        return main_layout;
    }

    class folder {
        String filename;
        CheckBox upload_check;

        folder(String filename, CheckBox upload_check) {
            this.filename = filename;
            this.upload_check = upload_check;
        }
    }


    Runnable connect_check = new Runnable(){
        public void run() {
            while (true) {
                if (info == "ok") {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            main(current_location);
                            info = "done";
                        }
                    });
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    void connect_to_server(){
        Button connect_to_server_view = findViewById(R.id.connect_to_server_view);
        connect_to_server_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = getip();
                if (!(ip.matches(""))){
                    String password = getpassword();
                    Thread r = new File_sync.upload_files(ip, password);
                    new Thread(r).start();

                }
            }
        });
    }

    String getpassword(){
        TextView password_view = findViewById(R.id.password_view);
        return password_view.getText().toString();
    }

    String getip(){
        EditText server_ip_view = findViewById(R.id.server_ip_view);

        String ip = server_ip_view.getText().toString();

        if (ip.matches("")){
            alert_error("IP address is empty");

        }

        return ip;
    }

    public void alert_error(String error1){
        new AlertDialog.Builder(this)
                .setTitle(error1)
                .setMessage("Steps:\n\n1) Please download and open the Computer app\n\n2)Type the password shown on the mobile in your Computer app and click on 'Start Server'\n\n3)Write the ip address of your computer and click on 'Connect to Server'.\n\nStill not working?\nIf you entered an ip from computer please click on 'Not Working?' on Computer app and try another ip (typically that starts with '192.168') and is your lan ip (of your router)\nAnd make sure your computer and mobile is on the same network (same wifi)")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Video Steps!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=Hxy8BZGQ5Jo")));

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    String generate_password(int n){

        // chose a Character random from this String
        String AlphaNumericString =
                "0123456789"
                        + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();

    }
    void regenarate_button(){
        Button regenerate_view = findViewById(R.id.regenerate);
        regenerate_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = generate_password(6);
                set_password_to_view(password);
            }
        });
    }
    void set_password_to_view(String password){
        TextView password_view = findViewById(R.id.password_view);
        password_view.setText(password);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void ask_perm(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {


                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                        new AlertDialog.Builder(this)
                                .setTitle("Storage Permission Denied")
                                .setMessage("Please allow Storage Permission to see the files and upload them to your computer.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    public void onClick(DialogInterface dialog, int which) {
                                        ask_perm();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        back_button();
    }


    public class upload_files extends Thread {

        String ip;
        String password;

        public upload_files(String temp_ip, String temp_password) {
            ip = temp_ip;
            password = temp_password;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {

            Socket server = conect_to_server();


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
            String alert = "null";
            try {

                sock = new Socket();
                sock.connect(new InetSocketAddress(ip, 65022), 2000);
                send(sock, password);
                String pass_from_server = recv(sock);

                if (pass_from_server.equals(password)) {
                    info = "ok";
                    return sock;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alert_error("Wrong password!");
                        }
                    });
                    return null;
                }
            } catch ( SocketTimeoutException e){
                e.printStackTrace();
                alert = "No device with this ip found. Please check if you are connected to your wifi";
            }
            catch (UnknownHostException e){
                e.printStackTrace();
                alert = "Please enter a valid ip written in the desktop app, typically starting with 192.168";
            }
            catch (Exception e) {
                e.printStackTrace();
                alert = "Unknown error. Please try again and if problem persists check if you are connected to wifi and if this app has network permission.";

            }

            final String finalAlert = alert;
            runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alert_error(finalAlert);
            }
        });
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


}

