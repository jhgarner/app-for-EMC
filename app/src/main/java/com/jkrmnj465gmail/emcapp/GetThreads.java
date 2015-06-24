package com.jkrmnj465gmail.emcapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jack on 12/6/2014.
 */
public class GetThreads extends AsyncTask<URL, Integer, String[][]> {
    public String[][] threads = new String[50][3];
    public Bitmap avatars[];
    Matcher avatarMatcher;
    Pattern avatarFinder;
    Pattern threadFinder;
    Pattern titleFinder;
    Matcher urlMatcher;
    Matcher titleMatcher;
    View loading;
    View recycler;
    Context context;
    ProgressBar spinner;
    fillUiInterface callback;
    int numThread;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public GetThreads(View loading, Context context, View recycler, fillUiInterface callback){
        this.loading = loading;
        this.context = context;
        this.recycler = recycler;
        this.callback = callback;

    }
    @Override
    protected void onPreExecute(){
        //Turn on the loading circle
        spinner = (ProgressBar) loading;
        spinner.setVisibility(View.VISIBLE);
    }
    @Override
    protected String[][] doInBackground(URL... selection){
        URL section = null;
        try {
            section = new URL(selection[0].toString());
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }
        //Are we connected to the internet?
        boolean abort = false;
        //Opening the connection
        BufferedReader why = null;
        try {
            why = new BufferedReader(
                    new InputStreamReader(section.openStream()));
        } catch (IOException e1) {
            threads[0][0] = "An error has occured. Please check your internet connection";
            threads[0][1] = "abort";
            abort = true;
            e1.printStackTrace();
        }
        BufferedReader in = why;

        String inputLine ="";
        //Stores the number of threads in the section. Varies because of pinned threads
        numThread = 0;
        //Small variables used to tell the parser what to look for
        int avatarEndUrl = 0;
        boolean foundStart = false;
        int start = 0;
        boolean firstQuote = false;
        int webLoc = 0;
        int urlEnd = 0;

        //When finding is equal to 0, it is looking for avatar. 1 looks for url. 2 looks for title.
        int finding = 0;

        avatarFinder = Pattern.compile("data-avatarHtml=\"true\"><img src=\"");
        threadFinder = Pattern.compile("<a href=\"threads/");
        titleFinder = Pattern.compile("/preview\">");

        //Time to start scraping the file but only if we are connected to the internet
        if(!abort){
            try {
                while ((inputLine = in.readLine()) != null) {
                    switch (finding) {
                        case 0:
                            //A matcher for finding the avatars
                            avatarMatcher = avatarFinder.matcher(inputLine);
                            //Did we get a match?
                            if (avatarMatcher.find()) {
                                //If we got a match, we need to find where the url ends
                                for (int i = avatarMatcher.end(); i < inputLine.length(); i++) {
                                    if (inputLine.charAt(i) == '\"') {
                                        avatarEndUrl = i;
                                        break;
                                    }
                                }
                                //Grab the url from the line

                                threads[numThread][2] = inputLine.substring(avatarMatcher.end(), avatarEndUrl);

                                finding++;
                            }
                            break;
                        case 1:
                            urlMatcher = threadFinder.matcher(inputLine);
                            if (urlMatcher.find()) {
                                for (int i = 15; i < inputLine.length(); i++) {
                                    if (inputLine.charAt(i) == '\"') {
                                        urlEnd = i - 1;
                                        break;
                                    }
                                }
                                threads[numThread][0] = inputLine.substring(13, urlEnd);
                                finding++;
                            }
                            break;
                        case 2:
                            titleMatcher = titleFinder.matcher(inputLine);
                            if (titleMatcher.find()) {
                                threads[numThread][1] = inputLine.substring(titleMatcher.end(), inputLine.length() - 4);
                                //The html stores some characters is weird ways. Time to change them.
                                threads[numThread][1] = threads[numThread][1].replaceAll("&quot;", "\"");
                                threads[numThread][1] = threads[numThread][1].replaceAll("&amp;", "&");
                                numThread++;
                                finding = 0;
                            }
                            break;
                    }


                }
                try {
                    in.close();
                } catch (IOException e) {
                    threads[0][0] = "An error has occured. Please check your internet connection";
                    threads[0][1] = "abort";
                    e.printStackTrace();
                }
            } catch (IOException e) {
                threads[0][0] = "An error has occured. Please check your internet connection";
                threads[0][1] = "abort";
                e.printStackTrace();
            }
            avatars = new Bitmap[numThread];
            for (int i = 0; i < numThread; i++) {
                URL url = null;
                try {
                    if(threads[i][2].charAt(4) == ':') {
                        url = new URL(threads[i][2]);
                    }
                    else{
                        url = new URL("http://empireminecraft.com/" + threads[i][2]);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    avatars[i] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return threads;
    }
    @Override
    protected void onPostExecute(String[][] threads){
        if(numThread == 0){
            numThread++;
        }
        String[][] sizedThreads = new String[numThread][3];
        for(int i = 0; i<numThread; i++){
            sizedThreads[i] = threads[i];
        }
        spinner.setVisibility(View.GONE);
        callback.onFinish(sizedThreads, avatars);
    }
    public interface fillUiInterface {
        public void onFinish(String[][] threads, Bitmap[] avatars);
    }

}
