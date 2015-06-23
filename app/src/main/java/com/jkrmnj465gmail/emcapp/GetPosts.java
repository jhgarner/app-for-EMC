package com.jkrmnj465gmail.emcapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
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

//Very similar to GetThreads with changes to grab other things instead.
public class GetPosts extends AsyncTask<String, Void, String[][]> {

    String posts[][] = new String[30][3];
    int numPosts = 0;

    public Bitmap avatars[];

    Matcher postMatcher;
    Pattern postFinder;

    Matcher avatarMatcher;
    Pattern avatarFinder;

    Matcher nameMatcher;
    Pattern nameFinder;

    View loading;
    View recycler;
    Context context;
    ProgressBar spinner;
    fillUiInterface callback;

    public GetPosts(View loading, Context context, View recycler, fillUiInterface callback){
        this.loading = loading;
        this.context = context;
        this.recycler = recycler;
        this.callback = callback;
    }

    protected void onPreExecute(){
        spinner = (ProgressBar) loading;
        spinner.setVisibility(View.VISIBLE);
    }

    protected String[][] doInBackground(String... urls) {

        boolean inPost = false;
        int currentLine = 0;
        int startPost = 0;
        boolean findName = false;

        URL post = null;
        try {
            post = new URL("http://empireminecraft.com/" + urls[0]);
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }
        //Are we connected to the internet?
        boolean abort = false;
        //Opening the connection
        BufferedReader why = null;
        try {
            why = new BufferedReader(
                    new InputStreamReader(post.openStream()));
        } catch (IOException e1) {
            posts[0][0] = "An error has occured. Please check your internet connection";
            posts[0][1] = "abort";
            abort = true;
            e1.printStackTrace();
        }
        BufferedReader in = why;

        String inputLine ="";
        if(!abort) {
            postFinder = Pattern.compile("article>");
            nameFinder = Pattern.compile("class=\"username author\">");
            avatarFinder = Pattern.compile("background-image: url\\('");
            try {
                while ((inputLine = in.readLine()) != null) {

                    //Checks if we are inside of a post.
                    // "article>" comes up at the beginning and end but nowhere else.
                    postMatcher = postFinder.matcher(inputLine);
                    if(postMatcher.find()){
                        if(!inPost) {
                            inPost = true;
                            posts[numPosts][0] = "";
                        }
                        else{
                            inPost = false;

                        }
                        continue;
                    }
                    if(inPost) {
                        posts[numPosts][0] += inputLine;
                    }


                    //We now need to check what the username is of the poster.
                    //class="username author"> is used and comes after everything else (the little grey text at the bottom of a post)
                    nameMatcher = nameFinder.matcher(inputLine);
                    if(nameMatcher.find()){
                        //This ends right before the url so time to start grabbing.
                        //The name also ends a specific number of characters before the end of the line which is helpful
                        posts[numPosts][2] = inputLine.substring(nameMatcher.end(), inputLine.length() - 5);
                        numPosts++;
                    }

                    //So now let us check for the avatar.
                    //"background-image: url('" come right before the avatar url so time to find that.
                    avatarMatcher = avatarFinder.matcher(inputLine);
                    if(avatarMatcher.find()){
                        posts[numPosts][1] = "";
                        //Since it ends right before the avatar url, start adding it to the array
                        //But we need to keep adding until we find a '
                        for(int i = avatarMatcher.end(); i < inputLine.length(); i++){
                            if(inputLine.charAt(i) == '\''){
                                //We should always break to exit the loop and it should never hit the end of input line
                                break;
                            }
                            posts[numPosts][1] += inputLine.charAt(i);
                        }
                        //If the avatar url is stored on EMC servers, the html won't have the full url
                        //Fix that by checking if it starts with http and add it if it doesn't.
                        if(!posts[numPosts][1].startsWith("http")){
                            posts[numPosts][1] = "http://empireminecraft.com/" + posts[numPosts][1];
                        }
                    }


                }
                try {
                    in.close();
                } catch (IOException e) {
                    posts[0][0] = "An error has occured. Please check your internet connection";
                    posts[0][1] = "An error has occured. Please check your internet connection";
                    e.printStackTrace();
                }
            } catch (IOException e) {
                posts[0][0] = "An error has occured. Please check your internet connection";
                posts[0][1] = "An error has occured. Please check your internet connection";
                e.printStackTrace();
            }
            avatars = new Bitmap[numPosts];
            for (int i = 0; i < numPosts; i++) {
                URL url = null;
                try {

                        url = new URL(posts[i][1]);

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
        return posts;
    }
    protected void onProgressUpdate() {

    }

    protected void onPostExecute(String[][] donePost) {
        String[][] sizedPosts = new String[numPosts][3];
        for(int i = 0; i<numPosts; i++){
            sizedPosts[i] = posts[i];
        }
        spinner.setVisibility(View.GONE);
        callback.onFinish(sizedPosts, avatars);


    }
    public interface fillUiInterface {
        public void onFinish(String[][] posts, Bitmap[] avatars);
    }
}

