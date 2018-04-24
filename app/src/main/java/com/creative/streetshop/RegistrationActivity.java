package com.creative.streetshop;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.creative.streetshop.Utility.CommonMethods;
import com.creative.streetshop.Utility.DeviceInfoUtils;
import com.creative.streetshop.alertbanner.AlertDialogForAnything;
import com.creative.streetshop.appdata.GlobalAppAccess;
import com.creative.streetshop.appdata.MydApplication;
import com.creative.streetshop.helperClass.SessionManager;
import com.creative.streetshop.model.LoginRegistration;
import com.creative.streetshop.model.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener {

    private EditText ed_full_name, ed_email, ed_address, ed_contact_number, ed_country, ed_city, ed_password, ed_confirm_password;

    private ImageView btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rergistration);

        init();
    }

    private void init() {

        ed_full_name = findViewById(R.id.ed_full_name);
        ed_email = findViewById(R.id.ed_email);
        ed_address = findViewById(R.id.ed_address);
        ed_contact_number = findViewById(R.id.ed_contact_number);
        ed_country = findViewById(R.id.ed_country);
        ed_city = findViewById(R.id.ed_city);
        ed_password = findViewById(R.id.ed_password);
        ed_confirm_password = findViewById(R.id.ed_confirm_password);

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_register) {

            if (!DeviceInfoUtils.isConnectingToInternet(this)) {
                AlertDialogForAnything.showAlertDialogWhenComplte(this, "Alert!", "No internet connection! Please connect to working internet connection.", false);
            }

            if (showWarning()) {

                if (MydApplication.getInstance().getPrefManger().getSession().isEmpty()) {
                    getSessionFirstThenRegister();
                } else {
                    sendRequestToRegister(GlobalAppAccess.URL_REGISTER);
                }

            }
        }

    }

    private void getSessionFirstThenRegister() {
        showProgressDialog("please wait..", true, false);
        SessionManager.LocationResult locationResult = new SessionManager.LocationResult() {

            @Override
            public void gotSession(String session) {

                if (!session.equals(GlobalAppAccess.ERROR_TYPE_NETWORK_PROBLEM) &&
                        !session.equals(GlobalAppAccess.ERROR_TYPE_SERVER_PROBLEM)) {
                    MydApplication.getInstance().getPrefManger().setSession(session);
                    sendRequestToRegister(GlobalAppAccess.URL_REGISTER);
                } else {
                    dismissProgressDialog();
                    AlertDialogForAnything.showAlertDialogWhenComplte(RegistrationActivity.this, "Error!", "Problem in getting the session!", false);
                }

            }
        };
        SessionManager myLocation = new SessionManager();
        myLocation.getSession(this, locationResult);
    }


    public void sendRequestToRegister(String url) {

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

                        LoginRegistration loginRegistration = MydApplication.gson.fromJson(response,LoginRegistration.class);

                        if(loginRegistration.getSuccess() == 1 ){

                            UserData userData = loginRegistration.getUserData();

                            UserData.AccountCustomField accountCustomField = userData.getAccountCustomField();
                            accountCustomField.setCity(userData.getCustomField().getAccount().getCity());
                            accountCustomField.setCountry(userData.getCustomField().getAccount().getCountry());
                            accountCustomField.setResidentialAddress(userData.getCustomField().getAccount().getResidentialAddress());

                            userData.setAccountCustomField(accountCustomField);

                            MydApplication.getInstance().getPrefManger().setUserData(userData);
                            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                            startActivity(intent);

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

    private String getJsonBody(){
        JSONObject jsonBody = new JSONObject();
        JSONObject custom_field = new JSONObject();
        JSONObject account = new JSONObject();

        try {
            jsonBody.put("firstname", ed_full_name.getText().toString());
            jsonBody.put("lastname", ed_full_name.getText().toString());
            jsonBody.put("email", ed_email.getText().toString());
            jsonBody.put("password", ed_password.getText().toString());
            jsonBody.put("confirm", ed_confirm_password.getText().toString());
            jsonBody.put("telephone", ed_contact_number.getText().toString());
            jsonBody.put("agree", "1");


            account.put("residential_address", ed_address.getText().toString());
            account.put("country", ed_country.getText().toString());
            account.put("city", ed_city.getText().toString());

            custom_field.put("account", account);

            jsonBody.put("custom_field",custom_field);

            return jsonBody.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }


    private boolean showWarning() {
        boolean isFormValid = true;

        if (ed_full_name.getText().toString().isEmpty()) {
            ed_full_name.setError("Required");
            isFormValid = false;
        }

        if (ed_email.getText().toString().isEmpty()) {
            ed_email.setError("Required");
            isFormValid = false;
        }
        if (!ed_email.getText().toString().isEmpty() && !CommonMethods.isEmailValid(ed_email.getText().toString())) {
            ed_email.setError("Invalid email");
            isFormValid = false;
        }

        if (ed_address.getText().toString().isEmpty()) {
            ed_address.setError("Required");
            isFormValid = false;
        }
        if (ed_contact_number.getText().toString().isEmpty()) {
            ed_contact_number.setError("Required");
            isFormValid = false;
        }
        if (ed_country.getText().toString().isEmpty()) {
            ed_country.setError("Required");
            isFormValid = false;
        }
        if (ed_city.getText().toString().isEmpty()) {
            ed_city.setError("Required");
            isFormValid = false;
        }
        if (ed_password.getText().toString().isEmpty()) {
            ed_password.setError("Required");
            isFormValid = false;
        }

        if (!ed_password.getText().toString().isEmpty() && ed_password.getText().toString().length() < 6) {
            ed_password.setError("Password too short!");
            isFormValid = false;
        }
        if (ed_confirm_password.getText().toString().isEmpty()) {
            ed_confirm_password.setError("Required");
            isFormValid = false;
        }

        if (!ed_password.getText().toString().isEmpty() && !ed_confirm_password.getText().toString().isEmpty()
                && !ed_password.getText().toString().equals(ed_confirm_password.getText().toString())) {
            ed_confirm_password.setError("Password does not match");
            isFormValid = false;
        }

        return isFormValid;


    }
}
