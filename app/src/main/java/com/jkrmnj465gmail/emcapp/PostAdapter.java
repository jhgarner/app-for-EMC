package com.jkrmnj465gmail.emcapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jack on 6/13/2015.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.SectionViewHolder> {
    public final static String POST = "com.jkrmnj465gmail.emcapp.POST";
    /*
     * This class has the purpose of making Recycler view useful and using certain mandatory methods.
     * Adapter is in charge of taking raw data (such as a string[]) and sorting it for the Recycler view
     * This gives recycler view info and views that are put into each parent.
     */
    //This variable stores what was clicked. -1 means nothing and is just a placeholder. It is public so main activity can get it.
    public int click = -1;
    //These hold the variables for the forum sections.
    private String[][] posts;
    private Bitmap[] avatars;

    //A helpful viewholder that gives the adapter access to all of the views and helps with performance
    //Important just to know that any view in the recycler goes here.
    public static final class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userName;
        WebView content;
        ImageView user_image;
        public MyViewHolderClicks mListener;

        public SectionViewHolder(View v, MyViewHolderClicks listener){
            super(v);
            mListener = listener;
            this.userName = (TextView) v.findViewById(R.id.username);
            this.user_image = (ImageView) v.findViewById(R.id.post_avatar);
            this.content = (WebView) v.findViewById(R.id.post_content);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            mListener.onSectionClick(v, this.getPosition());
        }

        public interface MyViewHolderClicks {
            public void onSectionClick(View caller, int position);
        }
    }

    // Simple constructor
    public PostAdapter(String[][] posts, Bitmap[] avatars) {
        this.posts = posts;
        this.avatars = avatars;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostAdapter.SectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_card_viewholder, viewGroup, false);
        // Do things like create the viewholder and return it. Also creates the onclick listener
        PostAdapter.SectionViewHolder vh = new SectionViewHolder(v, new PostAdapter.SectionViewHolder.MyViewHolderClicks() {
            //Modify this to change click behavior
            public void onSectionClick(View caller, int position) {
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (posts[0][1].equals("abort")) {
            holder.userName.setText("An error has occured. Please check your internet connection.");
        } else {
            holder.userName.setText(posts[position][2]);
            holder.user_image.setImageBitmap(avatars[position]);
            holder.content.setBackgroundColor(0);
            holder.content.setBackgroundResource(android.support.v7.cardview.R.color.cardview_light_background);
            //Webview + HTML is great. Some special things are added to make sure that it styles correctly.
            holder.content.loadDataWithBaseURL("http://empireminecraft.com/", "<TEXT=\"000000\"><style>img{display: inline; height:auto; max-width: 100%;}</style>" + posts[position][0], "text/html", "utf8", null);

        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return posts.length;
    }
}
