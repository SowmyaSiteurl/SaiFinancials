package in.siteurl.www.saifinance.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.siteurl.www.saifinance.R;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String loginUserId, sessionId, uId;

    private static int SPLASH_TIME_OUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uId = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                if (loginPref.contains("loginUserId")) {
                    loginUserId = loginPref.getString("loginUserId", null);
                    if (loginUserId.equals("") || loginUserId.equals(null)) {
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreen.this, HomePageActivity.class));
                    }
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
