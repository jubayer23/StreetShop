package com.creative.streetshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String LOGIN_TYPE_NORMAL = "normal";
    private static final String LOGIN_TYPE_FACEBOOK = "facebook";
    private static final String LOGIN_TYPE_TWITTER = "twitter";
    private ImageView btn_login, btn_fb_login, btn_twitter_login;

    private LinearLayout btn_create_account;

    private EditText ed_email, ed_password;

    private TextView tv_forgot_password;


    private CallbackManager mCallbackManager;

    private String social_login_email, social_login__access_token;

    private TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        init();

        initFaceBookLogin();

        initTwitterLogin();
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

        btn_twitter_login = findViewById(R.id.btn_twitter_login);
        btn_twitter_login.setOnClickListener(this);

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterLoginButton);
    }

    private void initFaceBookLogin() {
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Log.d("DEBUG", "Login");
                        // Log.d("DEBUG", loginResult.getAccessToken().getToken());
                        social_login__access_token = "";
                        social_login_email = "";
                        showProgressDialog("Please wait...", true, false);
                        social_login__access_token = loginResult.getAccessToken().getToken();


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

                                                //Log.d("DEBUG_email", email);
                                                social_login_email = email;

                                                if (MydApplication.getInstance().getPrefManger().getSession().isEmpty()) {
                                                    getSessionFirstThenLogin(LOGIN_TYPE_FACEBOOK);
                                                } else {
                                                    sendRequestToLogin(GlobalAppAccess.URL_SOCIAL_LOGIN, LOGIN_TYPE_FACEBOOK);
                                                }

                                            } else {
                                                dismissProgressDialog();
                                                AlertDialogForAnything.showAlertDialogWithoutTitle(LoginActivity.this,
                                                        "Facebook registration is not available. Please sign up for a Steetshop account using your email address or add an email in your Facebook Settings and try again.", false);
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
    }

    //////****fro Twitter///////////
    private void initTwitterLogin() {
        // TODO Auto-generated method stub
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                showProgressDialog("Please wait...", true, false);

                social_login__access_token = "";
                social_login_email = "";

                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                final String token = authToken.token;
                final String secret = authToken.secret;

                //twitterUserName = session.getUserName();

                TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true).enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        //String name = userResult.data.name;
                        social_login__access_token = token;
                        social_login_email = userResult.data.email;

                        if (social_login_email != null && !social_login_email.isEmpty()) {
                            if (MydApplication.getInstance().getPrefManger().getSession().isEmpty()) {
                                getSessionFirstThenLogin(LOGIN_TYPE_TWITTER);
                            } else {
                                sendRequestToLogin(GlobalAppAccess.URL_SOCIAL_LOGIN, LOGIN_TYPE_TWITTER);
                            }
                        }else {
                            dismissProgressDialog();
                            AlertDialogForAnything.showAlertDialogWithoutTitle(LoginActivity.this,
                                    "Twitter registration is not available. Please sign up for a Steetshop account using your email address or add an email in your Facebook Settings and try again.", false);
                        }




                    }

                    @Override
                    public void failure(com.twitter.sdk.android.core.TwitterException exc) {
                        // Log.d("TwitterKit", "Verify Credentials Failure", exc);
                        //Toast.makeText(UserRegistrationActivity.this,"Login authentication failed!",Toast.LENGTH_LONG).show();
                        dismissProgressDialog();
                        AlertDialogForAnything.showAlertDialogWhenComplte(LoginActivity.this, "Error", "Login authentication failed!", false);
                    }
                });

            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException exception) {
                // Do something on failure
                //  Toast.makeText(UserRegistrationActivity.this,"Login authentication failed!",Toast.LENGTH_LONG).show();
                AlertDialogForAnything.showAlertDialogWhenComplte(LoginActivity.this, "Error", "Login authentication failed!", false);
            }
        });
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

        if (id == R.id.tv_forgot_password) {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }

        if (!DeviceInfoUtils.isConnectingToInternet(this)) {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Alert!", "No internet connection! Please connect to working internet connection.", false);
            return;
        }


        if (id == R.id.btn_login) {


            if (showWarning()) {
                if (MydApplication.getInstance().getPrefManger().getSession().isEmpty()) {
                    getSessionFirstThenLogin(LOGIN_TYPE_NORMAL);
                } else {
                    sendRequestToLogin(GlobalAppAccess.URL_LOGIN_NORMAL, LOGIN_TYPE_NORMAL);
                }
            }
        }

        if (id == R.id.btn_fb_login) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
        }

        if (id == R.id.btn_twitter_login) {
            //new TokenGet().execute();
            twitterLoginButton.performClick();
        }


    }

    private void getSessionFirstThenLogin(final String login_type) {
        if (!isProgressDialogShowing())
            showProgressDialog("please wait..", true, false);
        SessionManager.LocationResult locationResult = new SessionManager.LocationResult() {

            @Override
            public void gotSession(String session) {

                if (!session.equals(GlobalAppAccess.ERROR_TYPE_NETWORK_PROBLEM) &&
                        !session.equals(GlobalAppAccess.ERROR_TYPE_SERVER_PROBLEM)) {
                    MydApplication.getInstance().getPrefManger().setSession(session);
                    if (login_type.equals(LOGIN_TYPE_NORMAL)) {
                        sendRequestToLogin(GlobalAppAccess.URL_LOGIN_NORMAL, login_type);
                    } else {
                        sendRequestToLogin(GlobalAppAccess.URL_SOCIAL_LOGIN, login_type);
                    }

                } else {
                    dismissProgressDialog();
                    AlertDialogForAnything.showAlertDialogWhenComplte(LoginActivity.this, "Error!", "Problem in getting the session!", false);
                }

            }
        };
        SessionManager myLocation = new SessionManager();
        myLocation.getSession(this, locationResult);
    }


    public void sendRequestToLogin(String url, String login_type) {

        //url = url + "?" + "email=" + email + "&password=" + password;
        final String jsonBody = getJsonBody(login_type);
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

    private String getJsonBody(String login_type) {
        JSONObject jsonBody = new JSONObject();

        try {

            switch (login_type) {
                case LOGIN_TYPE_FACEBOOK:
                    jsonBody.put("email", social_login_email);
                    jsonBody.put("access_token", social_login__access_token);
                    jsonBody.put("provider", "facebook");
                    break;
                case LOGIN_TYPE_NORMAL:
                    jsonBody.put("email", ed_email.getText().toString());
                    jsonBody.put("password", ed_password.getText().toString());
                    break;
                case LOGIN_TYPE_TWITTER:
                    jsonBody.put("email", social_login_email);
                    jsonBody.put("access_token", social_login__access_token);
                    jsonBody.put("provider", "twitter");
                    break;
            }

            return jsonBody.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        twitterLoginButton.onActivityResult(requestCode, resultCode, data);

        // mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
        if (mCallbackManager != null && mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
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
