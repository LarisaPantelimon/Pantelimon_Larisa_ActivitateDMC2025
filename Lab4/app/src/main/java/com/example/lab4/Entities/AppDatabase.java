package com.example.lab4.Entities;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.lab4.Palton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Palton.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({/* Add converters if needed */})
public abstract class AppDatabase extends RoomDatabase {

    public abstract PaltonDao paltonDao();

    public PaltonDao PaltonDao() {
        return paltonDao();
    }

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static Date returnData(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date data = null;
        try {
            data = formatter.parse("13/06/2024");
            return data;
        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AppDatabase getInstance(final Context context) {
        return getDatabase(context);
    }


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "paltoane_database.db"
                            )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        // Initialize with admin account if needed
                                        if (INSTANCE != null) {
                                            Palton defaultPalton = new Palton(
                                                    "rosu",
                                                    true,
                                                    "S",
                                                    "300",
                                                    "lana",
                                                    returnData()
                                            );
                                            INSTANCE.paltonDao().insert(defaultPalton);
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Future schema changes
        }
    };
}
