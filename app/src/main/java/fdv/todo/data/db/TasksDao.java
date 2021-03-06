package fdv.todo.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TasksDao {
    @Query("SELECT * FROM tasks ORDER BY priority")
    LiveData<List<TaskEntity>> loadAllTasks();
    @Insert
    void insertTask(TaskEntity taskEntity);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntity taskEntity);
    @Delete
    void deleteTask(TaskEntity taskEntity);

    // (8.1) Wrap the return type with LiveData
    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<TaskEntity> loadTaskById(int id);

}
