package com.creative.streetshop;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.creative.streetshop.Utility.DeviceInfoUtils;
import com.creative.streetshop.alertbanner.AlertDialogForAnything;
import com.creative.streetshop.appdata.GlobalAppAccess;
import com.creative.streetshop.appdata.MydApplication;
import com.creative.streetshop.model.LoginRegistration;
import com.creative.streetshop.model.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.menu_main, paramMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {

        switch (paramMenuItem.getItemId()) {

            case R.id.action_logout:
                if (DeviceInfoUtils.isConnectingToInternet(this)) {

                } else {
                    AlertDialogForAnything.showAlertDialogWhenComplte(this, "Alert", "Need internet connection in order to logout", false);
                }
                break;

        }

        return false;
    }


    public void sendRequestTpLogout(String url) {

        // TODO Auto-generated method stub
        if (!isProgressDialogShowing())
            showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            if (success == 1) {

                                MydApplication.getInstance().getPrefManger().setUserData("");
                                MydApplication.getInstance().getPrefManger().setSession("");

                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                Log.d("DEBUG", "its in error");

            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("accept", "application/json");
                params.put("X-Oc-Merchant-Id", GlobalAppAccess.SECRET_TOKEN);
                params.put("X-Oc-Session", MydApplication.getInstance().getPrefManger().getSession());
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return super.parseNetworkResponse(response);
                //return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req);
    }
}
