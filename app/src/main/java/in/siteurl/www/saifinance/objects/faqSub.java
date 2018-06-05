package in.siteurl.www.saifinance.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 16/5/18.
 */

public class faqSub implements Serializable {

    String faqSubId,fafSub, faqDate, faqStatus;

    public faqSub(String faqSubId, String fafSub, String faqDate, String faqStatus) {
        this.faqSubId = faqSubId;
        this.fafSub = fafSub;
        this.faqDate = faqDate;
        this.faqStatus = faqStatus;
    }

    public String getFaqSubId() {
        return faqSubId;
    }

    public void setFaqSubId(String faqSubId) {
        this.faqSubId = faqSubId;
    }

    public String getFafSub() {
        return fafSub;
    }

    public void setFafSub(String fafSub) {
        this.fafSub = fafSub;
    }

    public String getFaqDate() {
        return faqDate;
    }

    public void setFaqDate(String faqDate) {
        this.faqDate = faqDate;
    }

    public String getFaqStatus() {
        return faqStatus;
    }

    public void setFaqStatus(String faqStatus) {
        this.faqStatus = faqStatus;
    }
}
