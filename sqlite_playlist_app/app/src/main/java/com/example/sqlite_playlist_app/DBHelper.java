package com.example.sqlite_playlist_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "myvideos";
    public static final int DBVERSION = 1;

    public static final String TBL_VIDEO = "video";

    public static final String COL_VIDEO_ID = BaseColumns._ID;
    public static final String COL_VIDEO_PATH = "video_path";


    SQLiteDatabase mDB;

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
        mDB = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String crt_video_table = "CREATE TABLE IF NOT EXISTS " + TBL_VIDEO + "(" +
                COL_VIDEO_ID + " INTEGER PRIMARY KEY," +
                COL_VIDEO_PATH + " TEXT UNIQUE" +
                ")";
        db.execSQL(crt_video_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addVideo(String path) {
        ContentValues cv = new ContentValues();
        cv.put(COL_VIDEO_PATH,path);
        return mDB.insert(TBL_VIDEO,null,cv);
    }

    public Cursor getVideos() {
        return mDB.query(TBL_VIDEO,null,null,null,null,null,null);
    }

    public int deleteVideoFromDB(long id) {
        String whereclause = COL_VIDEO_ID + "=?";
        String[] whereargs = new String[]{String.valueOf(id)};
        return mDB.delete(TBL_VIDEO,whereclause,whereargs);
    }
}