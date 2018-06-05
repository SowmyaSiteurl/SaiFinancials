package in.siteurl.www.saifinance.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.apis.Constants;

public class changePasswordActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    private TextInputLayout mOldPassword, mNewPassword, mConfirmPassword;
    private RelativeLayout mPasswordLayout;
    String sessionId, uid;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    private Toolbar mToolbar;
    Dialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initializing views
        mOldPassword = findViewById(R.id.old_password);
        mNewPassword = findViewById(R.id.new_password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mPasswordLayout = findViewById(R.id.changePasswordLayout);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //toolbar
        mToolbar = findViewById(R.id.main_toolbar);
        mToolbar.setTitle("Change Password");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkInternetConnection();

    }

    //validating change password credentials
    public void validateChangePassword(View view) {

        if (TextUtils.isEmpty(mOldPassword.getEditText().getText().toString().trim())) {
            mOldPassword.setError("Old Password");
            return;
        }

        if (TextUtils.isEmpty(mNewPassword.getEditText().getText().toString().trim())) {
            mNewPassword.setError("New Password");
            return;
        }

        if (TextUtils.isEmpty(mConfirmPassword.getEditText().getText().toString().trim())) {
            mConfirmPassword.setError("Confirm Password");
            return;
        }

        if (mOldPassword.getEditText().getText().toString().length() < 5) {
            mOldPassword.setError("Password should be minimum 5 characters");
            Snackbar.make(mPasswordLayout, "Password should be minimum 5 characters", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (mNewPassword.getEditText().getText().toString().length() < 5) {
            mNewPassword.setError("Password should be minimum 5 characters");
            Snackbar.make(mPasswordLayout, "Password should be minimum 5 characters", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (mConfirmPassword.getEditText().getText().toString().length() < 5) {
            mConfirmPassword.setError("Password should be minimum 5 characters");
            Snackbar.make(mPasswordLayout, "Password should be minimum 5 characters", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (!mNewPassword.getEditText().getText().toString().equals(mConfirmPassword.getEditText().getText().toString())) {
            mNewPassword.setError("Password Didn't Match");
            mConfirmPassword.setError("Password Didn't Match");
            Snackbar.make(mPasswordLayout, "Password Didn't Match", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        changePassword(mOldPassword.getEditText().getText().toString().trim(), mNewPassword.getEditText().getText().toString().trim(), mConfirmPassword.getEditText().getText().toString().trim());

    }

    //this is the method for change password in server
    private void changePassword(final String oldPassword, final String newPassword, final String confirmPassword) {

        final AlertDialog loadingDialog = new SpotsDialog(changePasswordActivity.this, R.style.Custom);
        loadingDialog.show();

        StringRequest changePasswordRequest = new StringRequest(Request.Method.POST, Constants.changePassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                try {
                    JSONObject passwordObject = new JSONObject(response);
                    String error = passwordObject.getString("Error");
                    String message = passwordObject.getString("Message");
                    showAlertDialog(error, message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                }
                if (error instanceof ServerError) {
                    Toast.makeText(changePasswordActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(changePasswordActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(changePasswordActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(changePasswordActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(changePasswordActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(changePasswordActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(changePasswordActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("current_password", oldPassword);
                params.put("new_password", newPassword);
                params.put("confirm_password", confirmPassword);
                return params;
            }
        };
        changePasswordRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(changePasswordRequest);
    }

    //To show alert Dialog for change password response
    private void showAlertDialog(final String error, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(changePasswordActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Sai Finance");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (error.equals("true")) {
                    dialogInterface.dismiss();
                } else {
                    dialogInterface.dismiss();
                    startActivity(new Intent(changePasswordActivity.this, LoginActivity.class).
                            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    editor.clear();
                    editor.commit();
                    finish();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(changePasswordActivity.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(changePasswordActivity.this);
            loginErrorBuilder.setTitle("Error");
            loginErrorBuilder.setMessage(message);
            loginErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            loginErrorBuilder.show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    //this is the method to check internet connection
    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }

    private void showDialog(boolean isConnected) {

        if (isConnected) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } else {
            alertDialog.setContentView(R.layout.check_internet);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            Button button = alertDialog.findViewById(R.id.tryAgain);
            Button exit = alertDialog.findViewById(R.id.exit);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.exit(0);

                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    checkInternetConnection();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
    }
}
