package in.siteurl.www.saifinance.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.apis.Constants;

public class RegisterActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextInputLayout nameTextInputLayout, emailTextInputLayout, passwordTextInputLayout, phoneTextInputLayout, dobTextInputLayout, panTextInputLayout, aadhaarTextInputLayout, anniversaryTextInputLayout;
    EditText nameEditText, emailEditText, passwordEditText, phoneEditText, dobEditText, panEditText, aadhaarEditText, anniversaryEditText;
    String str_name, str_email, str_password, str_phone, str_dob, str_pan, str_aadhaar, str_anniversary;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    private SpotsDialog dialog;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    Dialog alertDialog;
    LinearLayout registerRootLayout;
    Calendar calendar;
    ImageView cal_anniversary, cal_dob;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initializing Views
        nameEditText = findViewById(R.id.rNameEditText);
        emailEditText = findViewById(R.id.rEmailEditText);
        passwordEditText = findViewById(R.id.rPasswordEditText);
        phoneEditText = findViewById(R.id.rPhoneEditText);
        panEditText = findViewById(R.id.rPanEditText);
        aadhaarEditText = findViewById(R.id.rAadharEditText);
        dobEditText = findViewById(R.id.dateEditText);
        anniversaryEditText = findViewById(R.id.rAnniversaryEditText);
        nameTextInputLayout = findViewById(R.id.rNameTextInputLayout);
        emailTextInputLayout = findViewById(R.id.rEmailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.rPasswordTextInputLayout);
        phoneTextInputLayout = findViewById(R.id.rPhoneTextInputLayout);
        panTextInputLayout = findViewById(R.id.rPanTextInputLayout);
        aadhaarTextInputLayout = findViewById(R.id.rAadharTextInputLayout);
        dobTextInputLayout = findViewById(R.id.dateTextInputLayout);
        anniversaryTextInputLayout = findViewById(R.id.rAnniversaryTextInputLayout);
        registerRootLayout = findViewById(R.id.registerPage);
        cal_anniversary = findViewById(R.id.calender_anniversary);
        cal_dob = findViewById(R.id.calender_dob);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarTitle = mToolbar.findViewById(R.id.register_title);
        toolbarTitle.setText("Register Here");

        //spot dialog function
        dialog = new SpotsDialog(RegisterActivity.this, R.style.Custom);
        dialog.dismiss();

        //calender
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        //calender on ClickListener function to select DOB
        cal_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        //calender on ClickListener function for Anniversary date
        cal_anniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerAnniversary();
            }
        });

        checkInternetConnection();


    }

    //this is the method for select DOB in datePicker
    public void datePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dobEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
        datePickerDialog.show();
    }

    //this is the method to select Anniversary date in datePicker
    public void datePickerAnniversary() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        anniversaryEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
        datePickerDialog.show();
    }


    //this is the method for validations of Register
    public void Register(View view) {

        dialog.show();
        str_name = nameEditText.getText().toString().trim();
        str_email = emailEditText.getText().toString().trim();
        str_password = passwordEditText.getText().toString().trim();
        str_phone = phoneEditText.getText().toString().trim();
        str_dob = dobEditText.getText().toString().trim();
        str_pan = panEditText.getText().toString().trim();
        str_aadhaar = aadhaarEditText.getText().toString().trim();
        str_anniversary = anniversaryEditText.getText().toString().trim();
        validate();
    }

    //validations
    public void validate() {

        if (str_name.equals("") || (str_name.equals(null))) {

            nameTextInputLayout.setError("Please enter valid Name");
            dialog.dismiss();
            return;

        } else {
            nameTextInputLayout.setError(null);
            dialog.dismiss();
        }

        if (str_email.equals("") || (str_email.equals(null) || !(str_email.matches(EMAIL_PATTERN)))) {

            emailTextInputLayout.setError("Please enter valid Email ID");
            dialog.dismiss();
            return;

        } else {
            emailTextInputLayout.setError(null);
            dialog.dismiss();
        }

        if (str_password.equals("") || (str_password.equals(null))) {

            passwordTextInputLayout.setError("Please enter valid Password");
            dialog.dismiss();
            return;

        } else {
            passwordTextInputLayout.setError(null);
            dialog.dismiss();
        }

        if (str_phone.equals("") || (str_phone.equals(null) || (str_phone.length() != 10))) {

            phoneTextInputLayout.setError("Please enter valid Phone No.");
            dialog.dismiss();
            return;

        } else {
            phoneTextInputLayout.setError(null);
            dialog.dismiss();
        }

        if (str_dob.equals("") || (str_dob.equals(null))) {

            dobTextInputLayout.setError("DOB cannot be empty");
            dialog.dismiss();
            return;

        } else {
            dobTextInputLayout.setError(null);
            dialog.dismiss();
        }

        if (str_pan.equals("") || (str_pan.equals(null))) {

            panTextInputLayout.setError("Please enter valid Pan No.");
            dialog.dismiss();
            return;

        } else {
            panTextInputLayout.setError(null);
            dialog.dismiss();
        }

        if (str_aadhaar.equals("") || (str_aadhaar.equals(null))) {

            aadhaarTextInputLayout.setError("Please enter valid Aadhaar No.");
            dialog.dismiss();
            return;

        } else {
            aadhaarTextInputLayout.setError(null);
            dialog.dismiss();
        }

        if (str_anniversary.equals("") || (str_anniversary.equals(null))) {

            anniversaryTextInputLayout.setError("Anniversary Date cannot be empty");
            dialog.dismiss();
            return;

        } else {
            dialog.show();
            anniversaryTextInputLayout.setError(null);
            Register();
        }
    }

    //this is the Method to send register details to server
    public void Register() {

        final AlertDialog loadingDialog = new SpotsDialog(RegisterActivity.this, R.style.Custom);
        loadingDialog.show();

        StringRequest registerRequest = new StringRequest(Request.Method.POST, Constants.register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialog.dismiss();
                try {
                    JSONObject register = new JSONObject(response);
                    String error = register.getString("Error");
                    String message = register.getString("Message");

                    if (error.equals("true")) {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (error.equals("false")) {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                      //  finish();
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
                    Toast.makeText(RegisterActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(RegisterActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(RegisterActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(RegisterActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(RegisterActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(RegisterActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                params.put("name", str_name);
                params.put("email", str_email);
                params.put("password", str_password);
                params.put("phone_no", str_phone);
                params.put("dob", str_dob);
                params.put("pan_no", str_pan);
                params.put("aadhar_no", str_aadhaar);
                params.put("anniversary_date", str_aadhaar);
                params.put("api_key", Constants.APIKEY);
                return params;
            }

        };
        registerRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(registerRequest);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(RegisterActivity.this);
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
