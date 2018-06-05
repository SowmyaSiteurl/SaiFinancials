package in.siteurl.www.saifinance.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 19/5/18.
 */

public class FaqDetails implements Serializable{

    String faqId, faqSubject, faqMessage, faqRating,faqDate, faqStatus, faqAdminNote, faqUserName,
    faqUserEmail,faqUserPhone;


    public FaqDetails(String faqId, String faqSubject, String faqMessage, String faqRating, String faqDate, String faqStatus, String faqAdminNote, String faqUserName, String faqUserEmail, String faqUserPhone) {
        this.faqId = faqId;
        this.faqSubject = faqSubject;
        this.faqMessage = faqMessage;
        this.faqRating = faqRating;
        this.faqDate = faqDate;
        this.faqStatus = faqStatus;
        this.faqAdminNote = faqAdminNote;
        this.faqUserName = faqUserName;
        this.faqUserEmail = faqUserEmail;
        this.faqUserPhone = faqUserPhone;
    }

    public String getFaqId() {
        return faqId;
    }

    public void setFaqId(String faqId) {
        this.faqId = faqId;
    }

    public String getFaqSubject() {
        return faqSubject;
    }

    public void setFaqSubject(String faqSubject) {
        this.faqSubject = faqSubject;
    }

    public String getFaqMessage() {
        return faqMessage;
    }

    public void setFaqMessage(String faqMessage) {
        this.faqMessage = faqMessage;
    }

    public String getFaqDate() {
        return faqDate;
    }

    public void setFaqDate(String faqDate) {
        this.faqDate = faqDate;
    }

    public String getFaqRating() {
        return faqRating;
    }

    public void setFaqRating(String faqRating) {
        this.faqRating = faqRating;
    }

    public String getFaqStatus() {
        return faqStatus;
    }

    public void setFaqStatus(String faqStatus) {
        this.faqStatus = faqStatus;
    }

    public String getFaqAdminNote() {
        return faqAdminNote;
    }

    public void setFaqAdminNote(String faqAdminNote) {
        this.faqAdminNote = faqAdminNote;
    }

    public String getFaqUserName() {
        return faqUserName;
    }

    public void setFaqUserName(String faqUserName) {
        this.faqUserName = faqUserName;
    }

    public String getFaqUserEmail() {
        return faqUserEmail;
    }

    public void setFaqUserEmail(String faqUserEmail) {
        this.faqUserEmail = faqUserEmail;
    }

    public String getFaqUserPhone() {
        return faqUserPhone;
    }

    public void setFaqUserPhone(String faqUserPhone) {
        this.faqUserPhone = faqUserPhone;
    }
}
