package fdv.todo.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import fdv.todo.utils.DateConverter;

@Database(entities = {TaskEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class TasksDB extends RoomDatabase {
    private static final String LOG_TAG = TasksDB.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static TasksDB sInstance;

    public static TasksDB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        TasksDB.class, TasksDB.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }
/*
    (4.1) Disable queries in sInstance on main thread!!!
    Queries should be done in a separate thread to avoid locking the UI.
    We will allow this ONLY TEMPORALLY to see that our DB is working.
                        .allowMainThreadQueries()
*/

    public abstract TasksDao tasksDao();
}
