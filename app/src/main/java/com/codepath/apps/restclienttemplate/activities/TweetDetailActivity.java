package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetaApp;
import com.codepath.apps.restclienttemplate.TweetaClient;

public class TweetDetailActivity extends AppCompatActivity {

    private TweetaClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        TextView tvBody = findViewById(R.id.tvBody);
        tvBody.setText(getIntent().getStringExtra("body"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        client = TweetaApp.getRestClient(this);
    }

    public void logout(View view)
    {
        client.clearAccessToken();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void compose(View view)
    {
        Toast.makeText(this,"To be implemented", Toast.LENGTH_SHORT).show();
    }

}
