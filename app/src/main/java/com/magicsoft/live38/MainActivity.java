package com.magicsoft.live38;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.magicsoft.live38.popup.PopupActivity;
import com.magicsoft.live38.utils.ContactProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //fehifejifejfiejfe

    }

    public void vidio(View view) {
        startActivity(new Intent(this, VideoViewActivity.class));
    }

    public void popupListener(View view) {
        startActivity(new Intent(this, PopupActivity.class));
    }

    public void mieiListener(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        ContactProvider.getInstance().query(this, new ContactProvider.QueryCallback() {
            @Override
            public void startQuery() {

            }

            @Override
            public void queryResult(List<ContactProvider.ContactModel> contactModels) {
                for (int i = 0; i < contactModels.size(); i++) {
                    Log.e("MMM", "queryResult: " + contactModels.get(i).toString());
                }
            }
        });
    }
}
