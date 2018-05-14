package fdv.todo.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import fdv.todo.data.db.TaskEntity;
import fdv.todo.data.db.TasksDB;

// (F.5) Make this class extend ViewModel
public class AddTaskViewModel extends ViewModel {
    // (F.6) Add a task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<TaskEntity> task;
    // (F.7) Create a constructor where you call loadTaskById of the taskDao to initialize the tasks variable
    // Note: The constructor should receive the database and the taskId
    public AddTaskViewModel(TasksDB database, int taskId) {
        task = database.tasksDao().loadTaskById(taskId);
    }
    // (F.8) Create a getter for the task variable
    public LiveData<TaskEntity> getTask() { return task; }
}
