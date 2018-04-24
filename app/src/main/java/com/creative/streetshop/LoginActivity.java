package com.creative.streetshop;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private ImageView btn_create_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

        btn_create_account = (ImageView) findViewById(R.id.btn_create_account);
        btn_create_account.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id  = view.getId();

        if(id == R.id.btn_create_account){
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        }

    }
}
