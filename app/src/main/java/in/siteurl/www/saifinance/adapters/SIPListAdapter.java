package in.siteurl.www.saifinance.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.objects.SipList;
import in.siteurl.www.saifinance.objects.insuranceList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 14/5/18.
 */

public class SIPListAdapter extends RecyclerView.Adapter<SIPListAdapter.ViewHolder> {

    private Context mcontext;
    private ArrayList<SipList> listOfSIP = new ArrayList<>();


    public SIPListAdapter(Context context, ArrayList<SipList> countryList) {

        this.mcontext = context;
        this.listOfSIP = countryList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sip_details_adapter, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(SIPListAdapter.ViewHolder viewHolder, int position) {

        //  viewHolder.date.setText(listOfSIP.get(position).getDate());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date datestart = null;
        String newclldatestart = null;
        try {
            datestart = inputFormat.parse(listOfSIP.get(position).getCreated_at());
            newclldatestart = outputFormat.format(datestart);
            viewHolder.date.setText(": "+newclldatestart);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputPattern1 = "yyyy-MM-dd";
        String outputPattern1 = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat1 = new SimpleDateFormat(inputPattern1);
        SimpleDateFormat outputFormat1 = new SimpleDateFormat(outputPattern1);
        Date datestart1 = null;
        String newclldatestart1 = null;
        try {
            datestart = inputFormat1.parse(listOfSIP.get(position).getTo_date());
            newclldatestart = outputFormat1.format(datestart);
            viewHolder.toDate.setText(": "+newclldatestart);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputPattern2 = "yyyy-MM-dd";
        String outputPattern2 = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat2 = new SimpleDateFormat(inputPattern2);
        SimpleDateFormat outputFormat2 = new SimpleDateFormat(outputPattern2);
        Date datestart2 = null;
        String newclldatestart2 = null;
        try {
            datestart = inputFormat2.parse(listOfSIP.get(position).getFrom_date());
            newclldatestart = outputFormat2.format(datestart);
            viewHolder.fromDate.setText(": "+newclldatestart);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.bankName.setText(": "+listOfSIP.get(position).getBankName());
        viewHolder.accNo.setText(": "+listOfSIP.get(position).getAc_no());
        viewHolder.amount.setText(": "+listOfSIP.get(position).getAmount());
        viewHolder.fundName.setText(": "+listOfSIP.get(position).getFund_name());
        viewHolder.folioNo.setText(": "+listOfSIP.get(position).getFolio_no());
        viewHolder.frequency.setText(": "+listOfSIP.get(position).getFrequency());
        viewHolder.sip.setText(": "+listOfSIP.get(position).getSip());
        viewHolder.status.setText(": "+listOfSIP.get(position).getSip_status());

    }

    @Override
    public int getItemCount() {
        return (null != listOfSIP ? listOfSIP.size() : 0);
    }

    //UI elements to get ID
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView bankName;
        TextView accNo;
        TextView amount;
        TextView fundName;
        TextView folioNo;
        TextView frequency;
        TextView sip;
        TextView fromDate;
        TextView toDate;
        TextView status;

        public ViewHolder(View view) {
            super(view);

            date = view.findViewById(R.id.sipDate);
            bankName = view.findViewById(R.id.sipbankName);
            accNo = view.findViewById(R.id.sipAccNo);
            amount = view.findViewById(R.id.sipAmount);
            fundName = view.findViewById(R.id.sipFundName);
            folioNo = view.findViewById(R.id.sipFolioNo);
            frequency = view.findViewById(R.id.sipFrequency);
            sip = view.findViewById(R.id.sip);
            fromDate = view.findViewById(R.id.sipFromDate);
            toDate = view.findViewById(R.id.sipTodate);
            status = view.findViewById(R.id.sipStatus);

        }
    }
}


