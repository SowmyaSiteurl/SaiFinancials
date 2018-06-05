package in.siteurl.www.saifinance.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.apis.Constants;
import in.siteurl.www.saifinance.objects.faqSub;

public class contactUs_Query extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    private Toolbar mToolbar;
    Dialog alertDialog;

    Spinner spinner;
    Button addFaq;
    private MaterialEditText message;
    String faqMessage;
    private faqSub mfaqSub;
    private ArrayList<faqSub> faqSubList = new ArrayList<>();
    String faqSub;
    RelativeLayout faqLayout;
    ArrayList<String> stringArrayList = new ArrayList<>();
    String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us__query);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //toolbar
        mToolbar = findViewById(R.id.query_toolbar);
        mToolbar.setTitle("Contact Us");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //spinner function
        spinner = (Spinner) findViewById(R.id.spinner);
        faqLayout = findViewById(R.id.faqLayout);
        addFaq = (Button) findViewById(R.id.sendFaq);
        message = (MaterialEditText) findViewById(R.id.faq);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {

                label = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        checkInternetConnection();
        getFAQSubject();
    }



    public void sendFaq(View view) {

        faqMessage = message.getText().toString().trim();

        if (TextUtils.isEmpty(faqMessage)) {
            message.setError("Please enter your query");
            Snackbar.make(faqLayout, "Please enter your query", Snackbar.LENGTH_SHORT).show();
            return;
        }

        sendFAQtoServer();
    }

    private void sendFAQtoServer() {

        final AlertDialog loadingDialog = new SpotsDialog(contactUs_Query.this, R.style.Custom);
        loadingDialog.show();

        StringRequest faqSubject = new StringRequest(Request.Method.POST, Constants.addFAQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString("Error");
                            String message = jsonObject.getString("Message");

                            if (error.equals("true")) {
                                Toast.makeText(contactUs_Query.this, message, Toast.LENGTH_SHORT).show();
                                return;
                            } else if (error.equals("false")) {
                                Toast.makeText(contactUs_Query.this, message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(contactUs_Query.this, HomePageActivity.class).
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
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("subject", label);
                params.put("message", faqMessage);
                return params;
            }
        };
        faqSubject.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(faqSubject);


    }

    //this is the method to get FAQ Subject
    private void getFAQSubject() {

        final AlertDialog loadingDialog = new SpotsDialog(contactUs_Query.this, R.style.Custom);
        loadingDialog.show();

        StringRequest faqSubject = new StringRequest(Request.Method.POST, Constants.faqSubject,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");


                            if (Error.equals("true")) {

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

                                Toast.makeText(contactUs_Query.this, Message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(contactUs_Query.this, LoginActivity.class);
                                startActivity(intent);

                            }

                            if (Error.equals("false")) {
                                JSONArray faqArray = jsonObject.getJSONArray("Details");
                                for (int i = 0; i < faqArray.length(); i++) {
                                    JSONObject faqDetails = faqArray.getJSONObject(i);

                                    String faqSubId = faqDetails.getString("faq_subject_id");
                                    faqSub = faqDetails.getString("subject");
                                    String faqDate = faqDetails.getString("created_at");
                                    String faqStatus = faqDetails.getString("faq_sub_status");
                                    stringArrayList.add(faqSub);

                                    mfaqSub = new faqSub(faqSubId, faqSub, faqDate, faqStatus);
                                    faqSubList.add(mfaqSub);

                                    // Initializing an ArrayAdapter
                                    final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                            contactUs_Query.this, R.layout.spinner_item, stringArrayList);

                                    //spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                                    spinner.setAdapter(spinnerArrayAdapter);

                                }
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
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        faqSubject.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(faqSubject);

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
