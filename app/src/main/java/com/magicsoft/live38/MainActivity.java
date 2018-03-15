package com.magicsoft.live38;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.magicsoft.live38.popup.PopupActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    //fehifejifejfiejfe

    }

    public void vidio(View view) {
        startActivity(new Intent(this,VideoViewActivity.class));
    }

    public void popupListener(View view) {
        startActivity(new Intent(this,PopupActivity.class));
    }
}
