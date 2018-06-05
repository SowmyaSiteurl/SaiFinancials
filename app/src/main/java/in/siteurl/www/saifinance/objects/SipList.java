package in.siteurl.www.saifinance.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 14/5/18.
 */

public class SipList implements Serializable {

    String sip_id;
    String folio_no;
    String fund_name;
    String sip;
    String customer_id;
    String amount;
    String frequency;
    String date;
    String from_date;
    String to_date;
    String bankName;
    String ac_no;
    String sip_status;
    String created_at;

    public SipList(String sip_id, String folio_no, String fund_name, String sip, String customer_id, String amount, String frequency, String date, String from_date, String to_date, String bankName, String ac_no, String sip_status, String created_at) {
        this.sip_id = sip_id;
        this.folio_no = folio_no;
        this.fund_name = fund_name;
        this.sip = sip;
        this.customer_id = customer_id;
        this.amount = amount;
        this.frequency = frequency;
        this.date = date;
        this.from_date = from_date;
        this.to_date = to_date;
        this.bankName = bankName;
        this.ac_no = ac_no;
        this.sip_status = sip_status;
        this.created_at = created_at;
    }

    public String getSip_id() {
        return sip_id;
    }

    public void setSip_id(String sip_id) {
        this.sip_id = sip_id;
    }

    public String getFolio_no() {
        return folio_no;
    }

    public void setFolio_no(String folio_no) {
        this.folio_no = folio_no;
    }

    public String getFund_name() {
        return fund_name;
    }

    public void setFund_name(String fund_name) {
        this.fund_name = fund_name;
    }

    public String getSip() {
        return sip;
    }

    public void setSip(String sip) {
        this.sip = sip;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAc_no() {
        return ac_no;
    }

    public void setAc_no(String ac_no) {
        this.ac_no = ac_no;
    }

    public String getSip_status() {
        return sip_status;
    }

    public void setSip_status(String sip_status) {
        this.sip_status = sip_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}