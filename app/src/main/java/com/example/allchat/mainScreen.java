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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class mainScreen extends AppCompatActivity {
    public static String username,password;
    private String profilePicName;
    private boolean isFreshStart = true;
    public static Dialog popUpMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        if(chattingScreen.timer!=null){
            chattingScreen.timer.cancel();
        }
        otherMethods.changeStatusBarColor(this);
        loadUserData();
        isFreshStart = true;
    }

    public void goToProfileEditScreen(View v){
        popUpMenu.dismiss();
        Intent intent = new Intent(mainScreen.this, change_picture.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
            String link=DBInfo.hostName+DBInfo.siteName+"Mobile/addFriend.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&friendUsername="+friendUsername;
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=in.readLine();
                    if(line != null && line.equals("ok")){
                        isOk=true;
                    }
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
            final Dialog loading=new Dialog(mainScreen.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainScreen.this, msg,Toast.LENGTH_SHORT).show();
                    if (addFriendFieldRef != null) addFriendFieldRef.setText("");
                    loading.dismiss();
                }
            },2000);
        }
    }

    public class deleteFriendConn extends AsyncTask<Void,Void,Void>{
        private String msg="";
        private String friendUsername;
        private String deletedFriendTag;
        public deleteFriendConn(String friendUsername,String deletedFriendTag){
            this.friendUsername=friendUsername;
            this.deletedFriendTag=deletedFriendTag;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            String link=DBInfo.hostName+DBInfo.siteName+"Mobile/deleteFriend.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&friendUsername="+friendUsername;
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    msg=in.readLine();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            final Dialog loading=new Dialog(mainScreen.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainScreen.this, msg,Toast.LENGTH_SHORT).show();
                    LinearLayout friendsBox = findViewById(R.id.friendsBox);
                    View friendRow = friendsBox.findViewWithTag(deletedFriendTag);
                    if(friendRow != null) {
                        friendsBox.removeView(friendRow);
                    }
                    loading.dismiss();
                }
            },2000);
        }
    }

    public void showAddFriendScreen(View v){
        final Dialog d=new Dialog(this);
        d.setContentView(R.layout.add_friend_screen);
        d.setCanceledOnTouchOutside(false);
        d.show();
        Window window = d.getWindow();
        if (window != null) window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button exitButton = d.findViewById(R.id.addFriendExit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        Button addButton = d.findViewById(R.id.addFriendButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText friendUsernameField = d.findViewById(R.id.addFriendField);
                String friendUsername = friendUsernameField.getText().toString();
                if(!friendUsername.trim().equals("")){
                    new addFriendConn(friendUsername,friendUsernameField).execute();
                } else {
                    Toast.makeText(mainScreen.this,"Field cannot be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refresh(View v){
        loadUserData();
        final Dialog loading=new Dialog(mainScreen.this);
        loading.setContentView(R.layout.loadingscreen);
        loading.setCanceledOnTouchOutside(false);
        loading.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
            }
        },1000);
    }

    private void loadUserData() {
        TextView usernameView = findViewById(R.id.usernameField);
        usernameView.setText(username);
        new conn(username,password).execute();
    }

    public class updateAva extends AsyncTask<Void,Void,Void>{
        private String msg="";
        private boolean isOk=false;
        @Override
        protected Void doInBackground(Void... voids) {
            String link=DBInfo.hostName+DBInfo.siteName+"Mobile/updateAvailability.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password)+"&which=1";
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=in.readLine();
                    if(line != null && line.equals("ok")){
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
            final Dialog loading=new Dialog(mainScreen.this);
            loading.setContentView(R.layout.loadingscreen);
            loading.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loading.dismiss();
                    if(isOk){
                        finish();
                        Intent intent = new Intent(mainScreen.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mainScreen.this,""+msg,Toast.LENGTH_SHORT).show();
                    }
                }
            },1500);
        }
    }

    public void logout(View v){
        popUpMenu.dismiss();
        new updateAva().execute();
    }

    public void mainpopupmenu(View v){
        final Dialog d=new Dialog(this);
        d.setContentView(R.layout.main_menu_profile_pic_click_popup);
        TextView nameonmainpopup = d.findViewById(R.id.usernameView);
        nameonmainpopup.setText(username);
        ImageView profilePic = d.findViewById(R.id.mainPopUpPic);
        Picasso.get()
                .load(DBInfo.hostName+DBInfo.siteName+"Extra/styles/images/users_images/"+profilePicName+".png")
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(profilePic);
        d.show();
        Window window = d.getWindow();
        if (window != null) window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popUpMenu = d;
    }

    public void goToResetPasswordScreen(View v) {
        popUpMenu.dismiss();
        reset_password.currentPassword = password;
        reset_password.username = username;
        Intent intent = new Intent(mainScreen.this, reset_password.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public class conn extends AsyncTask<Void,Void,Void> {
        String username,password;

        private final LinearLayout layout = findViewById(R.id.friendsBox);
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
            String link=DBInfo.hostName+DBInfo.siteName+"Mobile/getProfilePic.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password);
            String link2=DBInfo.hostName+DBInfo.siteName+"Mobile/getFriends.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password);
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
                int responseCode2 = con2.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=in.readLine();
                    if(line != null && !line.equals("Unknown Error")){
                        isOk=true;
                        msg=line;
                    }
                }
                if(responseCode2 == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                    String line=in.readLine();
                    if(line != null && !line.equals("Unknown Error")){
                        isOk2=true;
                        msg2=line;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        private void showFriends(){
            if (msg2 == null || msg2.isEmpty()) return;
            String fullmsg = msg2.endsWith("&") ? msg2.substring(0, msg2.length()-1) : msg2;
            String[] arr1 = fullmsg.split("&");
            for(int i=0;i<arr1.length;i++){
                String[] arr2 = arr1[i].split("\\|");
                if (arr2.length >= 4) {
                    setFriend(layout, arr2[0],arr2[1],arr2[3], i+1);
                }
            }
        }

        private void setFriend(LinearLayout layout, final String friendUsername, final String picture, final String whichSide, final int id){
            ImageView pic=new ImageView(mainScreen.this);
            pic.setLayoutParams(new TableRow.LayoutParams(150,150));
            Picasso.get()
                    .load(DBInfo.hostName+DBInfo.siteName+"Extra/styles/images/users_images/"+picture+".png")
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(pic);

            TextView uname=new TextView(mainScreen.this);
            uname.setText(friendUsername);
            uname.setTextColor(Color.WHITE);
            uname.setTextSize(25);
            uname.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT));

            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 0, 20, 0);

            MaterialButton chatButton=new MaterialButton(mainScreen.this);
            chatButton.setLayoutParams(params);
            chatButton.setPadding(0,0,0,0);
            chatButton.setText("Chat");
            chatButton.setTextColor(Color.BLACK);
            chatButton.setBackgroundColor(Color.GREEN);
            chatButton.setCornerRadius(100);

            MaterialButton deleteButton=new MaterialButton(mainScreen.this);
            deleteButton.setLayoutParams(params);
            deleteButton.setPadding(0,0,0,0);
            deleteButton.setText("Delete");
            deleteButton.setTextColor(Color.BLACK);
            deleteButton.setBackgroundColor(Color.RED);
            deleteButton.setCornerRadius(100);

            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chattingScreen.friendPicName=picture;
                    chattingScreen.friendUsername=friendUsername;
                    chattingScreen.username=mainScreen.username;
                    chattingScreen.password=password;
                    try {
                        chattingScreen.whichSide=Integer.parseInt(whichSide);
                    } catch (Exception e) { chattingScreen.whichSide = 1; }

                    final Dialog d=new Dialog(mainScreen.this);
                    d.setContentView(R.layout.loadingscreen);
                    d.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            d.dismiss();
                            Intent intent = new Intent(mainScreen.this, chattingScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    },1000);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new deleteFriendConn(friendUsername, "friend_"+id).execute();
                }
            });

            TableRow row=new TableRow(mainScreen.this);
            row.setGravity(Gravity.CENTER);
            row.addView(pic);

            TableRow row2=new TableRow(mainScreen.this);
            row2.setGravity(Gravity.CENTER);
            row2.addView(uname);

            TableRow row3=new TableRow(mainScreen.this);
            row3.setGravity(Gravity.CENTER);
            row3.addView(chatButton);
            row3.addView(deleteButton);

            TableLayout tableLayout = new TableLayout(mainScreen.this);
            tableLayout.addView(row);
            tableLayout.addView(row2);
            tableLayout.addView(row3);

            TextView line=new TextView(mainScreen.this);
            line.setBackgroundColor(Color.RED);
            line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,5));
            tableLayout.addView(line);

            tableLayout.setTag("friend_"+id);
            layout.addView(tableLayout);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            layout.removeAllViews();
            if(isOk){
                System.out.println("tttttttttttttttttttttttttttttttttttttttttttttttttttttt");
                profilePicName=msg;
                ImageView profilePic = findViewById(R.id.profilePicture);
                Picasso.get()
                        .load(DBInfo.hostName+DBInfo.siteName+"Extra/styles/images/users_images/"+profilePicName+".png")
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(profilePic);
            } else {
                finish();
                Intent intent = new Intent(mainScreen.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            if(isOk2 && "No Friends".equals(msg2)){
                TextView noFriendsView=new TextView(mainScreen.this);
                noFriendsView.setText("No Friends");
                noFriendsView.setTextColor(Color.WHITE);
                noFriendsView.setGravity(Gravity.CENTER);
                noFriendsView.setTextSize(30);
                ((LinearLayout)findViewById(R.id.friendsBox)).addView(noFriendsView);
            } else if(isOk2){
                showFriends();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isFreshStart) {
            loadUserData();
        }
        isFreshStart = false;
    }
}