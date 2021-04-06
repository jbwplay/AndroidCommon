package com.androidbase.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ThreadInfo.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class RoomDataBase extends RoomDatabase {

    private static RoomDataBase INSTANCE;

    public abstract ThreadInfoDao threadInfoDao();

    private static final Object sLock = new Object();

    /**
     * Migrate from: version 1 - using Room to version 2
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // SQLite supports a limited operations for ALTER.
            // Changing the type of a column is not directly supported, so this is what we need
            // to do:
            // Create the new table
            // database.execSQL("CREATE TABLE users_new (userid TEXT NOT NULL," + "username TEXT," + "last_update INTEGER," + "PRIMARY KEY(userid))");
            // Copy the data
            // database.execSQL("INSERT INTO users_new (userid, username, last_update) " + "SELECT userid, username, last_update " + "FROM users");
            // Remove the old table
            // database.execSQL("DROP TABLE users");
            // Change the table name to the correct one
            // database.execSQL("ALTER TABLE users_new RENAME TO users");
        }
    };

    public static RoomDataBase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RoomDataBase.class, "roombaselib.db")
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                            }
                        })
                        /*.addMigrations(MIGRATION_1_2)*/
                        .build();
            }
            return INSTANCE;
        }
    }

}