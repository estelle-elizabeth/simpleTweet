package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    EditText composeTweet;
    Button tweetButton;
    TwitterClient twitterClient;
    TextView tweetLength;

    public final String TAG = "ComposeActivity: ";

    public static final int MAX_TWEET_LENGTH = 140;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        composeTweet = findViewById(R.id.composeTweet);
        tweetButton = findViewById(R.id.tweetButton);
        tweetLength = findViewById(R.id.tweetLength);
        twitterClient = TwitterApplication.getRestClient(this);

        composeTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tweetLength.setText(String.valueOf(charSequence.length()));

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                tweetLength.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > MAX_TWEET_LENGTH){
                    tweetLength.setTextColor(Color.RED);
                    tweetButton.setEnabled(false);
                }

                else{
                    tweetLength.setTextColor(Color.BLACK);
                    tweetButton.setEnabled(true);
                }
//                tweetLength.setText(String.valueOf(editable.length()));
            }
        });

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = composeTweet.getText().toString();

                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Invalid input: tweet is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet is too long", Toast.LENGTH_SHORT).show();

                    return;
                }

                twitterClient.publishTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        try{
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            intent.putExtra("code", 20);
                            setResult(RESULT_OK, intent); // set result code and bundle data for response
                            finish();
                        }

                        catch(JSONException e){
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "publish tweet failed.", throwable);
                    }
                }, tweetContent);
            }
        });
    }




}
