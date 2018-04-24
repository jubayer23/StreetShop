package com.creative.streetshop.helperClass;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.streetshop.SplashActivity;
import com.creative.streetshop.appdata.GlobalAppAccess;
import com.creative.streetshop.appdata.MydApplication;
import com.creative.streetshop.model.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jubayer on 4/24/2018.
 */

public class SessionManager {

    private Context context;
    LocationResult locationResult;


    public void getSession(Context context, LocationResult result){
        this.context = context;
        locationResult = result;

        sendRequestForGetTimes(GlobalAppAccess.URL_GET_SESSION);

    }

    public void sendRequestForGetTimes(String url) {

        //url = url + "?" + "email=" + email + "&password=" + password;
        // TODO Auto-generated method stub
        // showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);



                        Session session = MydApplication.gson.fromJson(response, Session.class);

                        if (session.getSuccess() == 1) {
                            locationResult.gotSession(session.getData().getSession());

                        }else{
                            locationResult.gotSession(GlobalAppAccess.ERROR_TYPE_SERVER_PROBLEM);
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                locationResult.gotSession(GlobalAppAccess.ERROR_TYPE_NETWORK_PROBLEM);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("accept", "application/json");
                params.put("X-Oc-Merchant-Id", GlobalAppAccess.SECRET_TOKEN);
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req);
    }


    public static abstract class LocationResult {
        public abstract void gotSession(String session);
    }
}