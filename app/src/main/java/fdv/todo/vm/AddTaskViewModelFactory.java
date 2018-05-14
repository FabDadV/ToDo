package fdv.todo.vm;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import fdv.todo.data.db.TasksDB;

// (F.1) Make this class extend ViewModelAddTaskViewModelFactory ViewModelProvider.NewInstanceFactory
public class AddTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    // (F.2) Add two member variables. One for the database and one for the taskId
    private final TasksDB db;
    private final int taskId;
    // (F.3) Initialize the member variables in the constructor with the parameters received
    public AddTaskViewModelFactory(TasksDB database, int taskId) {
        db = database;
        this.taskId = taskId;
    }
    // (F.4) Uncomment the following method
    // Note: This can be reused with minor modifications
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddTaskViewModel(db, taskId);
    }
}
