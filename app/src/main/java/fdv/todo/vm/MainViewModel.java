package fdv.todo.vm;

import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import fdv.todo.data.db.TasksDB;
import fdv.todo.data.db.TaskEntity;

// (9.1) make this class extend AndroidViewModel and implement its default constructor
public class MainViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();
    // (9.2) Add a tasks member variable for a list of TaskEntry objects wrapped in a LiveData
    private LiveData<List<TaskEntity>> tasks;
    // (9.3) In the constructor use the loadAllTasks of the taskDao to initialize the tasks variable
    public MainViewModel(Application application) {
        super(application);
        TasksDB database = TasksDB.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.tasksDao().loadAllTasks();
    }
    // (9.4) Create a getter for the tasks variable
    public LiveData<List<TaskEntity>> getTasks() {
        return tasks;
    }
}
