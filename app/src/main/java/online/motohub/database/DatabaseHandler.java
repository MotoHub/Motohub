package online.motohub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import online.motohub.model.VideoUploadModel;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + VideoUploadModel.VideoUploadTable + "("
                + VideoUploadModel.VideoUploadID + " INTEGER PRIMARY KEY,"
                + VideoUploadModel.VideoUploadURL + " TEXT,"
                + VideoUploadModel.Notificationflag + " INTEGER,"
                + VideoUploadModel.ProfileID + " INTEGER,"
                + VideoUploadModel.TaggedProfileID + " TEXT,"
                + VideoUploadModel.NotificationIsRunning + " INTEGER,"
                + VideoUploadModel.VideoPost + " TEXT,"
                + VideoUploadModel.VideoFlag + " INTEGER,"
                + VideoUploadModel.VideoUploadThumbnailurl + " TEXT,"
                + VideoUploadModel.UploadUserType + " TEXT"+ ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VideoUploadModel.VideoUploadTable);

        onCreate(db);
    }

    // code to add the new contact
    public void addVideoDetails(VideoUploadModel videoUploadModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VideoUploadModel.VideoUploadURL, videoUploadModel.getVideoURL());
        values.put(VideoUploadModel.VideoUploadThumbnailurl, videoUploadModel.getThumbnailURl());
        values.put(VideoUploadModel.VideoPost, videoUploadModel.getPosts());
        values.put(VideoUploadModel.ProfileID, videoUploadModel.getProfileID());
        values.put(VideoUploadModel.TaggedProfileID, videoUploadModel.getTaggedProfileID());
        values.put(VideoUploadModel.NotificationIsRunning, 2);
        values.put(VideoUploadModel.Notificationflag, videoUploadModel.getNotificationflag());
        values.put(VideoUploadModel.VideoFlag, videoUploadModel.getFlag());
        values.put(VideoUploadModel.UploadUserType, videoUploadModel.getUserType());
        db.insert(VideoUploadModel.VideoUploadTable, null, values);
        db.close();
    }

    // code to get the single contact
    public VideoUploadModel getPost(String filename) {
        //  String post="";
        SQLiteDatabase db = this.getReadableDatabase();
        VideoUploadModel videoUploadModel = null;
        String query = "select " + VideoUploadModel.VideoPost + " ,"
                + VideoUploadModel.ProfileID + " ,"
                + VideoUploadModel.TaggedProfileID + " ,"
                + VideoUploadModel.Notificationflag+ " ,"
                + VideoUploadModel.UploadUserType  + " from " + VideoUploadModel.VideoUploadTable + " where " + VideoUploadModel.VideoUploadURL + " ='" + filename + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            videoUploadModel = new VideoUploadModel();
            videoUploadModel.setPosts(cursor.getString(0));
            videoUploadModel.setProfileID(cursor.getInt(1));
            videoUploadModel.setTaggedProfileID(cursor.getString(2));
            videoUploadModel.setNotificationflag(cursor.getInt(3));
            videoUploadModel.setUserType(cursor.getString(4));
        }
        if (cursor != null) {
            cursor.close();
            db.close();
        }
        return videoUploadModel;
    }

    public VideoUploadModel getImage(String filename) {
        //  String post="";
        SQLiteDatabase db = this.getReadableDatabase();
        VideoUploadModel videoUploadModel = null;
        String query = "select " + VideoUploadModel.ProfileID + " ,"
                + VideoUploadModel.TaggedProfileID + " ,"
                + VideoUploadModel.Notificationflag + " from " + VideoUploadModel.VideoUploadTable + " where " + VideoUploadModel.VideoUploadThumbnailurl + " ='" + filename + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            videoUploadModel = new VideoUploadModel();
            videoUploadModel.setProfileID(cursor.getInt(0));
            videoUploadModel.setTaggedProfileID(cursor.getString(1));
            videoUploadModel.setNotificationflag(cursor.getInt(2));
        }
        if (cursor != null) {
            cursor.close();
            db.close();
        }
        return videoUploadModel;
    }

    public int getPendingCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + VideoUploadModel.VideoUploadTable + " WHERE " + VideoUploadModel.NotificationIsRunning + "=?", new String[]{String.valueOf(2)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(VideoUploadModel.VideoUploadTable, null, null);
    }

    // Deleting single contact
    public void deletepost(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(VideoUploadModel.VideoUploadTable, VideoUploadModel.Notificationflag + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

}