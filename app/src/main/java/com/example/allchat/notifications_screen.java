package com.example.allchat;

import static cz.msebera.android.httpclient.protocol.HTTP.USER_AGENT;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class notifications_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_screen);
        getSupportActionBar().hide();
        otherMethods.changeStatusBarColor(this);
        loadNotifications();
    }

    public void goBack(View v) {
        finish();
    }

    private void loadNotifications() {
        new conn(mainScreen.username, mainScreen.password).execute();
    }

    public class conn extends AsyncTask<Void,Void,Void> {
        String username,password;

        private final LinearLayout layout = findViewById(R.id.notificationsContainer);
        public conn(String username,String password){
            this.username=username;
            this.password=password;
        }
        private String msg="";
        private boolean isOk=false;

        @Override
        protected Void doInBackground(Void... voids) {
            String link=DBInfo.hostName+DBInfo.siteName+"Mobile/getNotifications.php?check=fromMobile1090&username="+username+"&password="+otherMethods.getMd5(password);
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=in.readLine();
                    if(line != null){
                        msg=line;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        private void processRequest(String process, String id, String requesterUsername) {
            String link=DBInfo.hostName+DBInfo.siteName+"Mobile/processRequest.php?check=fromMobile1090&process="+process+"&username="+username+"&password="+otherMethods.getMd5(password)+"&requestId="+id+"&requesterUsername="+requesterUsername;
            try{
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line=in.readLine();
                    if(line != null){
                        String tempResult;
                        if(line.contains("Success")) {
                            tempResult = "Done";
                        } else {
                            tempResult = "Error";
                        }
                        runOnUiThread(() -> {
                            if(!tempResult.equals("Error")) layout.removeView(layout.findViewWithTag(id));
                            Toast.makeText(notifications_screen.this, tempResult, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        private void setNotification(LinearLayout layout, final String username, final String id){

            TextView uname=new TextView(notifications_screen.this);
            uname.setText(username);
            uname.setTextColor(Color.WHITE);
            uname.setTextSize(25);
            uname.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT));

            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 0, 20, 0);

            MaterialButton acceptButton=new MaterialButton(notifications_screen.this);
            acceptButton.setLayoutParams(params);
            acceptButton.setPadding(0,0,0,0);
            acceptButton.setText("Accept");
            acceptButton.setTextColor(Color.WHITE);
            acceptButton.setBackgroundColor(Color.GREEN);
            acceptButton.setCornerRadius(100);

            MaterialButton rejectButton=new MaterialButton(notifications_screen.this);
            rejectButton.setLayoutParams(params);
            rejectButton.setPadding(0,0,0,0);
            rejectButton.setText("Reject");
            rejectButton.setTextColor(Color.WHITE);
            rejectButton.setBackgroundColor(Color.RED);
            rejectButton.setCornerRadius(100);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        processRequest("accept", id, username);
                    });
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        processRequest("reject", id, username);
                    });
                }
            });

            TableRow.LayoutParams spaceParams = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1
            );
            Space space = new Space(notifications_screen.this);
            space.setLayoutParams(spaceParams);

            TableRow row=new TableRow(notifications_screen.this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(uname);
            row.addView(space);
            row.addView(acceptButton);
            row.addView(rejectButton);
            row.setTag(id);

            layout.addView(row);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            layout.removeAllViews();
            if(msg.contains("[") && msg.contains("]")){
                try {
                    // Assuming the format is a JSON Array directly or a JSON Object with a "notifications" array
                    JSONArray notifications;
                    try {
                        JSONObject obj = new JSONObject(msg);
                        notifications = obj.getJSONArray("notifications");
                    } catch (JSONException e) {
                        // Fallback if msg itself is the JSONArray
                        notifications = new JSONArray(msg);
                    }

                    for (int i = 0; i < notifications.length(); i++) {
                        JSONObject item = notifications.getJSONObject(i);
                        String requester = item.getString("requester");
                        setNotification(layout, requester, item.getString("request_id"));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                TextView otherResponse = new TextView(notifications_screen.this);
                otherResponse.setTextColor(Color.WHITE);
                otherResponse.setText(msg);
                layout.addView(otherResponse);
            }
        }
    }
}