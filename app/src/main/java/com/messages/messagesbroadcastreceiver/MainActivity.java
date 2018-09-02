package com.messages.messagesbroadcastreceiver;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView mNumberOfMessages;
    TextView mNumberOfNotValidMessages;
    SharedPreferences preferences;

    final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mNumberOfMessages = findViewById(R.id.number_of_messages);
        mNumberOfMessages.setText(String.valueOf(preferences.getInt("messagesCount", 0)));

        mNumberOfNotValidMessages = findViewById(R.id.number_of_not_valid_messages);
        mNumberOfNotValidMessages.setText(String.valueOf(preferences.getInt("messagesNotValidCount", 0)));

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
            }
        }
    }

}
