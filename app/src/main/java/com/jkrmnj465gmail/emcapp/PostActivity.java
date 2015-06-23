package com.jkrmnj465gmail.emcapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Arrays;


public class PostActivity extends AppCompatActivity implements GetPosts.fillUiInterface {
    //Some important variables because this activity has teh most logic after parsing
    String postUrl;
    int page = 1;
    boolean lastPage = false;
    String[][] posts;

    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.post_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        postUrl = intent.getStringExtra(ThreadListAdapter.POST);
        String threadTitle = intent.getStringExtra(ThreadListAdapter.NAME);
        getSupportActionBar().setTitle(threadTitle);
        AsyncTask test = new GetPosts(findViewById(R.id.loadingPost), this, findViewById(R.id.post_card), this).execute(postUrl);
        mRecyclerView = (RecyclerView) findViewById(R.id.post_card);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //checks if forward or backward are pressed
        if (id == R.id.forward) {
            //If we reach teh last page, a variable is set to stop the user from constantly trying to go farther.
            if(!lastPage) {
                page = page + 1;
                AsyncTask test = new GetPosts(findViewById(R.id.loadingPost), this, findViewById(R.id.post_card), this).execute(postUrl + "/page-" + page);
                mRecyclerView.setVisibility(View.INVISIBLE);
            }
            else{
                Snackbar snackbar = Snackbar.make(findViewById(R.id.post_layout), "You are at the last page", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.accent));
                snackbar.show();
            }
        }
        if (id == R.id.backward) {
            if(page - 1 >= 1){
                page = page-1;
                AsyncTask test = new GetPosts(findViewById(R.id.loadingPost), this, findViewById(R.id.post_card), this).execute(postUrl + "/page-" + page);
                mRecyclerView.setVisibility(View.INVISIBLE);
                lastPage = false;
            }
            else{
                Snackbar snackbar = Snackbar.make(findViewById(R.id.post_layout), "You are at the first page", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.accent));
                snackbar.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public void onFinish(String[][] posts, Bitmap[] avatars) {
        //Let up make sure that neither are null to stop the null pointer exception
        if(this.posts == null) {
            this.posts = posts;
        }
        //Now we can check if they are equal. Won't run the first time because of the else
        else if(Arrays.deepEquals(this.posts, posts)){
            page -= 1;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.post_layout), "You are at the last page", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.accent));
            snackbar.show();
            lastPage = true;
        }
        this.posts = posts;
        PostAdapter adapter = new PostAdapter(posts, avatars);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
