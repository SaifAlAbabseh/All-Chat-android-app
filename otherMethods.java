package com.example.allchat;

import android.app.Activity;
import android.os.Build;

public class otherMethods {
    public static boolean checkPasswordIfValid(String password){
        boolean res=true;

        if(password.trim().equals("")){
            res=false;
        }
        else{
            for (int i=0;i<password.length();i++){
                int charCode=(int)(password.charAt(i));
                if(charCode==32){
                    res=false;
                    break;
                }
            }
            if(res){
                if(!(password.length()>=8 && password.length()<=16)){
                    res=false;
                }
            }
        }

        return res;
    }
    public static boolean checkUsernameIfValid(String username){
        boolean res=true;


        if(username.trim().equals("")) {
            res=false;
        }
        else{
            for (int i=0;i<username.length();i++){
                int charCode=(int)(username.charAt(i));
                if(!((charCode>=65 && charCode<=90) || (charCode>=97 && charCode<=122) || (charCode>=48 && charCode<=57))){
                    System.out.println(""+charCode+"    ");
                    res=false;
                    break;
                }
            }
            if(res){
                if(!(username.length()>=6 && username.length()<=12)){
                    res=false;
                }
            }
        }

        return res;
    }
    public static void changeStatusBarColor(Activity a){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            a.getWindow().setStatusBarColor(a.getResources().getColor(R.color.black, a.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setStatusBarColor(a.getResources().getColor(R.color.black));
        }
    }
}
