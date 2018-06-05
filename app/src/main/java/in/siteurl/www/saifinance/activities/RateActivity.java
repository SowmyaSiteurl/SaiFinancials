package in.siteurl.www.saifinance.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
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
import in.siteurl.www.saifinance.adapters.tListOfFaqAdapter;
import in.siteurl.www.saifinance.apis.Constants;
import in.siteurl.www.saifinance.objects.FaqDetails;
import in.siteurl.www.saifinance.objects.insuranceList;

public class RateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    Dialog alertDialog;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    FaqDetails faqDetails;
    ArrayList<FaqDetails> detailsOfFaq = new ArrayList<>();
    tListOfFaqAdapter tListOfFaqAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //toolbar
        mToolbar = findViewById(R.id.rate_toolbar);
        mToolbar.setTitle("Rate Us");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing views
        recyclerView = findViewById(R.id.faqLists);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        relativeLayout = findViewById(R.id.faqLayout);

        toGetListOfFaq();

    }


    //this is the method to get ListOfFaq
    private void toGetListOfFaq() {

        final AlertDialog loadingDialog = new SpotsDialog(RateActivity.this, R.style.Custom);
        loadingDialog.show();

        final StringRequest faq_Details = new StringRequest(Request.Method.POST, Constants.listoffaq, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();

                try {
                    JSONObject faqObject = new JSONObject(response);
                    String error = faqObject.getString("Error");
                    String message = faqObject.getString("Message");

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

                        Toast.makeText(RateActivity.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RateActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }

                    if (error.equals("false")) {
                        JSONArray faqArray = faqObject.getJSONArray("Details");
                        for (int i = 0; i < faqArray.length(); i++) {
                            JSONObject faq = faqArray.getJSONObject(i);

                            String faq_id = faq.getString("faq_id");
                            String subject = faq.getString("subject");
                            String faq_message = faq.getString("message");
                            String rattings = faq.getString("rattings");
                            String faq_date = faq.getString("created_at");
                            String faq_status = faq.getString("faq_status");
                            String admin_note = faq.getString("admin_note");

                            JSONObject userDetails = faq.getJSONObject("user_detalis");
                            String userName = userDetails.getString("name");
                            String userEmail = userDetails.getString("email");
                            String userPhone = userDetails.getString("phone_no");

                            faqDetails = new FaqDetails(faq_id, subject, faq_message, rattings, faq_date,faq_status, admin_note, userName, userEmail, userPhone);
                            detailsOfFaq.add(faqDetails);

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tListOfFaqAdapter = new tListOfFaqAdapter(RateActivity.this, detailsOfFaq);
                recyclerView.setAdapter(tListOfFaqAdapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                }
                if (error instanceof ServerError) {
                    Toast.makeText(RateActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(RateActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(RateActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(RateActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(RateActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(RateActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(RateActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

        faq_Details.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(faq_Details);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(RateActivity.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(RateActivity.this);
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

}
