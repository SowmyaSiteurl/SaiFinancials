package in.siteurl.www.saifinance.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.activities.HomePageActivity;
import in.siteurl.www.saifinance.objects.SipList;
import in.siteurl.www.saifinance.objects.insuranceList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 15/5/18.
 */

public class InsuranceListAdapter extends RecyclerView.Adapter<InsuranceListAdapter.ViewHolder> {

    private Context mcontext;
    private ArrayList<insuranceList> listOfInsurance = new ArrayList<>();

    public InsuranceListAdapter(Context context, ArrayList<insuranceList> countryList) {

        this.mcontext = context;
        this.listOfInsurance = countryList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.insurance_details_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    //UI elements to get ID
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView companyName;
        TextView policyNo;
        TextView sumAssured;
        TextView premiumAmount;
        TextView fromDate;
        TextView toDate;
        TextView remarks;
        TextView status;


        public ViewHolder(View view) {
            super(view);

            date = view.findViewById(R.id.insuranceDate);
            companyName = view.findViewById(R.id.insuranceCompany);
            policyNo = view.findViewById(R.id.insurancePolicyNo);
            sumAssured = view.findViewById(R.id.insurancTotalAmt);
            premiumAmount = view.findViewById(R.id.insurancPremiumAmt);
            fromDate = view.findViewById(R.id.insurancFrmDate);
            toDate = view.findViewById(R.id.insurancToDate);
            remarks = view.findViewById(R.id.insurancRemarks);
          // status = view.findViewById(R.id.insurancStatus);
        }
    }

    @Override
    public void onBindViewHolder(final InsuranceListAdapter.ViewHolder viewHolder, final int position) {

        //  viewHolder.date.setText(listOfSIP.get(position).getDate());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date datestart = null;
        String newclldatestart = null;
        try {
            datestart = inputFormat.parse(listOfInsurance.get(position).getCreated_at());
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
            datestart = inputFormat1.parse(listOfInsurance.get(position).getFrom_date());
            newclldatestart = outputFormat1.format(datestart);
            viewHolder.fromDate.setText(": "+newclldatestart);

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
            datestart = inputFormat2.parse(listOfInsurance.get(position).getTo_date());
            newclldatestart = outputFormat2.format(datestart);
            viewHolder.toDate.setText(": "+newclldatestart);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.companyName.setText(": "+listOfInsurance.get(position).getCompany());
        viewHolder.policyNo.setText(": "+listOfInsurance.get(position).getPolicy_no());
        viewHolder.sumAssured.setText(": "+listOfInsurance.get(position).getSum_assured());
        viewHolder.premiumAmount.setText(": "+listOfInsurance.get(position).getPremium_amount());
        viewHolder.remarks.setText(": "+listOfInsurance.get(position).getRemarks());
       // viewHolder.status.setText(": "+listOfInsurance.get(position).getInsurance_status());

    }

    @Override
    public int getItemCount() {
        return (null != listOfInsurance ? listOfInsurance.size() : 0);
    }

}


