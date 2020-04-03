package com.example.avayacontest.Clases;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;

import com.example.avayacontest.BuildConfig;
import com.example.avayacontest.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Utils {

    public static Bitmap ConvertToImage(String image){
        try{
            //InputStream stream = new ByteArrayInputStream(image.getBytes());
            InputStream stream = new ByteArrayInputStream(Base64.decode(image.getBytes(), Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void showVersionDialog(Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AvayaSimpleAlertDialog);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.dlog_version_title));
        alertDialogBuilder.setMessage("AVAYA CONTEST - version " + BuildConfig.VERSION_NAME);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
