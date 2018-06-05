package in.siteurl.www.saifinance.activities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by siteurl on 9/5/18.
 */

public class singleton {

    private static singleton mInstance;
    private static Context mContext;
    private RequestQueue requestQueue;


    private singleton(Context context) {
        mContext = context;
        requestQueue = getRequestQueue();
    }


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized singleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new singleton(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
