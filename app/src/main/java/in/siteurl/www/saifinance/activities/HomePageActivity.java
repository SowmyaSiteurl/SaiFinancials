package in.siteurl.www.saifinance.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.adapters.UpcomingRenewalsAdapter;
import in.siteurl.www.saifinance.adapters.viewPagerAdapter;
import in.siteurl.www.saifinance.apis.Constants;
import in.siteurl.www.saifinance.objects.Tips;
import in.siteurl.www.saifinance.objects.VideoDetails;
import in.siteurl.www.saifinance.objects.insuranceList;


public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    private DrawerLayout mMainDrawer;
    private ActionBarDrawerToggle mToogle;
    private NavigationView mNavigationView;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid, name, email;
    private Toolbar mToolbar;
    Dialog alertDialog;
    private ImageView mLogout;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    AlertDialog.Builder referFrndBuilder;

    private RadioButton radioButton1, radioButton2, radioButton3, RB_SPI, RB_Onetime;
    Button Insurance, SIP;
    MaterialEditText PolicyMessage, SIPMessage, SIPAmount;
    String radio_value;

    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = {"\\?vi?=([^&]*)", "watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

    String url, youtubeId, youtubeName, video_description;
    private ArrayList<VideoDetails> listOfVideos = new ArrayList<>();
    private VideoDetails videoDetails;
    TextView title, description;
    ImageView next_video, previous_video;
    int i = 0;

    ViewPager pager;
    public static int count = 0;
    ImageView previousTip, nextTip;
    private viewPagerAdapter viewPagerAdapter;
    Timer timer;
    final long DELAY_MS = 2500;
    final long PERIOD_MS = 2500;
    int currentPage = 0;
    private ArrayList<Tips> listOfTips = new ArrayList<>();
    private Tips tips_details;

    private ArrayList<insuranceList> listOfInsurance = new ArrayList<>();
    RecyclerView InsuranceView;
    insuranceList insuranceList;
    UpcomingRenewalsAdapter upcomingRenewalsAdapter;
    ImageView arrow;
    YouTubePlayerView youTubePlayerView;
    TextView noRenewals;
    RelativeLayout tips_layout, renewals_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_home_page);
        } else {
            setContentView(R.layout.activity_home_page1);
        }
        //  setContentView(R.layout.activity_home_page);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        name = loginPref.getString("loginName", null);
        email = loginPref.getString("loginEmail", null);
        editor = loginPref.edit();

        //Drawer function
        mMainDrawer = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.nv_main);
        mToogle = new ActionBarDrawerToggle(this, mMainDrawer, R.string.open, R.string.close);
        mToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.whiteColor));//change drawer icon color
        mMainDrawer.addDrawerListener(mToogle);
        mToogle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        TextView loginName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name);
        loginName.setText("Welcome " + name);
        // TextView loginEmail = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email);
        // loginEmail.setText(email);

        //toolbar function
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mLogout = mToolbar.findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMainDrawer.addDrawerListener(mToogle);
        mToogle.syncState();

        listOfVideos();

        title = findViewById(R.id.textViewTitle);
        description = findViewById(R.id.textViewDescription);
        next_video = findViewById(R.id.next_video);
        previous_video = findViewById(R.id.previous_video);
        tips_layout = findViewById(R.id.tips_of_the_day);
        renewals_layout = findViewById(R.id.upcoming_renewals);

        //upcoming renewals
        arrow = findViewById(R.id.arrow);
        noRenewals = findViewById(R.id.noRenewals);
        InsuranceView = findViewById(R.id.renewals_list);
        InsuranceView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        InsuranceView.setLayoutManager(mLayoutManager);


        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, InsuranceList.class);
                startActivity(intent);
            }
        });

        pager = (ViewPager) findViewById(R.id.tips_pager);
        previousTip = findViewById(R.id.previous_tip);
        previousTip.setOnClickListener(this);
        previousTip.setVisibility(View.GONE);
        nextTip = findViewById(R.id.next_tip);
        nextTip.setOnClickListener(this);
        getTipsList();
        toGetInsuranceListFromServer();

        checkInternetConnection();


    }

    /*@SuppressLint("ResourceType")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getApplicationContext().getResources().getBoolean(R.layout.activity_home_page1);
           // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getApplicationContext().getResources().getBoolean(R.layout.activity_home_page);
           // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }*/

    //menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemid = item.getItemId();

        if (itemid == R.id.update_profile) {
            startActivity(new Intent(HomePageActivity.this, UpdateProfile.class));
        }
        if (itemid == R.id.insurance_request) {
            sendNewInsuranceRequest();
        }
        if (itemid == R.id.insurance_list) {
            Intent intent = new Intent(HomePageActivity.this, InsuranceList.class);
            startActivity(intent);
        }
        if (itemid == R.id.sip_request) {
            sendNewSIPRequest();
        }
        if (itemid == R.id.sip_list) {
            Intent intent = new Intent(HomePageActivity.this, SIPList.class);
            startActivity(intent);
        }
        if (itemid == R.id.refer_friend) {
            referAFriend();
        }
        if (itemid == R.id.query) {
            Intent intent = new Intent(HomePageActivity.this, contactUs_Query.class);
            startActivity(intent);
        }
        if (itemid == R.id.rate_us) {

            showDialogForRateUs();
        }
        if (itemid == R.id.change_password) {
            startActivity(new Intent(HomePageActivity.this, changePasswordActivity.class));
        }
        if (itemid == R.id.logout) {
            logout();
        }
        mMainDrawer.closeDrawers();
        return false;
    }


    //this is the alert dialog for showing RateUs
    private void showDialogForRateUs() {

        android.app.AlertDialog.Builder reviewBuilder = new android.app.AlertDialog.Builder(HomePageActivity.this);

        //code for title color change
        String titleText = "Rate Us";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.signInBackground));
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        reviewBuilder.setTitle(ssBuilder);

        // reviewBuilder.setTitle("Rate Us");
        // setTitleColor(getResources().getColor(R.color.signInBackground));
        View customLayout = getLayoutInflater().inflate(R.layout.rate_dialog, null);
        ImageView google = customLayout.findViewById(R.id.google_review);
        ImageView just = customLayout.findViewById(R.id.just_dial);
        reviewBuilder.setView(customLayout);


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String url = "https://www.google.com/search?q=Sai+Financial+services,+LIG+18,+1st+Main,+1st+Cross+Road,+E%26F+Block,+Ramakrishnanagar,+Mysuru,+Karnataka+570023&ludocid=4550966930342515671&hl=en&gl=IN&sa=X&ved=2ahUKEwiTguP30pPbAhUMro8KHaqyCcEQtaMBMAB6BAgJEAE#lkt=LocalPoiReviews&trex=m_t:lcl_akp,rc_f:nav,rc_ludocids:4550966930342515671,rc_q:Sai%2520Financial%2520services,ru_q:Sai%2520Financial%2520services";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/


                Intent intent = new Intent(HomePageActivity.this, RateUsActivity.class);
                intent.putExtra("rate", "googleRate");
                startActivity(intent);
            }
        });

        just.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String url = "https://www.justdial.com/Mysore/Sai-Financials-Near-Pooja-Bakery-Kuvempunagar/0821PX821-X821-130530205537-X3I2_BZDET";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/

                Intent intent = new Intent(HomePageActivity.this, RateUsActivity.class);
                intent.putExtra("rate", "justdialRate");
                startActivity(intent);
            }
        });

        reviewBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        reviewBuilder.show();

    }

    //this is the alert dialog for showing Insurance Request
    private void sendNewInsuranceRequest() {

        final android.app.AlertDialog.Builder sendInsuranceBuilder = new android.app.AlertDialog.Builder(HomePageActivity.this);
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

        final AlertDialog loadingDialog = new SpotsDialog(HomePageActivity.this, R.style.Custom);
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
                        Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (error.equals("false")) {
                        Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomePageActivity.this, HomePageActivity.class).
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


    //this is the alert dialog for showing add SIP Request
    private void sendNewSIPRequest() {

        android.app.AlertDialog.Builder sendSIPBuilder = new android.app.AlertDialog.Builder(HomePageActivity.this);
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

        final AlertDialog loadingDialog = new SpotsDialog(HomePageActivity.this, R.style.Custom);
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
                        Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (error.equals("false")) {
                        Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomePageActivity.this, HomePageActivity.class).
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


    //this is the alert dialog for showing add Friends
    private void referAFriend() {

        referFrndBuilder = new AlertDialog.Builder(HomePageActivity.this);
        referFrndBuilder.setTitle("Refer Your Friend/Family ");
        View customLayout = getLayoutInflater().inflate(R.layout.refer_a_friend, null);
        final MaterialEditText name = customLayout.findViewById(R.id.frndNmeEditText);
        final MaterialEditText email = customLayout.findViewById(R.id.frndEmailEditText);
        final MaterialEditText phone = customLayout.findViewById(R.id.frndPhneEditText);
        final MaterialEditText message = customLayout.findViewById(R.id.frndMsgEditText);
        Button refFrnd = customLayout.findViewById(R.id.refFrnd);
        referFrndBuilder.setView(customLayout);

        refFrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frndName = name.getText().toString().trim();
                String frndEmail = email.getText().toString().trim();
                String frndPhone = phone.getText().toString().trim();
                String frndMessage = message.getText().toString().trim();

                if (TextUtils.isEmpty(frndName)) {
                    Toast.makeText(HomePageActivity.this, "Enter Valid Name.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(frndEmail) || (!frndEmail.matches(EMAIL_PATTERN))) {
                    Toast.makeText(HomePageActivity.this, "Enter Valid Email", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(frndPhone) || phone.getText().toString().trim().length() < 10) {
                    Toast.makeText(HomePageActivity.this, "Enter Valid Mobile no.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(frndMessage)) {
                    Toast.makeText(HomePageActivity.this, "Enter your message.", Toast.LENGTH_LONG).show();
                    return;
                }

                ReferAFriend(frndName, frndEmail, frndPhone, frndMessage);
            }
        });
        referFrndBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        referFrndBuilder.show();
    }


    //this is the Method to send referred friend details to server
    public void ReferAFriend(final String name, final String email, final String phone, final String message) {

        final AlertDialog loadingDialog = new SpotsDialog(HomePageActivity.this, R.style.Custom);
        loadingDialog.show();

        StringRequest referFriendRequest = new StringRequest(Request.Method.POST, Constants.referFriend, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialog.dismiss();
                try {
                    JSONObject referFrndObject = new JSONObject(response);
                    String error = referFrndObject.getString("Error");
                    String message = referFrndObject.getString("Message");

                    if (error.equals("true")) {

                        Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (error.equals("false")) {
                        Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomePageActivity.this, HomePageActivity.class).
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
                    Toast.makeText(HomePageActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(HomePageActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(HomePageActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(HomePageActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(HomePageActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(HomePageActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(HomePageActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("name", name);
                params.put("email", email);
                params.put("phone_no", phone);
                params.put("message", message);
                return params;
            }

        };
        referFriendRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(referFriendRequest);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(HomePageActivity.this);
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

    // this is method for BackButton
    @Override
    public void onBackPressed() {

        final Snackbar snackbar = Snackbar
                .make(mMainDrawer, "Do You want to Exit ?", Snackbar.LENGTH_LONG)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.exit(0);
                        finish();


                        youTubePlayerView.initialize(new YouTubePlayerInitListener() {

                            @Override
                            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {

                                    @Override
                                    public void onStateChange(int state) {
                                        super.onStateChange(state);

                                        switch (state) {
                                            case PlayerConstants.PlayerState.ENDED:

                                                break;


                                            default:
                                                break;
                                        }

                                    }

                                });

                            }
                        }, true);
                    }
                })
                .setDuration(10000);

        snackbar.setActionTextColor(getResources().getColor(R.color.signInBackground));
        snackbar.show();

        /*android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(HomePageActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Sai Finance");
        builder.setMessage(" Do You want to Exit ?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                System.exit(0);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();*/

        //super.onBackPressed();
    }

    //this is the method for Logout function
    private void logout() {

        final AlertDialog loadingDialog = new SpotsDialog(HomePageActivity.this, R.style.Custom);
        loadingDialog.show();

        StringRequest logout = new StringRequest(Request.Method.POST, Constants.logout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");


                            if (Error.equals("false")) {

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

                                Toast.makeText(HomePageActivity.this, Message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(HomePageActivity.this, LoginActivity.class).
                                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }

                            if (Error.equals("true")) {

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

                                Toast.makeText(HomePageActivity.this, Message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                                startActivity(intent);

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
        logout.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(logout);

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

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(new ConnectivityReceiver(),
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        registerReceiver(new ConnectivityReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));*/

        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
    }


    //this is the method to get youtube videos from server
    private void listOfVideos() {

        StringRequest videoList = new StringRequest(Request.Method.POST, Constants.listofvideos,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");


                            if (Error.equals("false")) {
                                JSONArray videofListArray = jsonObject.getJSONArray("List Of Videos");
                                for (int i = 0; i < videofListArray.length(); i++) {
                                    JSONObject videos = videofListArray.getJSONObject(i);

                                    String videoId = videos.getString("video_id");
                                    youtubeId = videos.getString("youtube_id");
                                    youtubeName = videos.getString("name");
                                    video_description = videos.getString("description");
                                    url = videos.getString("url");
                                    String videoStatus = videos.getString("video_status");
                                    String createdAt = videos.getString("created_at");

                                    videoDetails = new VideoDetails(videoId, youtubeId, youtubeName, video_description, url, videoStatus, createdAt);
                                    listOfVideos.add(videoDetails);

                                }

                                getVideoList();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        videoList.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(videoList);

    }

    private void getVideoList() {

        youTubePlayerView = findViewById(R.id.youtube_player_view);


        youTubePlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_video.setVisibility(View.VISIBLE);
                previous_video.setVisibility(View.VISIBLE);
            }
        });

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {

            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {

                    @Override
                    public void onReady() {

                        // String videoId = extractVideoIdFromUrl(youtubeId);
                        title.setText(listOfVideos.get(i).getName());
                        description.setText(listOfVideos.get(i).getDescription());

                        //  previous_video.setVisibility(View.GONE);
                        String videoId = listOfVideos.get(i).getYoutube_id();
                        initializedYouTubePlayer.loadVideo(videoId, 1);
                        initializedYouTubePlayer.play();
                        initializedYouTubePlayer.pause();

                        next_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                i++;
                                if (i < listOfVideos.size()) {

                                    title.setText(listOfVideos.get(i).getName());
                                    description.setText(listOfVideos.get(i).getDescription());
                                    previous_video.setVisibility(View.VISIBLE);

                                    String videoid = listOfVideos.get(i).getYoutube_id();
                                    initializedYouTubePlayer.loadVideo(videoid, 0);
                                    initializedYouTubePlayer.play();

                                } else {
                                    next_video.setVisibility(View.GONE);
                                }

                            }
                        });

                        previous_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                i--;
                                if (i >= 0) {

                                    title.setText(listOfVideos.get(i).getName());
                                    description.setText(listOfVideos.get(i).getDescription());
                                    next_video.setVisibility(View.VISIBLE);

                                    String videoid = listOfVideos.get(i).getYoutube_id();
                                    initializedYouTubePlayer.loadVideo(videoid, 0);
                                    initializedYouTubePlayer.play();

                                } else {
                                    previous_video.setVisibility(View.GONE);
                                }
                            }
                        });

                    }

                    @Override
                    public void onStateChange(int state) {
                        super.onStateChange(state);

                        switch (state) {
                            case PlayerConstants.PlayerState.ENDED:

                                i++;
                                if (i < listOfVideos.size()) {

                                    title.setText(listOfVideos.get(i).getName());
                                    description.setText(listOfVideos.get(i).getDescription());

                                    String videoid = listOfVideos.get(i).getYoutube_id();
                                    initializedYouTubePlayer.loadVideo(videoid, 0);
                                    initializedYouTubePlayer.play();
                                }

                                break;

                            case PlayerConstants.PlayerState.PLAYING: {

                                next_video.setVisibility(View.GONE);
                                previous_video.setVisibility(View.GONE);

                            }
                            break;

                            case PlayerConstants.PlayerState.PAUSED:

                                next_video.setVisibility(View.VISIBLE);
                                previous_video.setVisibility(View.VISIBLE);

                                break;

                            default:
                                break;
                        }

                    }

                });

                addFullScreenListenerToPlayer(initializedYouTubePlayer);
            }
        }, true);

    }

    private void addFullScreenListenerToPlayer(final YouTubePlayer youTubePlayer) {
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {

                //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // youTubePlayerView.enterFullScreen();

                Intent intent = new Intent(HomePageActivity.this, YoutubeActivity.class);
                startActivity(intent);

            }

            @Override
            public void onYouTubePlayerExitFullScreen() {

                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                // youTubePlayerView.exitFullScreen();

                Intent intent = new Intent(HomePageActivity.this, YoutubeActivity.class);
                startActivity(intent);

            }
        });
    }


    //this is the function to get youtube Id from URl
    public String extractVideoIdFromUrl(String url) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

        for (String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return url.replace(matcher.group(), "");
        }
        return url;
    }


    //this is the method to get Tips List from the server
    private void getTipsList() {

        StringRequest tipsList = new StringRequest(Request.Method.POST, Constants.listOfTips,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");


                            if (Error.equals("false")) {
                                JSONArray tipsArray = jsonObject.getJSONArray("List Of Tips");
                                for (int i = 0; i < tipsArray.length(); i++) {
                                    JSONObject tips = tipsArray.getJSONObject(i);

                                    String tips_id = tips.getString("tips_id");
                                    String title = tips.getString("title");
                                    String content = tips.getString("content");
                                    String tips_img = tips.getString("tips_img");
                                    String tips_status = tips.getString("tips_status");
                                    String created_at = tips.getString("created_at");

                                    tips_details = new Tips(tips_id, title, content, tips_img, tips_status, created_at);
                                    listOfTips.add(tips_details);

                                }

                                viewPagerAdapter = new viewPagerAdapter(HomePageActivity.this, listOfTips);
                                pager.setAdapter(viewPagerAdapter);

                                //view pager
                                /*final Handler handler = new Handler();
                                final Runnable Update = new Runnable() {
                                    public void run() {
                                        if (currentPage == listOfTips.size()) {
                                            currentPage = 0;
                                        }
                                        pager.setCurrentItem(currentPage++, true);
                                    }
                                };

                                timer = new Timer(); // This will create a new Thread
                                timer.schedule(new TimerTask() { // task to be scheduled

                                    @Override
                                    public void run() {
                                        handler.post(Update);
                                    }
                                }, DELAY_MS, PERIOD_MS);*/
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        tipsList.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(tipsList);

    }


    //this is the method to get InsuranceList from server
    private void toGetInsuranceListFromServer() {

        /*final AlertDialog loadingDialog = new SpotsDialog(HomePageActivity.this, R.style.Custom);
        loadingDialog.show();*/

        StringRequest getInsuranceDetails = new StringRequest(Request.Method.POST, Constants.listofInsuranceList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // loadingDialog.dismiss();

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

                        Toast.makeText(HomePageActivity.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
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


                            try {

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formatDate = df.format(c.getTime());

                                Date newDateStr = df.parse(formatDate);
                                Date userEnteredDate = df.parse(to_date);

                                long NoOfDays = userEnteredDate.getTime() - newDateStr.getTime();
                                NoOfDays = TimeUnit.DAYS.convert(NoOfDays, TimeUnit.MILLISECONDS);

                                if (NoOfDays <= 15) {
                                    insuranceList = new insuranceList(insurance_id, customer_id, insurance_type_id, policy_no, company, sum_assured, premium_amount, from_date,
                                            to_date, start_date, remarks, created_at, insurance_status);
                                    listOfInsurance.add(insuranceList);
                                }


                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listOfInsurance.size() > 0) {
                    upcomingRenewalsAdapter = new UpcomingRenewalsAdapter(HomePageActivity.this, listOfInsurance);
                    InsuranceView.setAdapter(upcomingRenewalsAdapter);
                } else {
                    noRenewals.setVisibility(View.VISIBLE);
                    InsuranceView.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  loadingDialog.dismiss();

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
                    Toast.makeText(HomePageActivity.this, "enter your new Insurance request", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, radio_value, Toast.LENGTH_SHORT).show();
                sendInsuranceRequest(sendInsurance, radio_value);
            }
            break;

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
                    Toast.makeText(HomePageActivity.this, "enter your Amount", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(sendSIP)) {
                    Toast.makeText(HomePageActivity.this, "enter your message", Toast.LENGTH_LONG).show();
                    return;
                }

                sendSipRequest(sendSIP, sipAmount, radio_value);

            }
            break;

            case R.id.next_tip:

                if (count < listOfTips.size()) {
                    count++;
                    previousTip.setVisibility(View.VISIBLE);
                } else {
                    count = 0;
                    nextTip.setVisibility(View.GONE);

                }
                pager.setCurrentItem(count, true);
                break;

            case R.id.previous_tip:

                if (count > 0) {
                    count--;
                    previousTip.setVisibility(View.VISIBLE);
                } else {
                    count = 0;
                    previousTip.setVisibility(View.GONE);
                }
                pager.setCurrentItem(count, true);
                break;

        }

    }
}
