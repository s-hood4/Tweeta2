package com.codepath.apps.restclienttemplate;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.codepath.apps.restclienttemplate.models.Tweet;

public class TweetDataSourceFactory extends DataSource.Factory<Long, Tweet> {

    TweetaClient client;
    public MutableLiveData<TweetDataSource> postLiveData;
    String max_id;

    public TweetDataSourceFactory(TweetaClient client) {
        this.client = client;
        max_id = "999999999999999999";
    }

    public TweetDataSourceFactory(String max_id, TweetaClient client) {
        this.client = client;
        this.max_id = max_id;
    }

    @Override
    public DataSource<Long, Tweet> create() {
        TweetDataSource dataSource = new TweetDataSource(max_id, this.client);
        // Keep reference to the data source with a MutableLiveData reference
        postLiveData = new MutableLiveData<>();
        postLiveData.postValue(dataSource);
        return dataSource;
    }


}