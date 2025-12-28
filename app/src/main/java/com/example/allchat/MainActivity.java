package com.example.allchat;

import static cz.msebera.android.httpclient.protocol.HTTP.USER_AGENT;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        otherMethods.changeStatusBarColor(this);
    }

    public void goToSignUpScreen(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.25/"+DBInfo.siteName));
        startActivity(browserIntent);
    }
    public class conn extends AsyncTask<Void,Void,Void> {
        String username,password;
        public conn(String username,String password){
            this.username=username;
            this.password=password;
        }
        private String msg="";
        private boolean isOk=false;
        @Override
        protected Void doInBackground(Void... voids) {
            String link="http://"+DBInfo.hostName+"/"+DBInfo.siteName+"/Mobile/checkInfoForLogin.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password);
            String link2="http://"+DBInfo.hostName+"/"+DBInfo.siteName+"/Mobile/updateAvailability.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&which=2";
            try{
                URL url = new URL(link);
                URL url2=new URL(link2);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con2.setRequestMethod("GET");
                con2.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                int responseCode2 = con2.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line = "" + in.readLine();
                    if (line.equals("ok")) {
                        isOk = true;
                        if(responseCode2==HttpURLConnection.HTTP_OK){
                            BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                            String line2=""+in2.readLine();
                            if(!line2.equals("ok")){
                                isOk=false;
                            }
                        }
                    } else {
                        isOk = false;
                    }
                    msg = line;
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Handler h=new Handler();
            Dialog loading=new Dialog(MainActivity.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            h.postDelayed(new Runnable(){
                public void run(){
                    loading.hide();
                    if(isOk){
                        loading.dismiss();
                        finish();
                        mainScreen.username=username;
                        mainScreen.password=password;
                        startActivity(new Intent(MainActivity.this,mainScreen.class));
                    }
                    else{
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                }},2000);
        }
    }
    public void login(View v){
        EditText usernameField=(EditText) findViewById(R.id.username_field);
        EditText passwordField=(EditText) findViewById(R.id.password_field);

        String username=usernameField.getText().toString();
        String password=passwordField.getText().toString();


        if(username.trim().equals("") || password.trim().equals("")){
            Toast.makeText(MainActivity.this,"Fields cannot be empty",Toast.LENGTH_SHORT).show();
        }
        else{
            conn c=new conn(username,password);
            c.execute();
        }

    }
}