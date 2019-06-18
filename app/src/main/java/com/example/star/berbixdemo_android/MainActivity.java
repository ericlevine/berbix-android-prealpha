package com.example.star.berbixdemo_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.berbix.sdk.BerbixEnvironment;
import com.berbix.sdk.BerbixSDK;
import com.berbix.sdk.BerbixSDKAdapter;

public class MainActivity extends AppCompatActivity implements BerbixSDKAdapter {

    private Intent authIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BerbixSDK.getAuthorized(MainActivity.this, MainActivity.this);
            }
        });

        findViewById(R.id.cameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authIntent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(authIntent);
            }
        });

        // Configure
        BerbixSDK.shared.configure("LhcdxRRBrsB9ublAfwdcoVlH0MNnPxFJ",
                "8VXTXAFaG0sjdOgVJk3mugt4zYeyBUCl");
        BerbixSDK.shared.setEnvironment(BerbixEnvironment.STAGING);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void authorized(String code) {
        Toast.makeText(this, code, Toast.LENGTH_LONG).show();
    }
}
