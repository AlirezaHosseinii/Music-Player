    package com.example.musicplayer;

    import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import java.util.ArrayList;

    public class SongDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "music_player.db";
        static final String TABLE_NAME = "songs";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_FILE_PATH = "file_path";
        static final String COLUMN_ARTIST = "artist";
        static final String COLUMN_COVER_IMAGE_URL = "cover_image_url";

        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_TABLE =
                "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_TITLE + " TEXT," +
                        COLUMN_FILE_PATH + " TEXT," +
                        COLUMN_ARTIST + " TEXT," +
                        COLUMN_COVER_IMAGE_URL + " TEXT" +
                        ")";

        SongDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public static ArrayList<Song> retrieveSongs(Context context) {
            ArrayList<Song> songs = new ArrayList<>();

            SongDbHelper dbHelper = new SongDbHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            try {
                Cursor cursor = db.query(
                        SongDbHelper.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    String title = null;
                    String artist = null;
                    String filePath = null;
                    String coverImageUrl = null;
                    do {
                        int columnTitleIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_TITLE);
                        int columnArtistIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_ARTIST);
                        int columnFilePathIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_FILE_PATH);
                        int columnCoverImageUrlIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_COVER_IMAGE_URL);

                        if(columnTitleIndex >= 0){
                            title = cursor.getString(columnTitleIndex);
                        }
                        if(columnCoverImageUrlIndex >= 0){
                            coverImageUrl = cursor.getString(columnCoverImageUrlIndex);
                        }
                        if(columnFilePathIndex >= 0){
                            filePath = cursor.getString(columnFilePathIndex);
                        }
                        if(columnArtistIndex >= 0){
                            artist = cursor.getString(columnArtistIndex);
                        }
                        songs.add(new Song(title, filePath, artist, coverImageUrl));
                    } while (cursor.moveToNext());

                    cursor.close();
                }
            } finally {
                db.close();
            }

            return songs;
        }

        public static boolean songExists(SQLiteDatabase db, String filePath) {
            Cursor cursor = db.query(
                    SongDbHelper.TABLE_NAME,
                    null,
                    SongDbHelper.COLUMN_FILE_PATH + " = ?",
                    new String[]{filePath},
                    null,
                    null,
                    null
            );

            boolean exists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }

            return exists;
        }

    }
