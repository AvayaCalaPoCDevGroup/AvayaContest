package com.example.avayacontest.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.avayacontest.Clases.Constants;
import com.example.avayacontest.R;

import java.util.LinkedList;
import java.util.List;

public class ActivitySplash extends AppCompatActivity {

    final Handler handler = new Handler();
    private SharedPreferences sharedPreferencesAvaya;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private static int sRequestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferencesAvaya = getSharedPreferences(Constants.AVAYA_SHARED,0);
        checkPermissions(this);
    }

    public void launchNextActivity(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferencesAvaya.getString(Constants.SHARED_SALA_JSON,"").equals("")){
                    Intent i = new Intent(getApplicationContext(), ActivitySeleccionEvento.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 2000);
    }

    /**
     * Checa los permisos necesarios por la app, y de cumplir con todos se procede a la sig activity, de lo contrario salimos de la aplicacion
     * @param context
     */
    public void checkPermissions(Context context) {
        List<String> requestPermissions = new LinkedList<>();
        for (String permission :
                REQUIRED_PERMISSIONS) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permission);
            }
        }

        if (!requestPermissions.isEmpty()) {
            //Aun no se aceptan todos los permisos
            //assert
            final String[] permissions = requestPermissions.toArray(new String[0]);
            assert permissions != null;
            if(sRequestCode == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AvayaSimpleAlertDialog);
                builder
                        .setTitle(R.string.request_permissions_title)
                        .setMessage(getString(R.string.request_permissions_message))
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton(R.string.ok, (dialog, which) -> requestPermissions(permissions, sRequestCode++))
                        .create().show();
            }
            else {
                finish();
            }
        }
        else {
            //Aceptados todos los permisos
            launchNextActivity();
        }

    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        sRequestCode--;
        boolean proceed = true;
        for (int result :
                grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                proceed = false;
                break;
            }
        }
        if (proceed && permissions.length > 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(sharedPreferencesAvaya.getString(Constants.SHARED_SALA_JSON,"").equals("")){
                        Intent i = new Intent(getApplicationContext(), ActivitySeleccionEvento.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, 2000);
        } else {
            finish();
        }
    }
}
