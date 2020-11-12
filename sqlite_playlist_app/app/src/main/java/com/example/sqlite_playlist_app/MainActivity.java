package com.example.sqlite_playlist_app;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    TextView mMyTextView;
    ListView mVideoList;
    VideoView mVideoViewer;
    DBHelper mDBHlpr;
    Cursor mCsr;
    SimpleCursorAdapter mSCA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyTextView =  this.findViewById(R.id.mytext);
        mVideoList = this.findViewById(R.id.videolist);
        mVideoViewer = this.findViewById(R.id.videoviewer);

        mDBHlpr = new DBHelper(this);
        addVideosFromRawResourceToDB();
    }

    @Override
    protected void onDestroy() {
        mCsr.close(); //<<<<<<<<<< clear up the Cursor
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manageListView(); //<<<<<<<<<< rebuild and redisplay the List of Videos (in case they have changed)
    }

    /**
     *  Setup or Refresh the ListView adding the OnItemClick and OnItemLongClick listeners
     */
    private void manageListView() {
        mCsr = mDBHlpr.getVideos();

        // Not setup so set it up
        if (mSCA == null) {
            // Instantiate the SimpleCursorAdapter
            mSCA = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_1, // Use stock layout
                    mCsr, // The Cursor with the list of videos
                    new String[]{DBHelper.COL_VIDEO_PATH}, // the column (columns)
                    new int[]{android.R.id.text1}, // the view id(s) into which the column(s) data will be placed
                    0
            );
            mVideoList.setAdapter(mSCA); // Set the adpater for the ListView
            /**
             * Add The Long Click Listener (will delete the video row from the DB (NOT the video))
             */
            mVideoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    mDBHlpr.deleteVideoFromDB(id);
                    manageListView(); // <<<<<<<<<< refresh the ListView as data has changed
                    return true;
                }
            });
            /**
             * Play the respective video when the item is clicked
             * Note Cursor should be at the correct position so data can be extracted directly from the Cursor
             */
            mVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setCurrentVideo(mCsr.getString(mCsr.getColumnIndex(DBHelper.COL_VIDEO_PATH)));
                }
            });
        } else {
            mSCA.swapCursor(mCsr); //<<<<<<<<<< apply the changed Cursor
        }
    }

    /**
     * Set the currrent video and play it
     * @param path the path (resource name of the video)
     */
    private void setCurrentVideo(String path) {

        mVideoViewer.setVideoURI(
                Uri.parse(
                        "android.resource://" + getPackageName() + "/" + String.valueOf(
                                getResources().getIdentifier(
                                        path,
                                        "raw",
                                        getPackageName())
                        )
                )
        );
        mVideoViewer.start();
    }

    /**
     *  Look at all the resources in the res/raw folder and add the to the DB (not if they are duplicates due to UNQIUE)
     */
    private void addVideosFromRawResourceToDB() {
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            Log.i("Raw Asset: ", fields[count].getName());
            mDBHlpr.addVideo(fields[count].getName());
        }
    }
}