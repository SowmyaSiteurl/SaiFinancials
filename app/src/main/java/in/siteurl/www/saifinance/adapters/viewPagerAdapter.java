package in.siteurl.www.saifinance.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.activities.ResizableCustomView;
import in.siteurl.www.saifinance.objects.Tips;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 18/5/18.
 */

public class viewPagerAdapter extends PagerAdapter {

    private ArrayList<Tips> tipsList;
    Context mcontext;
    private LayoutInflater inflater;

    //sharedPreferences used to save data
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uId;

    private static final int MAX_LINES = 2;

    public viewPagerAdapter(Context context, ArrayList<Tips> tipsList) {

        this.mcontext = context;
        this.tipsList = tipsList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tipsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        loginPref = mcontext.getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uId = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        View itemView = inflater.inflate(R.layout.view_pager, container, false);

        TextView title = (TextView) itemView.findViewById(R.id.tips_title);
        ImageView image = (ImageView) itemView.findViewById(R.id.tips_image);
        final TextView content = (TextView) itemView.findViewById(R.id.tips_content);

        title.setText(tipsList.get(position).getTipsTitle());
        content.setText(tipsList.get(position).getTipsDesc());

        ResizableCustomView.makeTextViewResizable(content, MAX_LINES, "View More", true);

        //It is a image loading framework
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.sai_header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();


        Glide.with(mcontext).load(tipsList.get(position).getTipsImage())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(image);

        // container.addView(itemView);
        ((ViewPager) container).addView(itemView);


        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((View) object);
        // container.removeView((LinearLayout) object);
    }
}
