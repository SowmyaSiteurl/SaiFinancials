package in.siteurl.www.saifinance.adapters;

import android.content.Context;
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
import in.siteurl.www.saifinance.objects.FaqDetails;

/**
 * Created by siteurl on 19/5/18.
 */

public class tListOfFaqAdapter extends RecyclerView.Adapter<tListOfFaqAdapter.ViewHolder> {


        private Context mcontext;
        private ArrayList<FaqDetails> listOfFaq = new ArrayList<>();


    public tListOfFaqAdapter(Context context, ArrayList <FaqDetails> countryList) {

        this.mcontext = context;
        this.listOfFaq = countryList;

    }

        @Override
        public ViewHolder onCreateViewHolder (ViewGroup viewGroup,int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_of_faq, viewGroup, false);
        return new ViewHolder(view);
    }


        @Override
        public void onBindViewHolder (tListOfFaqAdapter.ViewHolder viewHolder,int position){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date datestart = null;
        String newclldatestart = null;
        try {
            datestart = inputFormat.parse(listOfFaq.get(position).getFaqDate());
            newclldatestart = outputFormat.format(datestart);
            viewHolder.faqDate.setText("Created At : " + newclldatestart);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        viewHolder.faqSub.setText("Subject : " + listOfFaq.get(position).getFaqSubject());
        viewHolder.faqMsg.setText("Message : " + listOfFaq.get(position).getFaqMessage());
        viewHolder.faqStatus.setText("Status : " + listOfFaq.get(position).getFaqStatus());
        viewHolder.faqUserName.setText("User Name : " + listOfFaq.get(position).getFaqUserName());
       // viewHolder.faqRate.setText("Ratings : " + listOfFaq.get(position).getFaqRating());


    }

        @Override
        public int getItemCount () {
        return (null != listOfFaq ? listOfFaq.size() : 0);
    }

        //UI elements to get ID
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView faqSub;
            TextView faqMsg;
            TextView faqDate;
          //  TextView faqRate;
            TextView faqStatus;
            TextView faqUserName;


            public ViewHolder(View view) {
                super(view);

                faqSub = view.findViewById(R.id.faqSub);
                faqMsg = view.findViewById(R.id.faqMsg);
                faqDate = view.findViewById(R.id.faqDate);
              //  faqRate = view.findViewById(R.id.fagRate);
                faqStatus = view.findViewById(R.id.faqStatus);
                faqUserName = view.findViewById(R.id.faqUserName);

            }
        }
    }



