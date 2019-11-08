package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ComposeActivity extends AppCompatActivity {
    EditText composeTweet;
    Button tweetButton;

    public static final int MAX_TWEET_LENGTH = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        composeTweet = findViewById(R.id.composeTweet);
        tweetButton = findViewById(R.id.tweetButton);

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

                Toast.makeText(ComposeActivity.this, "testing", Toast.LENGTH_LONG).show();
            }
        });
    }
}
