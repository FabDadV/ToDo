package fdv.todo.data.db;

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
    List<TaskEntity> loadAllTasks();
    @Insert
    void insertTask(TaskEntity taskEntity);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntity taskEntity);
    @Delete
    void deleteTask(TaskEntity taskEntity);
}
