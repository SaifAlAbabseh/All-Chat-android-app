package com.example.allchat;

import static cz.msebera.android.httpclient.protocol.HTTP.USER_AGENT;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class reset_password extends AppCompatActivity {

    public static String currentPassword = null,
                        username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();
        otherMethods.changeStatusBarColor(this);
    }

    public class resetPassConn extends AsyncTask<Void,Void,Void> {
        private String currentPass, newPass, username, msg;
        public resetPassConn(String currentPass, String newPass, String username){
            this.currentPass = currentPass;
            this.newPass = newPass;
            this.username = username;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            String link=DBInfo.hostName+DBInfo.siteName+"Mobile/resetPassword.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(currentPass)+"&newPassword="+otherMethods.getMd5(newPass);
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=in.readLine();
                    System.out.println("tttttttttttttttttttttttttttttttttttttttt" + line);
                    msg=line != null ? line : "Unknown error";
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
                msg = "Connection error";
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            final Dialog loading=new Dialog(reset_password.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(reset_password.this, msg, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    if(msg.contains("Password updated successfully")) {
                        Toast.makeText(reset_password.this, "Please login back again", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(reset_password.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            },2000);
        }
    }

    public void goBack(View v) {
        finish();
    }

    public void changePassword(View v) {
        EditText currentPassField = findViewById(R.id.currentPassword);
        EditText newPassField = findViewById(R.id.newPassword);
        EditText confirmNewPassField = findViewById(R.id.confirmNewPassword);

        String currentEnteredPass = currentPassField.getText().toString().trim();
        String newEnteredPass = newPassField.getText().toString().trim();
        String confirmNewEnteredPass = confirmNewPassField.getText().toString().trim();

        if(currentEnteredPass.isEmpty() || newEnteredPass.isEmpty() || confirmNewEnteredPass.isEmpty()) {
            Toast.makeText(reset_password.this, "Make sure all fields are not empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if(currentEnteredPass.equals(currentPassword)) {
                if(newEnteredPass.equals(confirmNewEnteredPass)) {
                    if(checkNewPassword(newEnteredPass)) {
                        new resetPassConn(currentPassword, newEnteredPass, username).execute();
                    }
                    else {
                        Toast.makeText(reset_password.this, "Check password requirements", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(reset_password.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(reset_password.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkNewPassword(String password) {
        if(password.contains(" ")) {
            return false;
        }
        if(password.length() < 8 || password.length() > 16) {
            return false;
        }
        return true;
    }
}