package com.shahriar.xenaecosystem.connect;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shahriar.xenaecosystem.R;
import com.shahriar.xenaecosystem.file_sync.upload_files;

public class Connect_To_Server extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_connect_to_server);

        String password = generate_password(6);
        set_password_to_view(password);

        regenarate_button();

        connect_to_server();



    }


    void connect_to_server(){
        Button connect_to_server_view = findViewById(R.id.connect_to_server_view);
        connect_to_server_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = getip();
                if (!(ip.matches(""))){
                    String password = getpassword();
                       Runnable r = new upload_files(ip, password);
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

    public void alert_error(String error){
        new AlertDialog.Builder(this)
                .setTitle(error)
                .setMessage("Please start the server on your computer and enter that ip address")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
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




}