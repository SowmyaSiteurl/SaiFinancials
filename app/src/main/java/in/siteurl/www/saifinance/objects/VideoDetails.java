package in.siteurl.www.saifinance.objects;

/**
 * Created by siteurl on 17/5/18.
 */

public class VideoDetails {

    String video_id,youtube_id,name,description,url,video_status,created_at;

    public VideoDetails(String video_id, String youtube_id, String name, String description, String url, String video_status, String created_at) {
        this.video_id = video_id;
        this.youtube_id = youtube_id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.video_status = video_status;
        this.created_at = created_at;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideo_status() {
        return video_status;
    }

    public void setVideo_status(String video_status) {
        this.video_status = video_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
