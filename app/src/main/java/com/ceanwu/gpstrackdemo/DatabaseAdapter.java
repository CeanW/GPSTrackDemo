package com.ceanwu.gpstrackdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Shengyun Wu on 2/7/2017.
 */

public class DatabaseAdapter {

    private MyDatabaseOpenHelper helper;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context context) {
        helper = new MyDatabaseOpenHelper(context);
    }

    /**
     * Add track item
     * @param track
     * @return
     */
    public int addTrack(Track track) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDatabaseOpenHelper.TRACK_NAME, track.getTrack_name());
        values.put(MyDatabaseOpenHelper.CREATE_DATE, track.getCreate_date());
        values.put(MyDatabaseOpenHelper.START_LOC, track.getStart_loc());
        values.put(MyDatabaseOpenHelper.END_LOC, track.getEnd_loc());
        long result = db.insertOrThrow(MyDatabaseOpenHelper.TABLE_TRACK, null, values);
        db.close();
        return (int) result;
    }

    /**
     * Update the latest location
     * @param endLoc
     * @param id
     */
    public void updateEndLoc(String endLoc, int id) {
        String sql = "UPDATE track SET end_loc=? WHERE _id=?";
        db = helper.getWritableDatabase();
        db.execSQL(sql, new Object[]{endLoc, id});
        db.close();
    }

    /**
     *
     * @param tid
     * @param lat
     * @param lng
     * @return
     */
    public int addTrackDetail(int tid, double lat, double lng) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDatabaseOpenHelper.LAT, lat);
        values.put(MyDatabaseOpenHelper.LNG, lng);
        values.put(MyDatabaseOpenHelper.TID, tid);
        long result = db.insertOrThrow(MyDatabaseOpenHelper.TABLE_TRACK_DETAIL, null, values);
        db.close();
        return (int) result;
    }

    public ArrayList<TrackDetail> getTrackDetails(int tid) {
        String sql = "SELECT _id, lat, lng FROM track_detail where tid=? order by _id desc";
        ArrayList<TrackDetail> trackDetails = new ArrayList<>();
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(tid)});
        if (c != null) {
            TrackDetail detail = null;
            int idColumn = c.getColumnIndex(MyDatabaseOpenHelper.ID);
            int latColumn = c.getColumnIndex(MyDatabaseOpenHelper.LAT);
            int lngColumn = c.getColumnIndex(MyDatabaseOpenHelper.LNG);

            while (c.moveToNext()) {
                detail = new TrackDetail(c.getInt(idColumn), c.getDouble(latColumn), c.getDouble(lngColumn));
                trackDetails.add(detail);
            }
            c.close();
        }
        return trackDetails;
    }

    public ArrayList<Track> getTracks(){
        String sql = "SELECT _id, track_name, create_date, start_loc, end_loc FROM track";
        ArrayList<Track> tracks = new ArrayList<>();
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        if (c != null) {
            Track track = null;
            int idColumn = c.getColumnIndex(MyDatabaseOpenHelper.ID);
            int nameColumn = c.getColumnIndex(MyDatabaseOpenHelper.TRACK_NAME);
            int dateColumn = c.getColumnIndex(MyDatabaseOpenHelper.CREATE_DATE);
            int startLocColumn = c.getColumnIndex(MyDatabaseOpenHelper.START_LOC);
            int endLocColumn = c.getColumnIndex(MyDatabaseOpenHelper.END_LOC);

            while (c.moveToNext()) {
                track = new Track(c.getInt(idColumn), c.getString(nameColumn),
                        c.getString(dateColumn), c.getString(startLocColumn), c.getString(endLocColumn));
                tracks.add(track);
            }
            c.close();
        }
        return tracks;
    }

    public void deleteTrack(int id){
        String sql1 = "DELETE FROM track WHERE _id=?";
        String sql2 = "DELETE FROM track_detail WHERE tid=?";

        db = helper.getWritableDatabase();
        try { //支持事件回滚，当成功时才提高，不成功时回到删除前状态
            db.beginTransaction();
            db.execSQL(sql1, new Object[]{id});
            db.execSQL(sql2, new Object[]{id});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            if (db != null) db.close();
        }
    }
}
