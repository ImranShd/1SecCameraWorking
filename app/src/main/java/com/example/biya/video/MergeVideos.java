/*package com.example.biya.video;

import android.app.ProgressDialog;
import android.graphics.Movie;
import android.os.AsyncTask;

import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static com.googlecode.mp4parser.authoring.container.mp4.MovieCreator.build;

public class MergeVideos extends AsyncTask<String, Integer, String>{

    private String workingPath;
    private ArrayList<String> videosToMerge;
    private ProgressDialog progressDialog;


    private MergeVideos(String workingPath, ArrayList<String> videosToMerge){
        this.workingPath = workingPath;
        this.videosToMerge = videosToMerge;

    }

    @Override
    protected  void onPreExecute(){}

    @Override
   protected String doInBackground(String... params){
        int count = videosToMerge.size();
        Movie[] inMovies = new Movie[count];
        for (int i=0; i<count; i++)
        {
            File file = new File (workingPath, videosToMerge.get(i));
            if(file.exists())
            {
                FileInputStream fs = null;
                try {
                    fs = new FileInputStream(file);
                    FileChannel fc = fs.getChannel();
                    inMovies[i] = build(String.valueOf(fc));
                    fs.close();
                    fc.close();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                }

            }

        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {

    }
}
*/