package com.example.allchat;

import android.app.Activity;
import android.os.Build;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class otherMethods {
    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

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
