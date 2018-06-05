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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.apis.Constants;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    EditText emailEditText, passwordEditText;
    String str_email, str_password;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    private SpotsDialog dialog;
    private Toolbar mToolbar;
    Dialog alertDialog;
    LinearLayout LoginRootLayout;
    private TextView mForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initializing Views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        LoginRootLayout = findViewById(R.id.loginPage);
        mForgotPassword = findViewById(R.id.forgotPassword);

        //Toolbar
        mToolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(mToolbar);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //spot dialog function
        dialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
       // dialog.dismiss();

        //forgot password onClickListener
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });

        checkInternetConnection();
    }


    //To show forgot password dialog
    public void showForgotPasswordDialog() {

        android.app.AlertDialog.Builder forgotPasswordBuilder = new android.app.AlertDialog.Builder(LoginActivity.this);
        forgotPasswordBuilder.setTitle("Forgot Password");
        forgotPasswordBuilder.setMessage("Please enter your registered email.");
        View customLayout = getLayoutInflater().inflate(R.layout.forgot_password_layout, null);
        final MaterialEditText email = customLayout.findViewById(R.id.edtEmail);
        forgotPasswordBuilder.setView(customLayout);
        forgotPasswordBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String forgotEmail = email.getText().toString().trim();
                if (TextUtils.isEmpty(forgotEmail) || (!forgotEmail.matches(EMAIL_PATTERN))) {
                    Toast.makeText(LoginActivity.this, "Enter Valid Email", Toast.LENGTH_LONG).show();
                    showForgotPasswordDialog();
                    return;
                }
                sendForgotPasswordEmailToServer(forgotEmail);

            }
        });

        forgotPasswordBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        forgotPasswordBuilder.show();
    }

    public void Login(View view) {

       // dialog.show();
        str_email = emailEditText.getText().toString().trim();
        str_password = passwordEditText.getText().toString().trim();

        validate();
    }

    //validations for email and password
    public void validate() {

        if (str_email.equals("") || (str_email.equals(null) || !(str_email.matches(EMAIL_PATTERN)))) {

            emailTextInputLayout.setError("Please enter valid Email");
           // dialog.dismiss();
            return;

        } else {
            emailTextInputLayout.setError(null);
          //  dialog.dismiss();
        }

        if (str_password.equals("") || (str_password.equals(null))) {

            passwordTextInputLayout.setError("Please enter valid Password");
           // dialog.dismiss();
            return;

        } else {
           // dialog.show();
            passwordTextInputLayout.setError(null);
            Sign_in();

        }
    }

    //this is the Method to send login details to server
    public void Sign_in() {

        final AlertDialog loadingDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        loadingDialog.show();

        StringRequest loginRequest = new StringRequest(Request.Method.POST, Constants.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                  loadingDialog.dismiss();

                try {
                    JSONObject signIn = new JSONObject(response);
                    String error = signIn.getString("Error");
                    String message = signIn.getString("Message");

                    if (error.equals("true")) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        Snackbar.make(LoginRootLayout, message, Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (error.equals("false")) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                        String sid = signIn.getString("sid");
                        String data = signIn.getString("data");
                        JSONObject jsonObject = new JSONObject(data);

                        String userId = jsonObject.getString("user_id");
                        String userName = jsonObject.getString("name");
                        String userEmail = jsonObject.getString("email");
                        String userPhone = jsonObject.getString("phone_no");
                        String userDob = jsonObject.getString("dob");
                        String userPan = jsonObject.getString("pan_no");
                        String userAadhaar = jsonObject.getString("aadhar_no");
                        String userAnniversaryDate = jsonObject.getString("anniversary_date");

                        editor.putString("loginUserId", userId);
                        editor.putString("loginName", userName);
                        editor.putString("loginEmail", userEmail);
                        editor.putString("loginPhone", userPhone);
                        editor.putString("loginDob", userDob);
                        editor.putString("loginPan", userPan);
                        editor.putString("loginAadhaar", userAadhaar);
                        editor.putString("loginAnnvrsyDate", userAnniversaryDate);
                        editor.putString("loginSid", sid);
                        editor.commit();

                        emailEditText.getText().clear();
                        passwordEditText.getText().clear();

                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        Snackbar.make(LoginRootLayout, "Invalid Credentials", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

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
                    Toast.makeText(LoginActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(LoginActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(LoginActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(LoginActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                params.put("username", str_email);
                params.put("password", str_password);
                params.put("api_key", Constants.APIKEY);
                return params;
            }

        };
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(loginRequest);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(LoginActivity.this);
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


    //this is the Onclick Function for Register
    public void SignUpNow(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    //this is the method to send email to server(for forgot password)
    public void sendForgotPasswordEmailToServer(final String forgotEmail) {
        final AlertDialog loadingDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        loadingDialog.show();

        StringRequest loginRequest = new StringRequest(Request.Method.POST, Constants.forgotPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loadingDialog.dismiss();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");

                            if (Error.equals("false")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

                            }

                            if (Error.equals("true")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

                            }
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
                    Toast.makeText(LoginActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(LoginActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(LoginActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(LoginActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", forgotEmail);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(LoginActivity.this).addToRequestQueue(loginRequest);
    }

    //Dialog box to show the forgot email
    public void emailAlert(String message) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Sai Finance");
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.setCancelable(false);
        builder.show();

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

