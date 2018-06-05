package in.siteurl.www.saifinance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.activities.InsuranceList;
import in.siteurl.www.saifinance.objects.insuranceList;

/**
 * Created by siteurl on 19/5/18.
 */

public class UpcomingRenewalsAdapter extends RecyclerView.Adapter<UpcomingRenewalsAdapter.ViewHolder> {

    private Context mcontext;
    private ArrayList<insuranceList> listOfInsurance = new ArrayList<>();

    public UpcomingRenewalsAdapter(Context context, ArrayList<insuranceList> countryList) {

        this.mcontext = context;
        this.listOfInsurance = countryList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upcoming_renewals, viewGroup, false);
        return new ViewHolder(view);
    }

    //UI elements to get ID
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView companyName;

        public ViewHolder(View view) {
            super(view);

            date = view.findViewById(R.id.renewalDate);
            companyName = view.findViewById(R.id.renewal_company);
        }
    }

    @Override
    public void onBindViewHolder(final UpcomingRenewalsAdapter.ViewHolder viewHolder, final int position) {

        //  viewHolder.date.setText(listOfSIP.get(position).getDate());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date datestart = null;
        String newclldatestart = null;
        try {
            datestart = inputFormat.parse(listOfInsurance.get(position).getTo_date());
            newclldatestart = outputFormat.format(datestart);
            viewHolder.date.setText("Expiry Date : " + newclldatestart);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.companyName.setText("Company Name : " + listOfInsurance.get(position).getCompany());


    }

    @Override
    public int getItemCount() {
        return (null != listOfInsurance ? listOfInsurance.size() : 0);
    }


}


