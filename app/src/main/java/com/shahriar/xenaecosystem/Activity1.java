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
import android.content.res.ColorStateList;
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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;


import java.io.File;


import java.util.ArrayList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Activity1 extends AppCompatActivity {


    public List<String> files_to_sync = new ArrayList<>();
    String current_location = Environment.getExternalStorageDirectory().toString();
    String local_location = "";
    List<folder> all_folders;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);

        upload_sync thread = new upload_sync();
        thread.start();

        main(current_location);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void main(String location){
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
    ArrayList<folder> create_folders(List<String> files, final LinearLayout main_layout){


        main_layout.removeAllViews();

        final ArrayList<folder> all_folders = new ArrayList<folder>();
        if (files.size() == 0){

           TextView no_file_error = new TextView(this);
           no_file_error.setText("No files in this folder");
           no_file_error.setTextColor(Color.parseColor("#dddddd"));
           main_layout.addView(no_file_error);

        }
        else{
        for (String filename: files) {

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
        return  all_folders;
    }


    void back_button(){
        List<String> local_loc_list =  new LinkedList<String>(Arrays.asList(local_location.split("/")));
        local_loc_list.remove(local_loc_list.get(local_loc_list.size() - 1));
        String temp_loc = "";
        for (String loc: local_loc_list){
            if (temp_loc == ""){
                temp_loc = loc;
            }
            else{
                temp_loc = temp_loc + "/" +loc;
            }
        }
        local_location = temp_loc;
        main(current_location + "/" + local_location);

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

    @Override
    public void onBackPressed() {
        back_button();
    }
}

