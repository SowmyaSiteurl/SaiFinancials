package in.siteurl.www.saifinance.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import in.siteurl.www.saifinance.R;

public class RateUsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    WebView webView;
    String read_Url = null;
    String GOOGLE_RATE, JUST_DIAL_RATE;

    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);

        alertDialog = new Dialog(this);
        webView = findViewById(R.id.web_view);

        checkInternetConnection();

        GOOGLE_RATE = getIntent().getStringExtra("rate");
        JUST_DIAL_RATE = getIntent().getStringExtra("rate");


        if (GOOGLE_RATE.equals("googleRate")) {
            String google = "https://www.google.com/search?q=Sai+Financial+services,+LIG+18,+1st+Main,+1st+Cross+Road,+E%26F+Block,+Ramakrishnanagar,+Mysuru,+Karnataka+570023&ludocid=4550966930342515671&hl=en&gl=IN&sa=X&ved=2ahUKEwiTguP30pPbAhUMro8KHaqyCcEQtaMBMAB6BAgJEAE#lkt=LocalPoiReviews&trex=m_t:lcl_akp,rc_f:nav,rc_ludocids:4550966930342515671,rc_q:Sai%2520Financial%2520services,ru_q:Sai%2520Financial%2520services";
            read_Url = google;
        }

        if (JUST_DIAL_RATE.equals("justdialRate")) {
            String justDial = "https://www.justdial.com/Mysore/Sai-Financials-Near-Pooja-Bakery-Kuvempunagar/0821PX821-X821-130530205537-X3I2_BZDET";
            read_Url = justDial;
        }

        webView.loadUrl(read_Url);
        webView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(read_Url)) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
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
