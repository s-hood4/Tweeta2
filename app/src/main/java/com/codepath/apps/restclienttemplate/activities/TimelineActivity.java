package com.codepath.apps.restclienttemplate.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetDataSourceFactory;
import com.codepath.apps.restclienttemplate.TweetaApp;
import com.codepath.apps.restclienttemplate.TweetaClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

public class TimelineActivity extends AppCompatActivity {

    private TweetaClient client;
    RecyclerView rvTweets;
    private TweetsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    TweetDataSourceFactory factory;
    LiveData<PagedList<Tweet>> tweets;
    public static String max_id;
    public static Boolean isAtBottom;

    private final int REQUEST_CODE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);
        factory = new TweetDataSourceFactory(TweetaApp.getRestClient(this));
        adapter = new TweetsAdapter();

        client = TweetaApp.getRestClient(this);

        rvTweets = findViewById(R.id.rvTweets);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);


        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(mLayoutManager);
        rvTweets.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                mLayoutManager.getOrientation());
        rvTweets.addItemDecoration(mDividerItemDecoration);

        PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();
        final TweetDataSourceFactory factory = new TweetDataSourceFactory(TweetaApp.getRestClient(this));
        tweets = new LivePagedListBuilder(factory, config).build();
        tweets.observe(this, new Observer<PagedList<Tweet>>() {
            @Override
            public void onChanged(@Nullable PagedList<Tweet> tweets) {
                adapter.submitList(tweets);
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                factory.postLiveData.getValue().invalidate();
            }
        });

       rvTweets.setOnFlingListener(new RecyclerView.OnFlingListener() {
           @Override
           public boolean onFling(int velocityX, int velocityY) {
               if(isAtBottom==true) {
                   onHitBottom();
               }
               return false;
           }
       });

    }

    public void logout(View view)
    {
        client.clearAccessToken();
        finish();
    }

    public void compose(View view)
    {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    public void onBackPressed()
    {
        moveTaskToBack(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            // I previously implemented the required user story of manually entering tweet,
            // had to update to call LivePagedListBuilder.observe to load newly sent tweet

           swipeContainer.setRefreshing(true);
            PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();
            tweets = new LivePagedListBuilder(factory, config).build();
            tweets.observe(this, new Observer<PagedList<Tweet>>() {
                @Override
                public void onChanged(@Nullable PagedList<Tweet> tweets) {
                    adapter.submitList(tweets);
                    swipeContainer.setRefreshing(false);
                }
            });


        }
    }

    public void onHitBottom(){
        swipeContainer.setRefreshing(true);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();
        final TweetDataSourceFactory factory = new TweetDataSourceFactory(max_id, TweetaApp.getRestClient(this));
        tweets = new LivePagedListBuilder(factory, config).build();
        tweets.observe(this, new Observer<PagedList<Tweet>>() {
            @Override
            public void onChanged(@Nullable PagedList<Tweet> tweets) {
                adapter.submitList(tweets);
                swipeContainer.setRefreshing(false);
            }
        });
    }

}
