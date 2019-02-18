package com.codepath.apps.restclienttemplate.adapters;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetDiffCallback;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.activities.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends PagedListAdapter<Tweet, TweetsAdapter.ViewHolder> {

    private Context context;
    private List<Tweet> tweets;
    public static String maxOnLoadId;


    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tweet tweet = getItem(position);
        if (tweet == null) {
            return;
        }

        if(maxOnLoadId == tweet.id)
        {
            TimelineActivity.isAtBottom = true;
        }
        else{
            TimelineActivity.isAtBottom = false;
        }
        String createdAt = tweet.createdAt;
        String newString;
        newString = toRelativeDateString(createdAt);

        holder.tvBody.setText(tweet.body);
        holder.tvUserName.setText(tweet.user.name);
        TimelineActivity.max_id = tweet.id;
        holder.tvScreenName.setText('@' + tweet.user.screenName + " • " + newString);
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .into(holder.ivProfileImage);

        holder.tlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TweetDetailActivity.class);
                //i.putExtra("tweet",tweet);
                i.putExtra("body", tweet.body);
                context.startActivity(i);
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public TextView tvScreenName;
        public TextView tvBody;
        public RelativeLayout tlContainer;
        public TextView tvUserName;


        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenNameAndTime);
            tvBody = itemView.findViewById(R.id.tvBody);
            tlContainer = itemView.findViewById(R.id.tlContainer);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }

    private String toRelativeDateString(String rawJsonDate) {


        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tbReturned = "";
        if (relativeDate.charAt(2) == ' ') {
            // string looks like '23 min. ago'
            tbReturned = (relativeDate.substring(0, 2) + relativeDate.substring(3, 4));
        } else {
            // string looks like '2 min. ago'
            tbReturned = (relativeDate.substring(0, 1) + relativeDate.substring(2, 3));
        }

        return tbReturned;
    }


    public static final DiffUtil.ItemCallback<Tweet> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Tweet>() {
                @Override
                public boolean areItemsTheSame(Tweet oldItem, Tweet newItem) {
                    return oldItem == newItem;
                }

                @Override
                public boolean areContentsTheSame(Tweet oldItem, Tweet newItem) {
                    return (oldItem == newItem);
                }
            };

    public TweetsAdapter() {
        super(DIFF_CALLBACK);
        context = this.context;
        tweets = this.tweets;
    }

    public TweetsAdapter(Context mcontext, List<Tweet> mtweets) {
        super(DIFF_CALLBACK);
        context = mcontext;
        tweets = mtweets;
    }

    public void addMoreTweets(List<Tweet> newtweets)
    {
        tweets.addAll(newtweets);
        submitList((PagedList<Tweet>) tweets); // DiffUtil takes care of the check
    }

    public void swapItems(List<Tweet> contacts) {
        // compute diffs
        final TweetDiffCallback diffCallback = new TweetDiffCallback(this.tweets, contacts);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        // clear contacts and add
        this.tweets.clear();
        this.tweets.addAll(contacts);
        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
    }
}
