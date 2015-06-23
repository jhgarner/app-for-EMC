package com.jkrmnj465gmail.emcapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.MalformedURLException;
import java.net.URL;


public class ThreadList extends AppCompatActivity implements GetThreads.fillUiInterface {
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    int sectionSelection;
    int section;
    URL threadUrl = null;

    String[][] threads;
    String[][] tempThreads;
    Bitmap[] avatars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //Time to find out what url we should connect to.
        Intent intent = getIntent();
        sectionSelection = intent.getIntExtra(SectionAdapter.SECTION, 0);
        try {
            switch (sectionSelection){
                case 0:
                        threadUrl = new URL("http://empireminecraft.com/forums/empire-news.9/");
                        getSupportActionBar().setTitle("Empire News");
                break;
                case 1:
                    threadUrl = new URL("http://empireminecraft.com/forums/empire-events.53/");
                    getSupportActionBar().setTitle("Empire Events");
                break;
                case 2:
                    threadUrl = new URL("http://empireminecraft.com/forums/empire-updates/");
                    getSupportActionBar().setTitle("Empire Updates");
                break;
                case 3:
                    threadUrl = new URL("http://empireminecraft.com/forums/empire-help-support.11/");
                    getSupportActionBar().setTitle("Help and Support");
                break;
                case 4:
                    threadUrl = new URL("http://empireminecraft.com/forums/the-suggestion-box.48/");
                    getSupportActionBar().setTitle("Suggestion Box");
                break;
                case 5:
                    threadUrl = new URL("http://empireminecraft.com/forums/introduce-yourself.5/");
                    getSupportActionBar().setTitle("Introduce Yourself");
                break;
                case 6:
                    threadUrl = new URL("http://empireminecraft.com/forums/share-your-emc-creations.6/");
                    getSupportActionBar().setTitle("Share Your Creations");
                break;
                case 7:
                    threadUrl = new URL("http://empireminecraft.com/forums/public-member-events.54/");
                    getSupportActionBar().setTitle("Public Member Events");
                break;
                case 8:
                    threadUrl = new URL("http://empireminecraft.com/forums/community-discussion.7/");
                    getSupportActionBar().setTitle("Community Discussion");
                break;
                case 9:
                    threadUrl = new URL("http://empireminecraft.com/forums/wilderness-frontier.58/");
                    getSupportActionBar().setTitle("Wilderness Frontier");
                break;
                case 10:
                    threadUrl = new URL("http://empireminecraft.com/forums/general-minecraft-discussion.18/");
                    getSupportActionBar().setTitle("General Minecraft Discussion");
                break;
                case 11:
                    threadUrl = new URL("http://empireminecraft.com/forums/marketplace-discussion.55/");
                    getSupportActionBar().setTitle("Marketplace Discussion");
                break;
                case 12:
                    threadUrl = new URL("http://empireminecraft.com/forums/products-businesses-services.39/");
                    getSupportActionBar().setTitle("Products, Businesses, and Services");
                break;
                case 13:
                    threadUrl = new URL("http://empireminecraft.com/forums/community-auctions.42/");
                    getSupportActionBar().setTitle("Community Auctions");
                break;
                case 14:
                    threadUrl = new URL("http://empireminecraft.com/forums/reverse-auctions.84/");
                    getSupportActionBar().setTitle("Reverse Auctions");
                break;
                case 15:
                    threadUrl = new URL("http://empireminecraft.com/forums/share-your-lets-plays-and-other-videos.60/");
                    getSupportActionBar().setTitle("Share Your Videos");
                break;
                case 16:
                    threadUrl = new URL("http://empireminecraft.com/forums/artists-gallery.73/");
                    getSupportActionBar().setTitle("Artist's Gallery");
                break;
                case 17:
                    threadUrl = new URL("http://empireminecraft.com/forums/shutter-talk.74/");
                    getSupportActionBar().setTitle("Shutter Talk");
                break;
                case 18:
                    threadUrl = new URL("http://empireminecraft.com/forums/writers-corner.75/");
                    getSupportActionBar().setTitle("Writer's Corner");
                break;
                case 19:
                    threadUrl = new URL("http://empireminecraft.com/forums/the-jukebox.79/");
                    getSupportActionBar().setTitle("The Jukebox");
                break;
                case 20:
                    threadUrl = new URL("http://empireminecraft.com/forums/gaming.20/");
                    getSupportActionBar().setTitle("Gaming");
                break;
                case 21:
                    threadUrl = new URL("http://empireminecraft.com/forums/miscellaneous.29/");
                    getSupportActionBar().setTitle("Miscellaneous");
                break;
                default:
                    threadUrl = new URL("http://empireminecraft.com/forums/miscellaneous.29/");
                    getSupportActionBar().setTitle("An Error Has Occured. Report to Jkrmnj");
                break;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Start the parser/downloader
        AsyncTask test = new GetThreads(findViewById(R.id.loadingThread), this, findViewById(R.id.thread_list), this).execute(threadUrl);

        //setup only part of the recyclerview.
        mRecyclerView = (RecyclerView) findViewById(R.id.thread_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thread_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
    public void onFinish(String[][] threads, Bitmap[] avatars) {

        //Due to some weird bugs, I had to set these things from within this class but I needed to wait for the parser to finish
        //The solution uses a callback which calls this thread and finishes the setting of the recyclerView.
        ThreadListAdapter adapter = new ThreadListAdapter(threads, avatars);
        mRecyclerView.setAdapter(adapter);
    }



}
