package in.siteurl.www.saifinance.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import in.siteurl.www.saifinance.R;
import in.siteurl.www.saifinance.apis.Constants;
import in.siteurl.www.saifinance.objects.VideoDetails;

public class YoutubeActivity extends AppCompatActivity {

    String url, youtubeId, youtubeName, video_description;
    private ArrayList<VideoDetails> listOfVideos = new ArrayList<>();
    private VideoDetails videoDetails;
    TextView title;
    ImageView next_video, previous_video;
    int i = 0;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;

    private YouTubePlayerView youTubePlayerView;
    //  private FullScreenManager fullScreenManager = new FullScreenManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        // shared preferences to save the data
        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        listOfVideos();
        title = findViewById(R.id.textViewTitle);
        next_video = findViewById(R.id.next_video);
        previous_video = findViewById(R.id.previous_video);

        youTubePlayerView = findViewById(R.id.youtube_player_view);

        initYouTubePlayerView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        youTubePlayerView.getPlayerUIController().getMenu().dismiss();
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen();
        else
            super.onBackPressed();
    }

    private void initYouTubePlayerView() {

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {

            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {


                    @Override
                    public void onReady() {

                        youTubePlayerView.enterFullScreen();

                        title.setText(listOfVideos.get(i).getName());

                        String videoId = listOfVideos.get(i).getYoutube_id();
                        initializedYouTubePlayer.loadVideo(videoId, 1);
                        initializedYouTubePlayer.play();
                        initializedYouTubePlayer.pause();

                        next_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                i++;
                                if (i < listOfVideos.size()) {

                                    title.setText(listOfVideos.get(i).getName());
                                    previous_video.setVisibility(View.VISIBLE);

                                    String videoid = listOfVideos.get(i).getYoutube_id();
                                    initializedYouTubePlayer.loadVideo(videoid, 0);
                                    initializedYouTubePlayer.play();

                                } else {
                                    next_video.setVisibility(View.GONE);
                                }

                            }
                        });

                        previous_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                i--;
                                if (i >= 0) {

                                    title.setText(listOfVideos.get(i).getName());
                                    next_video.setVisibility(View.VISIBLE);

                                    String videoid = listOfVideos.get(i).getYoutube_id();
                                    initializedYouTubePlayer.loadVideo(videoid, 0);
                                    initializedYouTubePlayer.play();

                                } else {
                                    previous_video.setVisibility(View.GONE);
                                }
                            }
                        });

                    }

                    @Override
                    public void onStateChange(int state) {
                        super.onStateChange(state);

                        switch (state) {
                            case PlayerConstants.PlayerState.ENDED:

                                i++;
                                if (i < listOfVideos.size()) {

                                    title.setText(listOfVideos.get(i).getName());

                                    String videoid = listOfVideos.get(i).getYoutube_id();
                                    initializedYouTubePlayer.loadVideo(videoid, 0);
                                    initializedYouTubePlayer.play();
                                }

                                break;

                            case PlayerConstants.PlayerState.PLAYING: {

                                next_video.setVisibility(View.GONE);
                                previous_video.setVisibility(View.GONE);

                            }
                            break;

                            case PlayerConstants.PlayerState.PAUSED:

                                next_video.setVisibility(View.VISIBLE);
                                previous_video.setVisibility(View.VISIBLE);

                                break;

                            default:
                                break;
                        }

                    }

                });

                addFullScreenListenerToPlayer(initializedYouTubePlayer);
            }
        }, true);

    }


    private void addFullScreenListenerToPlayer(final YouTubePlayer youTubePlayer) {
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // fullScreenManager.enterFullScreen();
                youTubePlayerView.enterFullScreen();

            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //  fullScreenManager.exitFullScreen();
                // youTubePlayerView.enterFullScreen();


                Intent intent = new Intent(YoutubeActivity.this, HomePageActivity.class);
                startActivity(intent);

            }
        });
    }


    //this is the method to get youtube videos from server
    private void listOfVideos() {

        StringRequest videoList = new StringRequest(Request.Method.POST, Constants.listofvideos,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");


                            if (Error.equals("false")) {
                                JSONArray videofListArray = jsonObject.getJSONArray("List Of Videos");
                                for (int i = 0; i < videofListArray.length(); i++) {
                                    JSONObject videos = videofListArray.getJSONObject(i);

                                    String videoId = videos.getString("video_id");
                                    youtubeId = videos.getString("youtube_id");
                                    youtubeName = videos.getString("name");
                                    video_description = videos.getString("description");
                                    url = videos.getString("url");
                                    String videoStatus = videos.getString("video_status");
                                    String createdAt = videos.getString("created_at");

                                    videoDetails = new VideoDetails(videoId, youtubeId, youtubeName, video_description, url, videoStatus, createdAt);
                                    listOfVideos.add(videoDetails);

                                }

                                //  getVideoList();
                                initYouTubePlayerView();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        videoList.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        singleton.getInstance(getApplicationContext()).addToRequestQueue(videoList);

    }

}
