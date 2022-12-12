package com.example.allchat;

import static cz.msebera.android.httpclient.protocol.HTTP.USER_AGENT;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class chattingScreen extends AppCompatActivity {
    public static String friendPicName;
    public static String friendUsername;
    public static String username;
    public static String password;
    public static int whichSide;
    private boolean isAvailable=false;
    static CountDownTimer timer=null;
    private int lastIndex=0;
    @Override
    public void onBackPressed() {
        finish();
        if(timer!=null){
            timer.cancel();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_screen);
        getSupportActionBar().hide();
        otherMethods.changeStatusBarColor(this);
        getMessagesConn con=new getMessagesConn();
        con.execute();
    }
    private void loadData(){
        loadFriendUsername();
        timer=new CountDownTimer(Long.MAX_VALUE,1000) {
            @Override
            public void onTick(long l) {
                refreshFriendData();
                checkLastMessage check=new checkLastMessage();
                check.execute();
            }

            @Override
            public void onFinish() {
                if(timer!=null){
                    timer.start();
                }
            }
        };
        timer.start();
    }
    public class checkLastMessage extends AsyncTask<Void,Void,Void>{
        private boolean isOk=false;
        private String msg;
        @Override
        protected Void doInBackground(Void... voids) {
            String tableName="";
            if(whichSide==1){
                tableName+=username+friendUsername;
            }
            else if(whichSide==2){
                tableName+=friendUsername+username;
            }

            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/checkLastMessage.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&tableName="+tableName+"&lastIndex="+lastIndex;

            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(!(line.equals("no") || line.equals("Unknown Error") || line.equals("No Messages"))){
                        isOk=true;
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
                lastIndex++;

                String fromwho="";
                String message="";
                try {
                    JSONObject obj=new JSONObject(msg);

                    fromwho=obj.getString("fromwho");
                    message=URLDecoder.decode(obj.getString("message"), "UTF-8");



                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                getMessagesConn con=new getMessagesConn();
                con.setMessage(fromwho,message);

                NestedScrollView sV=(NestedScrollView) findViewById(R.id.chatBoxScroll);
                sV.post(new Runnable() {
                    @Override
                    public void run() {
                        sV.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            else{
                if(msg.equals("Unknown Error")){
                    Toast.makeText(chattingScreen.this,msg,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public class getMessagesConn extends AsyncTask<Void,Void,Void>{
        private boolean isOk=false;
        private String msg;
        @Override
        protected Void doInBackground(Void... voids) {
            String tableName="";

            if(whichSide==1){
                tableName+=username+friendUsername;
            }
            else if(whichSide==2){
                tableName+=friendUsername+username;
            }

            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/getMessages.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&tableName="+tableName;

            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(!(line.equals("Unknown Error") || line.equals("No Messages"))){
                        isOk=true;
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

                try {
                    JSONArray arr=new JSONArray(msg);

                    if(arr.length()>0){
                        for (int i=0;i<arr.length();i++){
                            JSONObject obj=arr.getJSONObject(i);
                            String fromwho=obj.getString("fromwho");
                            String message="";
                            try {
                                message=URLDecoder.decode(obj.getString("message"),"UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            setMessage(fromwho, message);
                            lastIndex++;

                            NestedScrollView sV=(NestedScrollView) findViewById(R.id.chatBoxScroll);
                            sV.post(new Runnable() {
                                @Override
                                public void run() {
                                    sV.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        }
                    }
                    else{
                        Toast.makeText(chattingScreen.this,"No  Messages",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }







                loadData();
            }
            else if(msg.equals("No Messages")){
                loadData();
            }
            else{
                Toast.makeText(chattingScreen.this,""+msg,Toast.LENGTH_SHORT).show();
            }
        }
        public void setMessage(String fromWho,String message){
            boolean isItFromU=false;
            if(fromWho.equals(username)){
                isItFromU=true;
            }


            TableRow row1=new TableRow(chattingScreen.this);
            TableRow row2=new TableRow(chattingScreen.this);

            TextView fromWhoField=new TextView(chattingScreen.this);
            TextView messageField=new TextView(chattingScreen.this);


            TextView line=new TextView(chattingScreen.this);
            line.setText("");
            line.setBackgroundColor(Color.RED);
            line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,10));

            row1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

            if(isItFromU){
                row1.setGravity(Gravity.RIGHT);
                row2.setGravity(Gravity.RIGHT);
            }
            else{
                row1.setGravity(Gravity.LEFT);
                row2.setGravity(Gravity.LEFT);
            }


            fromWhoField.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
            messageField.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));

            fromWhoField.setTextSize(25);
            messageField.setTextSize(25);

            fromWhoField.setTextColor(Color.WHITE);
            messageField.setTextColor(Color.WHITE);

            fromWhoField.setText("From: "+fromWho);
            fromWhoField.setTextColor(Color.YELLOW);
            messageField.setText(message);

            row1.addView(fromWhoField);
            row2.addView(messageField);

            LinearLayout layout=(LinearLayout) findViewById(R.id.chattingBox);
            layout.addView(row1);
            layout.addView(row2);
            layout.addView(line);
        }
    }
    public class conn extends AsyncTask<Void,Void,Void> {

        private boolean isOk=false;
        private String msg;
        @Override
        protected Void doInBackground(Void... voids) {
            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/getFriendData.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&friendUsername="+friendUsername;
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(!(line.equals("Unknown Error") || line.equals("Not a friend"))){
                        isOk=true;
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
                String res[]=msg.split("\\|");
                if(res[0].equals("0")){
                    isAvailable=false;
                }
                else{
                    isAvailable=true;
                }
                friendPicName=res[1];
            }
            else{
                Toast.makeText(chattingScreen.this,""+msg,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void refreshFriendData(){
        conn c=new conn();
        c.execute();

        TextView avaField=(TextView)findViewById(R.id.chatAva);
        if(isAvailable){
            avaField.setBackgroundColor(Color.GREEN);
        }
        else{
            avaField.setBackgroundColor(Color.RED);
        }

        ImageView friendPicField=(ImageView)findViewById(R.id.chatFriendPic);

        Picasso.get().load("http://"+DBInfo.hostName+"/All_Chat/Extra/styles/images/"+friendPicName+".png").into(friendPicField);
    }
    private void loadFriendUsername(){
        TextView usernameView=(TextView) findViewById(R.id.friendUsernameChat);
        usernameView.setText(friendUsername);
    }
    public class sendConn extends AsyncTask<Void,Void,Void> {

        private boolean isOk=false;
        private String msg;
        private String message;
        public sendConn(String message){
            this.message=message;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            String tableName="";

            if(whichSide==1){
                tableName+=username+friendUsername;
            }
            else if(whichSide==2){
                tableName+=friendUsername+username;
            }
            String encodedMessage="";
            try {
                encodedMessage= URLEncoder.encode(message,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/sendMessage.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&tableName="+tableName+"&message="+encodedMessage+"&lastIndex="+lastIndex;


            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(!line.equals("Unknown Error")){
                        isOk=true;
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
            if(!isOk){
                Toast.makeText(chattingScreen.this,msg,Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void sendMessage(View v){
        EditText messageField=(EditText) findViewById(R.id.chatField);
        String message=messageField.getText().toString().trim();
        if(!message.equals("")){
            sendConn con=new sendConn(message);
            con.execute();
            messageField.setText("");
        }
        else{
            Toast.makeText(this,"Field cannot be empty",Toast.LENGTH_SHORT).show();
        }
    }
}