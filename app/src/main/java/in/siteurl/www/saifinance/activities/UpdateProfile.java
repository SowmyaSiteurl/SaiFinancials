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
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.apis.Constants;
import in.siteurl.www.saifinance.objects.profileDetails;

public class UpdateProfile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextInputLayout nameTextInputLayout, emailTextInputLayout, phoneTextInputLayout, dobTextInputLayout, panTextInputLayout, aadhaarTextInputLayout, anniversaryTextInputLayout;
    EditText nameEditText, emailEditText, phoneEditText, dobEditText, panEditText, aadhaarEditText, anniversaryEditText;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    private SpotsDialog dialog;
    private Toolbar mToolbar;
    LinearLayout profileRootLayout;
    Calendar calendar;
    Dialog alertDialog;
    ImageView cal_anniversary, cal_dob;
    private int mYear, mMonth, mDay;
    private profileDetails profileDetails;
    String edt_dob_date, edt_anndob_date;
    String userId, name, email, phoneNo, dob, panNo, aadhaarNo, anniversaryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //Initializing Views
        nameEditText = findViewById(R.id.profileNameEditText);
        emailEditText = findViewById(R.id.profilelEmailEditText);
        phoneEditText = findViewById(R.id.profilePhomeEditText);
        panEditText = findViewById(R.id.profilePanEditText);
        aadhaarEditText = findViewById(R.id.profileAadharEditText);
        dobEditText = findViewById(R.id.profileDobEditText);
        anniversaryEditText = findViewById(R.id.profileAnniversaryEditText);
        nameTextInputLayout = findViewById(R.id.profileNameTextInputLayout);
        emailTextInputLayout = findViewById(R.id.profileEmailTextInputLayout);
        phoneTextInputLayout = findViewById(R.id.profilePhoneTextInputLayout);
        panTextInputLayout = findViewById(R.id.profilePanTextInputLayout);
        aadhaarTextInputLayout = findViewById(R.id.profileAadharTextInputLayout);
        dobTextInputLayout = findViewById(R.id.profileDobTextInputLayout);
        anniversaryTextInputLayout = findViewById(R.id.profileAnniversaryTextInputLayout);
        profileRootLayout = findViewById(R.id.profilePage);
        cal_anniversary = findViewById(R.id.calenderAnniversary);
        cal_dob = findViewById(R.id.calenderDob);

        //Toolbar
        mToolbar = findViewById(R.id.profile_toolbar);
        mToolbar.setTitle("Update Profile");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //spot dialog function
        dialog = new SpotsDialog(UpdateProfile.this, R.style.Custom);
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
        toGetProfileDetailsFromServer();

    }

    //this is the method for select DOB in datePicker
    public void datePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfile.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        edt_dob_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        dobEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
        datePickerDialog.show();
    }

    //this is the method to select Anniversary date in datePicker
    public void datePickerAnniversary() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfile.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        edt_anndob_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        anniversaryEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
        datePickerDialog.show();
    }

    //this is the method to get user Details from server
    private void toGetProfileDetailsFromServer() {

        final AlertDialog loadingDialog = new SpotsDialog(UpdateProfile.this, R.style.Custom);
        loadingDialog.show();

        StringRequest getAdminDetails = new StringRequest(Request.Method.POST, Constants.viewProfile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();


                try {
                    JSONObject viewProfile = new JSONObject(response);
                    String error = viewProfile.getString("Error");
                    String message = viewProfile.getString("Message");

                    if (error.equals("true")) {
                        editor.putString("loginUserId", "");
                        editor.putString("loginName", "");
                        editor.putString("loginEmail", "");
                        editor.putString("loginPhone", "");
                        editor.putString("loginDob", "");
                        editor.putString("loginPan", "");
                        editor.putString("loginAadhaar", "");
                        editor.putString("loginAnnvrsyDate", "");
                        editor.putString("loginSid", "");
                        editor.commit();
                        finishAffinity();

                        Toast.makeText(UpdateProfile.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UpdateProfile.this, LoginActivity.class);
                        startActivity(intent);

                    }

                    if (error.equals("false")) {

                        JSONObject profileObject = viewProfile.getJSONObject("data");

                        userId = profileObject.getString("user_id");
                        name = profileObject.getString("name");
                        email = profileObject.getString("email");
                        phoneNo = profileObject.getString("phone_no");
                        dob = profileObject.getString("dob");
                        panNo = profileObject.getString("pan_no");
                        aadhaarNo = profileObject.getString("aadhar_no");
                        anniversaryDate = profileObject.getString("anniversary_date");

                        profileDetails = new profileDetails(userId, name, email, phoneNo, dob, panNo,aadhaarNo, anniversaryDate);

                        nameEditText.setText(name);
                        emailEditText.setText(email);
                        phoneEditText.setText(phoneNo);
                        dobEditText.setText(dob);
                        edt_dob_date = dob;
                        panEditText.setText(panNo);
                        aadhaarEditText.setText(aadhaarNo);
                        anniversaryEditText.setText(anniversaryDate);
                        edt_anndob_date = anniversaryDate;

                    } else {
                        Snackbar.make(profileRootLayout, message, Snackbar.LENGTH_LONG).show();
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
                    Toast.makeText(UpdateProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(UpdateProfile.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(UpdateProfile.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(UpdateProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(UpdateProfile.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(UpdateProfile.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(UpdateProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                return params;
            }
        };

        getAdminDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(getAdminDetails);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(UpdateProfile.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(UpdateProfile.this);
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


    //this is the method to update user details to server
    public void updateProfile(View view) {

        if (checkPreviousData()) {
            final AlertDialog loadingDialog = new SpotsDialog(UpdateProfile.this, R.style.Custom);
            loadingDialog.show();
            StringRequest saveAdminDetails = new StringRequest(Request.Method.POST, Constants.updateProfile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    loadingDialog.dismiss();

                    try {
                        JSONObject responsefromserver = new JSONObject(response);
                        String error = responsefromserver.getString("Error");
                        String message = responsefromserver.getString("Message");
                        showUpdateProfile(error, message);

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
                        Toast.makeText(UpdateProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(UpdateProfile.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(UpdateProfile.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(UpdateProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(UpdateProfile.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(UpdateProfile.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(UpdateProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                    params.put("name", nameEditText.getText().toString().trim());
                    params.put("email", emailEditText.getText().toString().trim());
                    params.put("phone_no", phoneEditText.getText().toString().trim());
                    params.put("dob", edt_dob_date);
                    params.put("pan_no", panEditText.getText().toString().trim());
                    params.put("aadhar_no", aadhaarEditText.getText().toString().trim());
                    params.put("anniversary_date", edt_anndob_date);

                    return params;
                }
            };

            saveAdminDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            singleton.getInstance(getApplicationContext()).addToRequestQueue(saveAdminDetails);

        } else {
            Snackbar.make(profileRootLayout, "No Changes Found", Snackbar.LENGTH_SHORT).show();
        }
    }

    //check previous data of User
    public boolean checkPreviousData() {

        String name, email, phone, dob, pan, aadhar, anniversary;
        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        phone = phoneEditText.getText().toString().trim();
        dob = dobEditText.getText().toString().trim();
        pan = panEditText.getText().toString().trim();
        aadhar = aadhaarEditText.getText().toString().trim();
        anniversary = anniversaryEditText.getText().toString().trim();

        if (name.equals(profileDetails.getName()) && (email.equals(profileDetails.getEmail())) && (phone.equals(profileDetails.getPhoneNo())) && (dob.equals(profileDetails.getDob())) &&
                (pan.equals(profileDetails.getPanNo())) && (aadhar.equals(profileDetails.getAadhaarNo())) && (anniversary.equals(profileDetails.getAnniversaryDate()))) {
            return false;
        }

        return true;

    }

    //To show admin edit result in server
    private void showUpdateProfile(final String error, String message) {

        android.app.AlertDialog.Builder resultBuilder = new android.app.AlertDialog.Builder(UpdateProfile.this);
        resultBuilder.setTitle("Sai Finance");
        resultBuilder.setMessage(message);
        resultBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (error.equals("false")) {
                    startActivity(new Intent(UpdateProfile.this, UpdateProfile.class).
                            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        resultBuilder.setCancelable(false);
        resultBuilder.show();

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
