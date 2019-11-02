package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    RecyclerView recyclerView;
    List<Tweet> tweets;
    TweetsAdapter tweetsAdapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        client = TwitterApplication.getRestClient(this);

        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("timeline", "user is refreshing");
                populateHomeTimeline();
            }
        });

        recyclerView = findViewById(R.id.rvTweets);
        tweets = new ArrayList<Tweet>();
        tweetsAdapter = new TweetsAdapter(this, tweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(tweetsAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i("timeline", "user scrolling" + page);
                loadMoreData();
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        populateHomeTimeline();
    }

    private void loadMoreData() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("timeline", "next page of tweets " + json.toString());

                JSONArray jsonArray = json.jsonArray;

                try{
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    tweetsAdapter.addAll(tweets);
                }

                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("timeline", "failure on next page", throwable);
            }
        }, tweets.get(tweets.size() - 1).id);

    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("timeline", json.toString());
                JSONArray jsonArray = json.jsonArray;

                try{
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    tweetsAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }

                catch (JSONException e){
                    Log.e("Error", "JSONException thrown");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("timeline", "failure", throwable);
            }
        });
    }
}
