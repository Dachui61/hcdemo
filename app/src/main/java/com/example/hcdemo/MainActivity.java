package com.example.hcdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity  {


    private DevManager devManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button initcamera = findViewById(R.id.button1);
        Button stopcamera = findViewById(R.id.button2);
        Button recordcamera = findViewById(R.id.button3);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        devManager = new DevManager();

        initcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devManager.addDev(view.getContext());
                int luserid =  devManager.getlUserID();
//                Toast.makeText(view.getContext(), "userid:"+luserid, Toast.LENGTH_SHORT).show();
                devManager.realPlay(surfaceView);

                devManager.snap(view.getContext());

            }
        });

        stopcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devManager.stop(view.getContext());
            }
        });

        recordcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devManager.record(view.getContext());
            }
        });



    }
}