package in.siteurl.www.saifinance.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 18/5/18.
 */

public class Tips implements Serializable {

    String tips_id,tipsTitle, tipsDesc,tipsImage,tips_status,created_at;

    public Tips(String tips_id, String tipsTitle, String tipsDesc, String tipsImage, String tips_status, String created_at) {
        this.tips_id = tips_id;
        this.tipsTitle = tipsTitle;
        this.tipsDesc = tipsDesc;
        this.tipsImage = tipsImage;
        this.tips_status = tips_status;
        this.created_at = created_at;
    }

    public String getTips_id() {
        return tips_id;
    }

    public void setTips_id(String tips_id) {
        this.tips_id = tips_id;
    }

    public String getTipsTitle() {
        return tipsTitle;
    }

    public void setTipsTitle(String tipsTitle) {
        this.tipsTitle = tipsTitle;
    }

    public String getTipsDesc() {
        return tipsDesc;
    }

    public void setTipsDesc(String tipsDesc) {
        this.tipsDesc = tipsDesc;
    }

    public String getTipsImage() {
        return tipsImage;
    }

    public void setTipsImage(String tipsImage) {
        this.tipsImage = tipsImage;
    }

    public String getTips_status() {
        return tips_status;
    }

    public void setTips_status(String tips_status) {
        this.tips_status = tips_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
