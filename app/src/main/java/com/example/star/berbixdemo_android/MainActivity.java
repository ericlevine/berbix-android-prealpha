package com.example.star.berbixdemo_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.berbix.sdk.BerbixSDK;
import com.berbix.sdk.BerbixSDKAdapter;
import com.berbix.sdk.BerbixSDKOptions;
import com.berbix.sdk.BerbixSDKOptionsBuilder;

public class MainActivity extends AppCompatActivity implements BerbixSDKAdapter {

    private Intent authIntent = null;
    private BerbixSDK berbixSDK = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                berbixSDK.startFlow(MainActivity.this, MainActivity.this);
            }
        });

        findViewById(R.id.cameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authIntent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(authIntent);
            }
        });

        BerbixSDKOptions options = new BerbixSDKOptionsBuilder()
                .setRoleKey("K0JsN3jJaA92hakCrKMbXz1t1NUns8-A")
                .setBaseURL("https://eric.dev.berbix.com:8443/v0/")
                .build();
        berbixSDK = new BerbixSDK("OqrzpLpafz17ETzVOlR367m5l0rC7m9c", options);
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

    public void onComplete() {
        Toast.makeText(this, "flow completed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReady() { }

    @Override
    public void onError(Throwable t) { }
}
