package fdv.todo.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import fdv.todo.R;
import fdv.todo.AppExecutors;
import fdv.todo.data.db.TaskEntity;
import fdv.todo.data.db.TasksDB;
import fdv.todo.vm.AddTaskViewModel;
import fdv.todo.vm.AddTaskViewModelFactory;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    // Fields for views
    EditText editText;
    RadioGroup radioGroup;
    Button button;

    private int taskId = DEFAULT_TASK_ID;
    // (2.3) Create TaskDB member variable for the Database
    private TasksDB db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        // Initialize member variable for the data base
        db = TasksDB.getInstance(getApplicationContext());
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            taskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            button.setText(R.string.update_button);
            if (taskId == DEFAULT_TASK_ID) {
                // populate the UI
                taskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                // TODO (F.9) Remove the logging and the call to loadTaskById, this is done in the ViewModel now
                // TODO (F.10) Declare a AddTaskViewModelFactory using mDb and mTaskId
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(db, taskId);
                // TODO (F.11) Declare a AddTaskViewModel variable and initialize it by calling
                // ViewModelProviders.of for that use the factory created above AddTaskViewModel
                final AddTaskViewModel viewModel = ViewModelProviders
                        .of(this, factory).get(AddTaskViewModel.class);
                // TODO (F.12) Observe the LiveData object in the ViewModel. Use it also when removing the observer
                viewModel.getTask().observe(this, new Observer<TaskEntity>() {
                    @Override
                    public void onChanged(@Nullable TaskEntity task) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(task);
                    }
                });
/*
                Log.d(TAG, "Actively retrieving a specific task from the DataBase");
                // (8.2) Fix compile issue by wrapping the return type with LiveData
                final LiveData<TaskEntity> task = db.tasksDao().loadTaskById(taskId);
                // (8.4) Observe tasks and move the logic from runOnUiThread to onChanged
                task.observe(this, new Observer<TaskEntity>() {
                    @Override
                    public void onChanged(@Nullable TaskEntity taskEntity) {
                        // (8.5) Remove the observer as we do not need it any more
                        task .removeObserver(this);
                        Log.d(TAG, "Receiving database update from LiveData");
                        populateUI(taskEntity);
                    }
                });
*/
/*
// (8.3) Extract all this logic outside the Executor and remove the Executor
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final TaskEntity task = db.tasksDao().loadTaskById(mTaskId);
                        // We will be able to simplify this once we learn more
                        // about Android Architecture Components
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateUI(tasks);
                            }
                        });
                    }
                });
*/
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, taskId);
        super.onSaveInstanceState(outState);
    }
    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        editText = findViewById(R.id.editTextTaskDescription);
        radioGroup = findViewById(R.id.radioGroup);
        button = findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }
    /**
     * populateUI would be called to populate the UI when in update mode
     * @param task the taskEntity to populate the UI
     */
    private void populateUI(TaskEntity task) {
        if (task == null) {
            return;
        }
        editText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
    }
    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        // Create a description variable and assign to it the value in the edit text
        String description = editText.getText().toString();
        // Create a priority variable and assign the value returned by getPriorityFromViews()
        int priority = getPriorityFromViews();
        // Create a date variable and assign to it the current Date
        Date date = new Date();
        // Create taskEntity variable using the variables defined above
        // (4.4) Make taskEntry final so it is visible inside the run method
        final TaskEntity task = new TaskEntity(description, priority, date);
        // (4.2) Get the diskIO Executor from the instance of AppExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (taskId == DEFAULT_TASK_ID) {
                    // insert new task
                    db.tasksDao().insertTask(task);
                } else {
                    //update task
                    task.setId(taskId);
                    db.tasksDao().updateTask(task);
                }
                finish();
            }
        });
/* Use the tasksDao in the TaskDB variable to insert the taskEntity on UI thread:
      db.tasksDao().insertTask(taskEntity);
      finish();
*/
    }
    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }
    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }
}
