package in.siteurl.www.saifinance.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.adapters.InsuranceListAdapter;
import in.siteurl.www.saifinance.adapters.SIPListAdapter;
import in.siteurl.www.saifinance.apis.Constants;
import in.siteurl.www.saifinance.objects.SipList;
import in.siteurl.www.saifinance.objects.insuranceList;

public class InsuranceList extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    RelativeLayout InsuranceLayout;
    Dialog alertDialog;
    private ArrayList<insuranceList> listOfInsurance = new ArrayList<>();
    RecyclerView InsuranceView;
    insuranceList insuranceList;
    InsuranceListAdapter insuranceListAdapter;

    TextView noPolicy;
    Button no_policy;
    private RadioButton radioButton1, radioButton2, radioButton3;
    String radio_value;
    MaterialEditText PolicyMessage;
    Button Insurance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_list);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //toolbar
        mToolbar = findViewById(R.id.insurance_list_toolbar);
        mToolbar.setTitle("Your Policies");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing views
        InsuranceView = findViewById(R.id.insuranceLists);
        InsuranceView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        InsuranceView.setLayoutManager(mLayoutManager);

        checkInternetConnection();
        toGetInsuranceListFromServer();

        InsuranceLayout = findViewById(R.id.insuranceLayout);
        noPolicy = findViewById(R.id.noPolicy);
        no_policy = findViewById(R.id.no_policy);

        no_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewInsuranceRequest();
            }
        });
    }


    //this is the alert dialog for showing Insurance Request
    private void sendNewInsuranceRequest() {

        final android.app.AlertDialog.Builder sendInsuranceBuilder = new android.app.AlertDialog.Builder(InsuranceList.this);
        sendInsuranceBuilder.setTitle("Insurance Request");
        final View customLayout = getLayoutInflater().inflate(R.layout.send_insurance_request, null);
        PolicyMessage = customLayout.findViewById(R.id.insuranceEditText);

        radioButton1 = customLayout.findViewById(R.id.health);
        radioButton1.setOnClickListener(this);
        radioButton2 = customLayout.findViewById(R.id.life);
        radioButton2.setOnClickListener(this);
        radioButton3 = customLayout.findViewById(R.id.general);
        radioButton3.setOnClickListener(this);

        sendInsuranceBuilder.setView(customLayout);
        Insurance = customLayout.findViewById(R.id.sendInsurance);
        Insurance.setOnClickListener(this);

        sendInsuranceBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        sendInsuranceBuilder.show();
    }

    //this is the method to send new Insurance Request
    public void sendInsuranceRequest(final String insuraneMessage, final String radio_value) {

        final AlertDialog loadingDialog = new SpotsDialog(InsuranceList.this, R.style.Custom);
        loadingDialog.show();

        StringRequest insuranceRequest = new StringRequest(Request.Method.POST, Constants.InsuranceRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialog.dismiss();
                try {
                    JSONObject insuranceObject = new JSONObject(response);
                    String error = insuranceObject.getString("Error");
                    String message = insuranceObject.getString("Message");

                    if (error.equals("true")) {
                        Toast.makeText(InsuranceList.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (error.equals("false")) {
                        Toast.makeText(InsuranceList.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(InsuranceList.this, HomePageActivity.class).
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

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("message", insuraneMessage);
                params.put("insurance_type_id", radio_value);
                return params;
            }

        };
        insuranceRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(insuranceRequest);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.health:
                if (radioButton1.isChecked()) {
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                    radio_value = "1";
                }
                if (!radioButton1.isChecked()) {
                    radio_value = "";
                }

                break;

            case R.id.life:
                if (radioButton2.isChecked()) {
                    radioButton1.setChecked(false);
                    radioButton3.setChecked(false);
                    radio_value = "2";
                }
                if (!radioButton2.isChecked()) {
                    radio_value = "";
                }

                break;
            case R.id.general:
                if (radioButton3.isChecked()) {
                    radioButton2.setChecked(false);
                    radioButton1.setChecked(false);
                    radio_value = "3";
                }
                if (!radioButton3.isChecked()) {
                    radio_value = "";
                }

                break;


            case R.id.sendInsurance: {
                String sendInsurance = PolicyMessage.getText().toString().trim();
                if (TextUtils.isEmpty(sendInsurance)) {
                    Toast.makeText(InsuranceList.this, "enter your new Insurance request", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, radio_value, Toast.LENGTH_SHORT).show();
                sendInsuranceRequest(sendInsurance, radio_value);
            }
            break;
        }

    }

    //this is the method to get InsuranceList from server
    private void toGetInsuranceListFromServer() {

        final AlertDialog loadingDialog = new SpotsDialog(InsuranceList.this, R.style.Custom);
        loadingDialog.show();

        StringRequest getInsuranceDetails = new StringRequest(Request.Method.POST, Constants.listofInsuranceList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();

                try {
                    JSONObject insuranceObject = new JSONObject(response);
                    String error = insuranceObject.getString("Error");
                    String message = insuranceObject.getString("Message");

                    /*if (error.equals("true")) {

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

                        Toast.makeText(InsuranceList.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(InsuranceList.this, LoginActivity.class);
                        startActivity(intent);
                    }*/

                    if (error.equals("false")) {
                        JSONArray insuranceListArray = insuranceObject.getJSONArray("List of Insurance ");
                        for (int i = 0; i < insuranceListArray.length(); i++) {
                            JSONObject insuranceDetails = insuranceListArray.getJSONObject(i);

                            String insurance_id = insuranceDetails.getString("insurance_id");
                            String customer_id = insuranceDetails.getString("customer_id");
                            String insurance_type_id = insuranceDetails.getString("insurance_type_id");
                            String policy_no = insuranceDetails.getString("policy_no");
                            String company = insuranceDetails.getString("company");
                            String sum_assured = insuranceDetails.getString("sum_assured");
                            String premium_amount = insuranceDetails.getString("premium_amount");
                            String from_date = insuranceDetails.getString("from_date");
                            String to_date = insuranceDetails.getString("to_date");
                            String start_date = insuranceDetails.getString("start_date");
                            String remarks = insuranceDetails.getString("remarks");
                            String created_at = insuranceDetails.getString("created_at");
                            String insurance_status = insuranceDetails.getString("insurance_status");

                            insuranceList = new insuranceList(insurance_id, customer_id, insurance_type_id, policy_no, company, sum_assured, premium_amount, from_date,
                                    to_date, start_date, remarks, created_at, insurance_status);
                            listOfInsurance.add(insuranceList);

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listOfInsurance.size() > 0) {
                    insuranceListAdapter = new InsuranceListAdapter(InsuranceList.this, listOfInsurance);
                    InsuranceView.setAdapter(insuranceListAdapter);
                    no_policy.setVisibility(View.GONE);
                    noPolicy.setVisibility(View.GONE);
                } else {
                    noPolicy.setVisibility(View.VISIBLE);
                    no_policy.setVisibility(View.VISIBLE);
                    InsuranceView.setVisibility(View.GONE);
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
                    Toast.makeText(InsuranceList.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(InsuranceList.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(InsuranceList.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(InsuranceList.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(InsuranceList.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(InsuranceList.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(InsuranceList.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

        getInsuranceDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(getInsuranceDetails);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(InsuranceList.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(InsuranceList.this);
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
