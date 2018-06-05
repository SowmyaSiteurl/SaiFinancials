package in.siteurl.www.saifinance.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 15/5/18.
 */

public class insuranceList implements Serializable {

    String insurance_id;
    String customer_id;
    String insurance_type_id;
    String policy_no;
    String company;
    String sum_assured;
    String premium_amount;
    String from_date;
    String to_date;
    String start_date;
    String remarks;
    String created_at;
    String insurance_status;

    public insuranceList(String insurance_id, String customer_id, String insurance_type_id, String policy_no, String company, String sum_assured, String premium_amount, String from_date, String to_date, String start_date, String remarks, String created_at, String insurance_status) {
        this.insurance_id = insurance_id;
        this.customer_id = customer_id;
        this.insurance_type_id = insurance_type_id;
        this.policy_no = policy_no;
        this.company = company;
        this.sum_assured = sum_assured;
        this.premium_amount = premium_amount;
        this.from_date = from_date;
        this.to_date = to_date;
        this.start_date = start_date;
        this.remarks = remarks;
        this.created_at = created_at;
        this.insurance_status = insurance_status;
    }

    public String getInsurance_id() {
        return insurance_id;
    }

    public void setInsurance_id(String insurance_id) {
        this.insurance_id = insurance_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getInsurance_type_id() {
        return insurance_type_id;
    }

    public void setInsurance_type_id(String insurance_type_id) {
        this.insurance_type_id = insurance_type_id;
    }

    public String getPolicy_no() {
        return policy_no;
    }

    public void setPolicy_no(String policy_no) {
        this.policy_no = policy_no;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSum_assured() {
        return sum_assured;
    }

    public void setSum_assured(String sum_assured) {
        this.sum_assured = sum_assured;
    }

    public String getPremium_amount() {
        return premium_amount;
    }

    public void setPremium_amount(String premium_amount) {
        this.premium_amount = premium_amount;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getInsurance_status() {
        return insurance_status;
    }

    public void setInsurance_status(String insurance_status) {
        this.insurance_status = insurance_status;
    }
}
