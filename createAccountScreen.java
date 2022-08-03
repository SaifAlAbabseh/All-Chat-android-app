package com.example.allchat;

import static cz.msebera.android.httpclient.protocol.HTTP.USER_AGENT;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class createAccountScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_screen);
        getSupportActionBar().hide();
        otherMethods.changeStatusBarColor(this);
        setListenerToConfirmPassField();
    }
    public void showUsernameReq(View v){


        Dialog popUp =new Dialog(this);
        popUp.setContentView(R.layout.reqspopupscreen);
        TextView reqType=(TextView)popUp.findViewById(R.id.reqType);
        TextView reqDesc=(TextView)popUp.findViewById(R.id.reqDescription);

        reqType.setText("Username Reqs");
        reqDesc.setText("-Without spaces\n-Without symbols\n-At least 6 characters\n-At most 12 characters");

        popUp.show();
    }
    public void showPasswordReq(View v){


        Dialog popUp =new Dialog(this);
        popUp.setContentView(R.layout.reqspopupscreen);
        TextView reqType=(TextView)popUp.findViewById(R.id.reqType);
        TextView reqDesc=(TextView)popUp.findViewById(R.id.reqDescription);

        reqType.setText("Password Reqs");
        reqDesc.setText("-Without spaces\n-At least 8 characters\n-At most 16 characters");


        popUp.show();
    }
    private void setListenerToConfirmPassField(){
        EditText confirmPass=(EditText)findViewById(R.id.signup_confirmPass_field);
        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String conPass=confirmPass.getText().toString();
                TextView face=(TextView) findViewById(R.id.faceForConPass);
                if(!conPass.trim().equals("")){
                    EditText pass=(EditText)findViewById(R.id.signup_password_field);
                    String passstr=pass.getText().toString();
                    if(conPass.equals(passstr)){
                        face.setText(":)");
                        face.setBackgroundColor(Color.GREEN);
                    }
                    else{
                        face.setText(":(");
                        face.setBackgroundColor(Color.RED);
                    }
                }
                else{
                    face.setText(":(");
                    face.setBackgroundColor(Color.RED);
                }
            }
        });
    }
    public void goBackToLoginScreen(View v) {
        finish();
    }
    public class conn extends AsyncTask<Void,Void,Void> {
        String username="",password="";
        public conn(String username,String password){
            this.username=username;
            this.password=password;
        }
        private String msg="";
        private boolean isOk=false;
        @Override
        protected Void doInBackground(Void... voids) {
            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/createAccount.php?check=fromMobile1090&username="+username+"&password="+password;
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(line.equals("Successfully created account")){
                        isOk=true;
                    }
                    else{
                        isOk=false;
                    }
                    msg=line;
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(isOk){
                EditText usernameField=(EditText) findViewById(R.id.signup_username_field);
                EditText passField=(EditText) findViewById(R.id.signup_password_field);
                EditText conpassField=(EditText) findViewById(R.id.signup_confirmPass_field);
                usernameField.setText("");
                passField.setText("");
                conpassField.setText("");
            }
            Handler h=new Handler();
            Dialog loading=new Dialog(createAccountScreen.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            h.postDelayed(new Runnable(){
                public void run(){
                    loading.hide();
                    Toast.makeText(createAccountScreen.this,msg,Toast.LENGTH_SHORT).show();
                }},2000);
        }
    }
    public void createAccount(View v){
            EditText usernameField=(EditText) findViewById(R.id.signup_username_field);
            EditText passField=(EditText) findViewById(R.id.signup_password_field);
            EditText conpassField=(EditText) findViewById(R.id.signup_confirmPass_field);

            if(usernameField.getText().toString().trim().equals("") || passField.getText().toString().trim().equals("") || conpassField.getText().toString().trim().equals("")){
                Toast.makeText(this,"Fields cannot be empty",Toast.LENGTH_SHORT).show();
            }
            else{
                if(passField.getText().toString().equals(conpassField.getText().toString())){
                    if(otherMethods.checkUsernameIfValid(usernameField.getText().toString())){
                        if(otherMethods.checkPasswordIfValid(passField.getText().toString())){
                            conn c=new conn(usernameField.getText().toString(),passField.getText().toString());
                            c.execute();
                        }
                        else{
                            Toast.makeText(this,"Check password requirements",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(this,"Check username requirements",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this,"Passwords does not match",Toast.LENGTH_SHORT).show();
                }
            }
    }
}