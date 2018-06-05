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
import android.support.v7.widget.DefaultItemAnimator;
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
import in.siteurl.www.saifinance.adapters.SIPListAdapter;
import in.siteurl.www.saifinance.apis.Constants;
import in.siteurl.www.saifinance.objects.SipList;

public class SIPList extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    RelativeLayout SIPLayout;
    Dialog alertDialog;
    private ArrayList<SipList> listIfSIP = new ArrayList<>();
    RecyclerView SIPView;
    SipList sipList;
    SIPListAdapter sipListAdapter;

    TextView noSip;
    Button no_sip;
    private RadioButton RB_SPI, RB_Onetime;
    Button SIP;
    MaterialEditText SIPMessage, SIPAmount;
    String radio_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siplist);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //toolbar
        mToolbar = findViewById(R.id.sip_list_toolbar);
        mToolbar.setTitle("Your Investment List");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing views
        SIPView = findViewById(R.id.sipLists);
        SIPView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        SIPView.setLayoutManager(mLayoutManager);

        SIPLayout = findViewById(R.id.SIPLayout);
        noSip = findViewById(R.id.noSip);
        no_sip = findViewById(R.id.no_sip);


        no_sip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewSIPRequest();

            }
        });

        checkInternetConnection();
        toGetSIPListFromServer();

    }


    //this is the alert dialog for showing add SIP Request
    private void sendNewSIPRequest() {

        android.app.AlertDialog.Builder sendSIPBuilder = new android.app.AlertDialog.Builder(SIPList.this);
        sendSIPBuilder.setTitle("New Investment Request");
        View customLayout = getLayoutInflater().inflate(R.layout.send_sip_request, null);
        SIPMessage = customLayout.findViewById(R.id.sipEditText);
        SIPAmount = customLayout.findViewById(R.id.sipAmountEditText);
        sendSIPBuilder.setView(customLayout);

        RB_SPI = customLayout.findViewById(R.id.sip);
        RB_SPI.setOnClickListener(this);
        RB_Onetime = customLayout.findViewById(R.id.one_time);
        RB_Onetime.setOnClickListener(this);
        SIP = customLayout.findViewById(R.id.sendSip);
        SIP.setOnClickListener(this);

        sendSIPBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        sendSIPBuilder.show();

    }

    //this is the method to send new Investment Request
    public void sendSipRequest(final String radio_value, final String amount, final String message) {

        final AlertDialog loadingDialog = new SpotsDialog(SIPList.this, R.style.Custom);
        loadingDialog.show();

        StringRequest sipRequest = new StringRequest(Request.Method.POST, Constants.SIPRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialog.dismiss();
                try {
                    JSONObject sipObject = new JSONObject(response);
                    String error = sipObject.getString("Error");
                    String message = sipObject.getString("Message");

                    if (error.equals("true")) {
                        Toast.makeText(SIPList.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (error.equals("false")) {
                        Toast.makeText(SIPList.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SIPList.this, HomePageActivity.class).
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
                params.put("message", message);
                params.put("amount", amount);
                params.put("investment_type_id", radio_value);
                return params;
            }

        };
        sipRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(sipRequest);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.one_time: {
                if (RB_Onetime.isChecked()) {
                    RB_SPI.setChecked(false);
                    radio_value = "1";
                }
                if (!RB_Onetime.isChecked()) {
                    radio_value = "";
                }

            }
            break;

            case R.id.sip: {

                if (RB_SPI.isChecked()) {
                    RB_Onetime.setChecked(false);
                    radio_value = "2";
                }
                if (!RB_SPI.isChecked()) {
                    radio_value = "";
                }

            }
            break;

            case R.id.sendSip: {

                String sendSIP = SIPMessage.getText().toString().trim();
                String sipAmount = SIPAmount.getText().toString().trim();

                if (TextUtils.isEmpty(sipAmount)) {
                    Toast.makeText(SIPList.this, "enter your Amount", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(sendSIP)) {
                    Toast.makeText(SIPList.this, "enter your message", Toast.LENGTH_LONG).show();
                    return;
                }

                sendSipRequest(sendSIP, sipAmount, radio_value);

            }
            break;
        }
    }


    //this is the method to get StaffList from server
    private void toGetSIPListFromServer() {

        final AlertDialog loadingDialog = new SpotsDialog(SIPList.this, R.style.Custom);
        loadingDialog.show();

        StringRequest getSIPDetails = new StringRequest(Request.Method.POST, Constants.listofSIP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();

                try {
                    JSONObject sipObject = new JSONObject(response);
                    String error = sipObject.getString("Error");
                    String message = sipObject.getString("Message");

                    /*if (error.equals("true")) {

                        editor.putString("loginUserId", "");
                        editor.putString("loginName", "");
                        editor.putString("loginEmail", "");
                        editor.putString("loginPhone", "");
                        editor.putString("loginDob", "");
                        editor.putString("loginPan", "");
                        editor.putString("loginAadhaar", "");
                        editor.putString("loginAnnvrsyDate", "");
                        editor.commit();
                        finishAffinity();

                        Toast.makeText(SIPList.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SIPList.this, LoginActivity.class);
                        startActivity(intent);
                    }*/

                    if (error.equals("false")) {
                        JSONArray sipfListArray = sipObject.getJSONArray("List of Investment Request");
                        for (int i = 0; i < sipfListArray.length(); i++) {
                            JSONObject sipDetails = sipfListArray.getJSONObject(i);

                            String sip_id = sipDetails.getString("sip_id");
                            String folio_no = sipDetails.getString("folio_no");
                            String fund_name = sipDetails.getString("fund_name");
                            String sip = sipDetails.getString("sip");
                            String customer_id = sipDetails.getString("customer_id");
                            String amount = sipDetails.getString("amount");
                            String frequency = sipDetails.getString("frequency");
                            String date = sipDetails.getString("date");
                            String from_date = sipDetails.getString("from_date");
                            String to_date = sipDetails.getString("to_date");
                            String bankName = sipDetails.getString("bank_name");
                            String ac_no = sipDetails.getString("ac_no");
                            String sip_status = sipDetails.getString("sip_status");
                            String created_at = sipDetails.getString("created_at");

                            sipList = new SipList(sip_id, folio_no, fund_name, sip, customer_id, amount, frequency, date, from_date, to_date, bankName, ac_no, sip_status, created_at);
                            listIfSIP.add(sipList);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (listIfSIP.size() > 0) {
                    sipListAdapter = new SIPListAdapter(SIPList.this, listIfSIP);
                    SIPView.setAdapter(sipListAdapter);
                    noSip.setVisibility(View.GONE);
                    no_sip.setVisibility(View.GONE);
                } else {
                    noSip.setVisibility(View.VISIBLE);
                    no_sip.setVisibility(View.VISIBLE);
                    SIPView.setVisibility(View.GONE);
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
                    Toast.makeText(SIPList.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(SIPList.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(SIPList.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(SIPList.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(SIPList.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(SIPList.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(SIPList.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

        getSIPDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(getSIPDetails);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(SIPList.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(SIPList.this);
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
