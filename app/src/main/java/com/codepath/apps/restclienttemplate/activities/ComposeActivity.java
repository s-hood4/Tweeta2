package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetaClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    Button sendBtn;
    EditText etCompose;
    TextView tvCharCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        sendBtn = findViewById(R.id.sendBtn);
        etCompose = findViewById(R.id.etCompose);
        tvCharCount = findViewById(R.id.tvCharCount);

        Toolbar toolbar = (Toolbar) findViewById(R.id.Composetb);
        setSupportActionBar(toolbar);
        sendBtn.setBackgroundResource(R.drawable.round_button_grayed_out);
        sendBtn.setClickable(false);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String body = etCompose.getText().toString();
                if ((body.trim().length() <= 0) && (body.length() == 0)) {
                    sendBtn.setBackgroundResource(R.drawable.round_button_grayed_out);
                    sendBtn.setClickable(false);
                    tvCharCount.setText("");
                } else if (body.length() == 0) {
                    sendBtn.setBackgroundResource(R.drawable.round_button_grayed_out);
                    sendBtn.setClickable(false);
                    tvCharCount.setText("");
                } else if (body.trim().length() <= 0) {
                    sendBtn.setBackgroundResource(R.drawable.round_button_grayed_out);
                    sendBtn.setClickable(false);
                    tvCharCount.setText(body.length() + "/280");
                } else if (body.length() >= 270) {
                    sendBtn.setBackgroundResource(R.drawable.round_button);
                    sendBtn.setClickable(true);
                    tvCharCount.setText(body.length() + "/280");
                    tvCharCount.setTextColor(Color.RED);
                } else {
                    sendBtn.setBackgroundResource(R.drawable.round_button);
                    sendBtn.setClickable(true);
                    tvCharCount.setText(body.length() + "/280");
                    tvCharCount.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void goBack(View view)
    {
        finish();
    }

    public void sendTweet(View view) {
        TweetaClient client = new TweetaClient(this);
        client.composeTweet(etCompose.getText().toString(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent data = new Intent();
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, data);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
