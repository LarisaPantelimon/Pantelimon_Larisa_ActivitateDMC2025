package com.example.Entities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Accounts.class, AccountsWeb.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({/* Add converters if needed */})
public abstract class AppDatabase extends RoomDatabase {
    // DAO for Accounts table (lowercase 'a' to match Room conventions)
    public abstract AccountDao accountsDao();

    // DAO for AccountsWeb table
    public abstract AccountsWebDao accountsWebDao();

    // Additional method with uppercase 'A' to match your usage
    public AccountDao AccountDao() {
        return accountsDao(); // Simply returns the existing DAO instance
    }

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "accounts_database.db"
                            )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        // Initialize with admin account if needed
                                        if (INSTANCE != null) {
                                            Accounts adminAccount = new Accounts(
                                                    "admin@example.com",
                                                    "admin@example.com",
                                                    "admin_public_key",
                                                    "admin_private_key",
                                                    "some_iv"
                                            );
                                            INSTANCE.accountsDao().insert(adminAccount);
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