package com.creative.streetshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.creative.streetshop.Utility.CommonMethods;
import com.creative.streetshop.Utility.DeviceInfoUtils;
import com.creative.streetshop.alertbanner.AlertDialogForAnything;
import com.creative.streetshop.appdata.GlobalAppAccess;
import com.creative.streetshop.appdata.MydApplication;
import com.creative.streetshop.helperClass.SessionManager;
import com.creative.streetshop.model.CommonResponse;
import com.creative.streetshop.model.LoginRegistration;
import com.creative.streetshop.model.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText ed_email;
    private ImageView btn_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();
    }

    private void init(){
        ed_email = findViewById(R.id.ed_email);
        btn_forgot_password = findViewById(R.id.btn_forgot_password);
        btn_forgot_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();


        if(id == R.id.btn_forgot_password){
            if (!DeviceInfoUtils.isConnectingToInternet(this)) {
                AlertDialogForAnything.showAlertDialogWhenComplte(this, "Alert!", "No internet connection! Please connect to working internet connection.", false);
            }

            if(showWarning()){
                if (MydApplication.getInstance().getPrefManger().getSession().isEmpty()) {
                    getSessionFirstThenRequestToForgotPassword();
                } else {
                    sendRequestToForgotPassword(GlobalAppAccess.URL_RETRIVE_PASSWORD);
                }
            }
        }
    }

    private void getSessionFirstThenRequestToForgotPassword() {
        showProgressDialog("please wait..", true, false);
        SessionManager.LocationResult locationResult = new SessionManager.LocationResult() {

            @Override
            public void gotSession(String session) {

                if (!session.equals(GlobalAppAccess.ERROR_TYPE_NETWORK_PROBLEM) &&
                        !session.equals(GlobalAppAccess.ERROR_TYPE_SERVER_PROBLEM)) {
                    MydApplication.getInstance().getPrefManger().setSession(session);
                    sendRequestToForgotPassword(GlobalAppAccess.URL_RETRIVE_PASSWORD);
                } else {
                    dismissProgressDialog();
                    AlertDialogForAnything.showAlertDialogWhenComplte(ForgotPasswordActivity.this, "Error!", "Problem in getting the session!", false);
                }

            }
        };
        SessionManager myLocation = new SessionManager();
        myLocation.getSession(this, locationResult);
    }

    public void sendRequestToForgotPassword(String url) {

        //url = url + "?" + "email=" + email + "&password=" + password;
        final String jsonBody = getJsonBody();
        if(jsonBody.isEmpty())return;
        Log.d("DEBUG", jsonBody);
        // TODO Auto-generated method stub
        if (!isProgressDialogShowing())
            showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG_response",response);


                        dismissProgressDialog();

                        CommonResponse commonResponse = MydApplication.gson.fromJson(response,CommonResponse.class);

                        if(commonResponse.getSuccess() == 1 ){

                            AlertDialogForAnything.showAlertDialogWhenComplte(ForgotPasswordActivity.this,"Success","Password recovery successfull. Please check you email inbox for further instruction", true);

                        }else{
                            AlertDialogForAnything.showAlertDialogWhenComplte(ForgotPasswordActivity.this,"Success",commonResponse.getError().get(0), true);
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                Log.d("DEBUG","its in error");
                VolleyLog.d("DEBUG", "Error: " + error.getMessage());

            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonBody == null ? null : jsonBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonBody, "utf-8");
                    return null;
                }
            }

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

    private String getJsonBody() {
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("email", ed_email.getText().toString());
            return jsonBody.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return "";
    }

    private boolean showWarning(){
        boolean isFormValid = true;

        if (ed_email.getText().toString().isEmpty()) {
            ed_email.setError("Required");
            isFormValid = false;
        }
        if (!ed_email.getText().toString().isEmpty() && !CommonMethods.isEmailValid(ed_email.getText().toString())) {
            ed_email.setError("Invalid email");
            isFormValid = false;
        }
        return isFormValid;
    }
}
