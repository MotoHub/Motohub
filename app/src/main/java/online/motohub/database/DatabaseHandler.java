package online.motohub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import online.motohub.model.ProfileResModel;
import online.motohub.model.SpectatorLiveEntity;
import online.motohub.model.SpectatorLiveModel;
import online.motohub.model.VideoUploadModel;
import online.motohub.util.AppConstants;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";

    private String LOCAL_USER_TABLE = "LOCAL_USER_TABLE";
    private String ID = "ID";
    private String LOGIN_TYPE = "LOGIN_TYPE";
    private String EMAIL = "EMAIL";
    private String PASWWORD = "PASWWORD";
    private String USER_IMG_URL = "USER_IMG_URL";
    private String USER_NAME = "USER_NAME";

    //Offline Storage


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
                + VideoUploadModel.UploadUserType + " TEXT" + ")";

        String CREATE_VIDEO_TABLE = "CREATE TABLE " + SpectatorLiveModel.TABLE + "("
                + SpectatorLiveModel.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SpectatorLiveModel.PROFILE_ID + " INTEGER,"
                + SpectatorLiveModel.USERID + " INTEGER,"
                + SpectatorLiveModel.USERTYPE + " TEXT,"
                + SpectatorLiveModel.CAPTION + " TEXT,"
                + SpectatorLiveModel.FILEURL + " TEXT,"
                + SpectatorLiveModel.THUMBNAIL + " TEXT,"
                + SpectatorLiveModel.EVENTID + " INTEGER,"
                + SpectatorLiveModel.EVENT_FINISH_DATE + " TEXT,"
                + SpectatorLiveModel.LIVE_POST_PROFILE_ID + " INTEGER" + ")";

        String CREATE_LOCAL_USER_TABLE = "CREATE TABLE " + LOCAL_USER_TABLE + "(" + ID + " TEXT PRIMARY KEY,"
                + LOGIN_TYPE + " TEXT,"
                + EMAIL + " TEXT,"
                + PASWWORD + " TEXT,"
                + USER_IMG_URL + " TEXT,"
                + USER_NAME + " TEXT"
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_VIDEO_TABLE);
        db.execSQL(CREATE_LOCAL_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VideoUploadModel.VideoUploadTable);
        db.execSQL("DROP TABLE IF EXISTS " + SpectatorLiveModel.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_USER_TABLE);
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
                + VideoUploadModel.Notificationflag + " ,"
                + VideoUploadModel.UploadUserType + " from " + VideoUploadModel.VideoUploadTable + " where " + VideoUploadModel.VideoUploadURL + " ='" + filename + "'";
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

    //TODO SaveVideos
    public void insertSpectatorLiveVideo(SpectatorLiveEntity objSpectatorModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SpectatorLiveModel.PROFILE_ID, objSpectatorModel.getProfileID());
        values.put(SpectatorLiveModel.USERID, objSpectatorModel.getUserID());
        values.put(SpectatorLiveModel.USERTYPE, AppConstants.USER_EVENT_VIDEOS);
        values.put(SpectatorLiveModel.CAPTION, objSpectatorModel.getCaption());
        values.put(SpectatorLiveModel.FILEURL, objSpectatorModel.getVideoUrl());
        values.put(SpectatorLiveModel.THUMBNAIL, objSpectatorModel.getThumbnail());
        values.put(SpectatorLiveModel.EVENTID, objSpectatorModel.getEventID());
        values.put(SpectatorLiveModel.EVENT_FINISH_DATE, objSpectatorModel.getEventFinishDate());
        values.put(SpectatorLiveModel.LIVE_POST_PROFILE_ID, objSpectatorModel.getLivePostProfileID());
        // Inserting Row
        db.insert(SpectatorLiveModel.TABLE, null, values);
        db.close();
    }

    //TODO get all saved videos
    public ArrayList<SpectatorLiveEntity> getSpectatorLiveVideos() {
        ArrayList<SpectatorLiveEntity> mSpectatorLiveEntityArrayList = new ArrayList<SpectatorLiveEntity>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String queryTaxMaster = "SELECT * FROM " + SpectatorLiveModel.TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(queryTaxMaster, null);
        if (cursor.moveToFirst()) {
            do {
                SpectatorLiveEntity mSpectatorLiveEntity = new SpectatorLiveEntity();
                mSpectatorLiveEntity.setID(String.valueOf(cursor.getInt(0)));
                mSpectatorLiveEntity.setProfileID(String.valueOf(cursor.getInt(1)));
                mSpectatorLiveEntity.setUserID(String.valueOf(cursor.getInt(2)));
                mSpectatorLiveEntity.setUserType(cursor.getString(3));
                mSpectatorLiveEntity.setCaption(cursor.getString(4));
                mSpectatorLiveEntity.setVideoUrl(cursor.getString(5));
                mSpectatorLiveEntity.setThumbnail(cursor.getString(6));
                mSpectatorLiveEntity.setEventID(String.valueOf(cursor.getInt(7)));
                mSpectatorLiveEntity.setEventFinishDate(cursor.getString(8));
                mSpectatorLiveEntity.setLivePostProfileID(String.valueOf(cursor.getInt(9)));
                mSpectatorLiveEntityArrayList.add(mSpectatorLiveEntity);
            } while (cursor.moveToNext());
        }
        return mSpectatorLiveEntityArrayList;
    }

    public void deleteSpectator() {
        SQLiteDatabase db = this.getWritableDatabase(); //get database
        db.execSQL("DELETE FROM " + SpectatorLiveModel.TABLE); //delete all rows in a table
        db.close();
    }
	
	public void deleteRow(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + SpectatorLiveModel.TABLE + " WHERE " + SpectatorLiveModel.ID + "='" + value + "'");
        db.close();
    }

    public void addLocalUser(ProfileResModel mProfileModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, mProfileModel.getEmail());
        values.put(LOGIN_TYPE, mProfileModel.getLoginType());
        values.put(EMAIL, mProfileModel.getEmail());
        values.put(PASWWORD, mProfileModel.getPassword());
        values.put(USER_IMG_URL, mProfileModel.getProfilePicture());
        values.put(USER_NAME, mProfileModel.getUserName());
        db.insert(LOCAL_USER_TABLE, null, values);
        db.close();
    }

    public ArrayList<ProfileResModel> getLocalUserList(){
        ArrayList<ProfileResModel> mProfileList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String queryTaxMaster = "SELECT * FROM " + LOCAL_USER_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(queryTaxMaster, null);
        if (cursor.moveToFirst()) {
            do {
                ProfileResModel mProfileModel = new ProfileResModel();
                mProfileModel.setDB_ID(cursor.getString(0));
                mProfileModel.setLoginType(cursor.getString(1));
                mProfileModel.setEmail(cursor.getString(2));
                mProfileModel.setPassword(cursor.getString(3));
                mProfileModel.setProfilePicture(cursor.getString(4));
                mProfileModel.setUserName(cursor.getString(5));
                mProfileList.add(mProfileModel);
            } while (cursor.moveToNext());
        }
        return mProfileList;
    }
}