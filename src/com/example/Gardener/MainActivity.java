package com.example.Gardener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Called when the activity is first created.
 * Used to access the connection and browse activities
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void gotoConnectActivity(View view) {
        Intent i = new Intent(this, ConnectActivity.class);
        startActivity(i);
    }

    public void gotoBrowseActivity(View view) {
        Intent i = new Intent(this, BrowseActivity.class);
        startActivity(i);
    }
}
