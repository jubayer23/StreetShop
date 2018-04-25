package com.creative.streetshop;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.creative.streetshop.Utility.UserLastKnownLocation;
import com.creative.streetshop.appdata.GlobalAppAccess;
import com.creative.streetshop.appdata.MydApplication;
import com.creative.streetshop.helperClass.SessionManager;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(MydApplication.getInstance().getPrefManger().getUserData() != null){

            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    },
                    4000
            );

        }

        //showProgressDialog("please wait..", true, false);
        SessionManager.LocationResult locationResult = new SessionManager.LocationResult() {

            @Override
            public void gotSession(String session) {

                if(!session.equals(GlobalAppAccess.ERROR_TYPE_NETWORK_PROBLEM ) &&
                        !session.equals(GlobalAppAccess.ERROR_TYPE_SERVER_PROBLEM)){
                    MydApplication.getInstance().getPrefManger().setSession(session);
                }


                new Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        },
                        2000
                );

            }
        };
        SessionManager myLocation = new SessionManager();
        myLocation.getSession(this, locationResult);
    }



}
