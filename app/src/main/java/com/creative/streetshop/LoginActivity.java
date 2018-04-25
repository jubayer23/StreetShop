package com.creative.streetshop;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.creative.streetshop.model.LoginRegistration;
import com.creative.streetshop.model.UserData;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btn_login, btn_fb_login;

    private LinearLayout btn_create_account;

    private EditText ed_email, ed_password;

    private TextView tv_forgot_password;


    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("DEBUG", "Login");
                        Log.d("DEBUG", loginResult.getAccessToken().getToken());


                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {


                                        final JSONObject jsonObject = response.getJSONObject();
                                        // Application code
                                        try {

                                            String email = jsonObject.getString("email");
                                            if (email != null && !email.isEmpty()) {

                                                Log.d("DEBUG_email", email);

                                            } else {
                                               dismissProgressDialog();
                                                AlertDialogForAnything.showAlertDialogWithoutTitle(LoginActivity.this,
                                                        "Facebook registration is not available. Please sign up for a LifeData account using your email address or add an email in your Facebook Settings and try again.", false);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            dismissProgressDialog();
                                            AlertDialogForAnything.showAlertDialogWithoutTitle(LoginActivity.this,
                                                    "Facebook login is not working. Please sign up using your email address or Twitter.", false);


                                        }
                                        // 01/31/1980 format
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        init();
    }

    private void init() {


        ed_email = findViewById(R.id.ed_email);
        ed_password = findViewById(R.id.ed_password);


        btn_create_account = findViewById(R.id.btn_create_account);
        btn_create_account.setOnClickListener(this);

        btn_login = (ImageView) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        tv_forgot_password = findViewById(R.id.tv_forgot_password);
        tv_forgot_password.setOnClickListener(this);


        btn_fb_login = findViewById(R.id.btn_fb_login);
        btn_fb_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.btn_create_account) {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (id == R.id.btn_login) {

            if (!DeviceInfoUtils.isConnectingToInternet(this)) {
                AlertDialogForAnything.showAlertDialogWhenComplte(this, "Alert!", "No internet connection! Please connect to working internet connection.", false);
            }

            if (showWarning()) {
                if (MydApplication.getInstance().getPrefManger().getSession().isEmpty()) {
                    getSessionFirstThenLogin();
                } else {
                    sendRequestToLogin(GlobalAppAccess.URL_LOGIN);
                }
            }
        }

        if (id == R.id.btn_fb_login) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
        }

        if (id == R.id.tv_forgot_password) {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }

    }

    private void getSessionFirstThenLogin() {
        showProgressDialog("please wait..", true, false);
        SessionManager.LocationResult locationResult = new SessionManager.LocationResult() {

            @Override
            public void gotSession(String session) {

                if (!session.equals(GlobalAppAccess.ERROR_TYPE_NETWORK_PROBLEM) &&
                        !session.equals(GlobalAppAccess.ERROR_TYPE_SERVER_PROBLEM)) {
                    MydApplication.getInstance().getPrefManger().setSession(session);
                    sendRequestToLogin(GlobalAppAccess.URL_LOGIN);
                } else {
                    dismissProgressDialog();
                    AlertDialogForAnything.showAlertDialogWhenComplte(LoginActivity.this, "Error!", "Problem in getting the session!", false);
                }

            }
        };
        SessionManager myLocation = new SessionManager();
        myLocation.getSession(this, locationResult);
    }


    public void sendRequestToLogin(String url) {

        //url = url + "?" + "email=" + email + "&password=" + password;
        final String jsonBody = getJsonBody();
        if (jsonBody.isEmpty()) return;
        Log.d("DEBUG", jsonBody);
        // TODO Auto-generated method stub
        if (!isProgressDialogShowing())
            showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG_response", response);


                        dismissProgressDialog();

                        LoginRegistration loginRegistration = MydApplication.gson.fromJson(response, LoginRegistration.class);

                        if (loginRegistration.getSuccess() == 1) {

                            UserData userData = loginRegistration.getUserData();


                            MydApplication.getInstance().getPrefManger().setUserData(userData);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                Log.d("DEBUG", "its in error");
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
            jsonBody.put("password", ed_password.getText().toString());
            return jsonBody.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean showWarning() {
        boolean isFormValid = true;

        if (ed_email.getText().toString().isEmpty()) {
            ed_email.setError("Required");
            isFormValid = false;
        }
        if (!ed_email.getText().toString().isEmpty() && !CommonMethods.isEmailValid(ed_email.getText().toString())) {
            ed_email.setError("Invalid email");
            isFormValid = false;
        }

        if (ed_password.getText().toString().isEmpty()) {
            ed_password.setError("Required");
            isFormValid = false;
        }

        return isFormValid;
    }
}
