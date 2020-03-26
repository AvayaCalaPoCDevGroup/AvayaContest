package com.example.avayacontest.Clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
}
