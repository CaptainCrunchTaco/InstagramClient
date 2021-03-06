package com.example.android.instagramclient;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static android.text.format.DateUtils.getRelativeTimeSpanString;


public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "d08b6d2f8db04a8db385367f29c0d993";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;
    //refreshing differentiates between startup actions and when you refresh
    private boolean refreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        //Set up refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing = true;
                fetchPopularPhotos();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // SEND OUT API REQUEST TO POPULAR PHOTOS
        photos = new ArrayList<>();
        //Create the adapter linking it to the source
        aPhotos = new InstagramPhotosAdapter(this, photos);
        //Find the listview from the layout
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        //Set the adapter binding it to the listview
        lvPhotos.setAdapter(aPhotos);
        //Fetch the popularphotos
        fetchPopularPhotos();
    }



    //Trigger API request
    public void fetchPopularPhotos() {
//        - Popular: https://api.instagram.com/v1/media/popular?access_token=ACCESS-TOKEN
//        - Response
//                - Type: { "data" => [x] => "type" } ("image" or "video")
//        -URL: { "data" => [x] => "images" => "standard_resolution" => "url"}
//        - Caption: { "data" => [x] => "caption" => "text"}
//        - Author: { "data" => [x] => "user" => "username"}

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        //Create the network client
        AsyncHttpClient client = new AsyncHttpClient();

        //Trigger the GET request
        client.get(url, null, new JsonHttpResponseHandler() {
           //onSuccess (worked, 200)

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Expecting a JSON object
                //Type: { "data" => [x] => "type" } ("image" or "video")
                //URL: { "data" => [x] => "images" => "standard_resolution" => "url"}
                //Caption: { "data" => [x] => "caption" => "text"}
                //Author: { "data" => [x] => "user" => "username"}
                // Remember to CLEAR OUT old items before appending in the new ones
                //Otherwise, you're gonna just keep adding to the adapter!
                if(refreshing) {
                    aPhotos.clear();
                }
                //Iterate each of the photo items and decode the item into java object

                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data"); // array of posts
                    // iterate array of posts
                    for (int i = 0; i < photosJSON.length(); i++) {
                        //get the json object at that position
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        //decode the attributes of the json into a date model
                        InstagramPhoto photo = new InstagramPhoto();
                        //Author: { "data" => [x] => "user" => "username"}
                        photo.username =  photoJSON.getJSONObject("user").getString("username");
                        photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        photo.profilePicture = photoJSON.getJSONObject("user").getString("profile_picture");
                        photoJSON.getString("created_time");
                        photo.timeStamp = getRelativeTimeSpanString(Long.parseLong(photoJSON.getString("created_time")) * 1000, System.currentTimeMillis(), SECOND_IN_MILLIS);
                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                //
                if(refreshing) {
                    swipeContainer.setRefreshing(false);
                    refreshing = false;
                }
                //callback
                aPhotos.notifyDataSetChanged();
            }

            //onFailure

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //DO SOMETHING
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
