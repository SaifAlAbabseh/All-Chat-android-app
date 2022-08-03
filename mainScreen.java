package com.example.allchat;

import static cz.msebera.android.httpclient.protocol.HTTP.USER_AGENT;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class mainScreen extends AppCompatActivity {
    public static String username,password;
    private String profilePicName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        getSupportActionBar().hide();
        if(chattingScreen.timer!=null){
            chattingScreen.timer.cancel();
        }
        otherMethods.changeStatusBarColor(this);
        loadUserData();
    }
    public void goToProfileEditScreen(View v){
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "http://"+DBInfo.hostName+"/All_Chat/uploadPic.php?username="+username+"&password="+password+"&temp=javaToWeb1090" ) );
        startActivity( browse );
    }

    public class addFriendConn extends AsyncTask<Void,Void,Void>{
        private String msg="";
        private boolean isOk=false;
        private String friendUsername;
        private EditText addFriendFieldRef;
        public addFriendConn(String friendUsername,EditText addFriendFieldRef){
            this.friendUsername=friendUsername;
            this.addFriendFieldRef=addFriendFieldRef;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/addFriend.php?check=fromMobile1090&username="+username+"&password="+password+"&friendUsername="+friendUsername;
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(line.equals("ok")){
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
            Dialog loading=new Dialog(mainScreen.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isOk){
                        Toast.makeText(mainScreen.this,"Successfully added friend",Toast.LENGTH_SHORT).show();
                        addFriendFieldRef.setText("");
                        loading.hide();
                    }
                    else{
                        loading.hide();
                        Toast.makeText(mainScreen.this,""+msg,Toast.LENGTH_SHORT).show();
                    }
                }
            },3000);

        }
    }

    public void showAddFriendScreen(View v){
        Dialog d=new Dialog(this);
        d.setContentView(R.layout.add_friend_screen);
        d.setCanceledOnTouchOutside(false);
        d.show();
        Window window = d.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button exitButton =(Button)d.findViewById(R.id.addFriendExit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                d.hide();
            }
        });


        Button addButton=(Button)d.findViewById(R.id.addFriendButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText friendUsernameField=(EditText) d.findViewById(R.id.addFriendField);
                String friendUsername=friendUsernameField.getText().toString();
                if(!friendUsername.trim().equals("")){
                    addFriendConn addConn=new addFriendConn(friendUsername,friendUsernameField);
                    addConn.execute();
                }
                else{
                    Toast.makeText(mainScreen.this,"Field cannot be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void refresh(View v){
        LinearLayout layout=(LinearLayout) findViewById(R.id.friendsBox);
        layout.removeAllViews();
        loadUserData();
        Dialog loading=new Dialog(mainScreen.this);
        loading.setContentView(R.layout.loadingscreen);
        loading.setCanceledOnTouchOutside(false);
        loading.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
                loading.hide();
            }
        },2000);
    }
    private void loadUserData(){
        conn c=new conn(username,password);
        c.execute();
    }
    public class updateAva extends AsyncTask<Void,Void,Void>{
        private String msg="";
        private boolean isOk=false;
        @Override
        protected Void doInBackground(Void... voids) {
            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/updateAvailability.php?check=fromMobile1090&username="+username+"&password="+password+"&which=1";
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(line.equals("ok")){
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
            Dialog loading=new Dialog(mainScreen.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isOk){
                        finish();
                        startActivity(new Intent(mainScreen.this,MainActivity.class));
                        loading.hide();
                    }
                    else{
                        loading.hide();
                        Toast.makeText(mainScreen.this,""+msg,Toast.LENGTH_SHORT).show();
                    }
                }
            },2000);

        }
    }
    public void logout(View v){
        new updateAva().execute();
    }
    public void mainpopupmenu(View v){
        Dialog d=new Dialog(this);
        d.setContentView(R.layout.main_menu_profile_pic_click_popup);
        TextView nameonmainpopup=(TextView)d.findViewById(R.id.usernameView);
        nameonmainpopup.setText(username);
        ImageView profilePic=(ImageView)d.findViewById(R.id.mainPopUpPic);
        Picasso.get().load("http://"+DBInfo.hostName+"/All_Chat/Extra/styles/images/"+profilePicName+".png").into(profilePic);
        d.show();
        Window window = d.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public class conn extends AsyncTask<Void,Void,Void> {
        String username,password;
        public conn(String username,String password){
            this.username=username;
            this.password=password;
        }
        private String msg="";
        private String msg2="";
        private boolean isOk=false;
        private boolean isOk2=false;
        @Override
        protected Void doInBackground(Void... voids) {
            String link="http://"+DBInfo.hostName+"/All_Chat/Mobile/getProfilePic.php?check=fromMobile1090&username="+username+"&password="+password;
            String link2="http://"+DBInfo.hostName+"/All_Chat/Mobile/getFriends.php?check=fromMobile1090&username="+username+"&password="+password;
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                URL url2 = new URL(link2);
                HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                con2.setRequestMethod("GET");
                con2.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode2 = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=""+in.readLine();
                    if(line.equals("Unknown Error")){
                        isOk=false;
                    }
                    else{
                        isOk=true;
                    }
                    msg=line;
                }
                if(responseCode2 == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                    String line=""+in.readLine();
                    if(line.equals("Unknown Error")){
                        isOk2=false;
                    }
                    else{
                        isOk2=true;
                    }
                    msg2=line;
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        private void showFriends(){
            String fullmsg=msg2.substring(0,msg2.length()-1);
            String[] arr1=fullmsg.split("&");
            for(int i=0;i<arr1.length;i++){
                String[] arr2=arr1[i].split("\\|");
                setFriend(arr2[0],arr2[1],arr2[3]);
            }

        }

        private void setFriend(String username,String picture,String whichSide){



            ImageView pic=new ImageView(mainScreen.this);
            pic.setLayoutParams(new TableRow.LayoutParams(150,150));
            Picasso.get().load("http://"+DBInfo.hostName+"/All_Chat/Extra/styles/images/"+picture+".png").into(pic);

            TextView uname=new TextView(mainScreen.this);
            uname.setText(username);
            uname.setTextColor(Color.WHITE);
            uname.setTextSize(25);
            uname.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT));

            Button chatButton=new Button(mainScreen.this);
            chatButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT));
            chatButton.setPadding(0,0,0,0);
            chatButton.setText("Chat");
            chatButton.setTextSize(15);
            chatButton.setTextColor(Color.BLACK);
            chatButton.setBackgroundColor(Color.RED);

            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chattingScreen.friendPicName=picture;
                    chattingScreen.friendUsername=username;
                    chattingScreen.username=mainScreen.username;
                    chattingScreen.password=password;
                    chattingScreen.whichSide=Integer.parseInt(whichSide);
                    Dialog d=new Dialog(mainScreen.this);
                    d.setContentView(R.layout.loadingscreen);
                    d.setCanceledOnTouchOutside(false);
                    d.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            d.dismiss();
                            startActivity(new Intent(mainScreen.this,chattingScreen.class));
                            d.hide();
                        }
                    },1000);
                }
            });


            TableRow row=new TableRow(mainScreen.this);
            row.setGravity(Gravity.CENTER);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

            row.addView(pic);

            TableRow row2=new TableRow(mainScreen.this);
            row2.setGravity(Gravity.CENTER);
            row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));


            TableRow row3=new TableRow(mainScreen.this);
            row3.setGravity(Gravity.CENTER);
            row3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));


            row2.addView(uname);


            row3.addView(chatButton);


            TextView line=new TextView(mainScreen.this);
            line.setText("");
            line.setBackgroundColor(Color.RED);
            line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,20));

            LinearLayout layout=(LinearLayout) findViewById(R.id.friendsBox);
            layout.addView(row);
            layout.addView(row2);
            layout.addView(row3);
            layout.addView(line);
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(isOk){
                profilePicName=msg;
                ImageView profilePic=(ImageView)findViewById(R.id.profilePicture);
                Picasso.get().load("http://"+DBInfo.hostName+"/All_Chat/Extra/styles/images/"+profilePicName+".png").into(profilePic);
            }
            else{
                finish();
                startActivity(new Intent(mainScreen.this,MainActivity.class));
            }
            if(isOk2 && msg2.equals("No Friends")){

                TextView noFriendsView=new TextView(mainScreen.this);
                noFriendsView.setText("No Friends");
                noFriendsView.setTextColor(Color.WHITE);
                noFriendsView.setGravity(Gravity.CENTER);
                noFriendsView.setTextSize(30);
                noFriendsView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

                LinearLayout layout=(LinearLayout) findViewById(R.id.friendsBox);
                layout.addView(noFriendsView);
            }
            else if(isOk2){
                showFriends();
            }
            else{
                Toast.makeText(mainScreen.this,"Unknown Error",Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(mainScreen.this,MainActivity.class));
            }
        }
    }
}