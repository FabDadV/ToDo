package fdv.todo.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.util.List;

import fdv.todo.AppExecutors;
import fdv.todo.R;
import fdv.todo.data.db.TaskEntity;
import fdv.todo.data.db.TasksDB;
import fdv.todo.vm.MainViewModel;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {
    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    // Create TaslDB member variable for the Database
    private TasksDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set the RecyclerView to its corresponding view
        recyclerView = findViewById(R.id.recyclerViewTasks);
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the adapter and attach it to the RecyclerView
        adapter = new TaskAdapter(this, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);
        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                // (5.1) Get the diskIO Executor from the instance of AppExecutors and
                // call the diskIO execute method with a new Runnable and implement its run method
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // (5.3) get the position from the viewHolder parameter
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntity> tasks = adapter.getTasks();
                        // (5.4) Call deleteTask in the taskDao with the task at that position
                        db.tasksDao().deleteTask(tasks.get(position));
                        // (5.6) Call retrieveTasks method to refresh the UI
                        // (7.6) Remove the call to retrieveTasks:  retrieveTasks();
                    }
                });

            }
        }).attachToRecyclerView(recyclerView);
        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });
        // Initialize member variable for the data base
        db = TasksDB.getInstance(getApplicationContext());
        // (7.7) Call retrieveTasks from here and remove the onResume method
        setupViewModel();
    }
    @Override
    public void onItemClickListener(int itemId) {
        Log.d(TAG,"Item: " + String.valueOf(itemId));
        // Launch AddTaskActivity adding the itemId as an extra in the intent
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
            startActivity(intent);
    }
    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this re-queries the database data for any changes.
     */
/*
    @Override
    protected void onResume() {
        super.onResume();
        // (4.5) Get the diskIO Executor from the instance of AppExecutors and call
        // the diskIO execute method with a new Runnable and implement its run method
        // COMPLETED (5) Get the diskIO Executor from the instance of AppExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        // (5.5) Extract the logic to a retrieveTasks method so it can be reused
        retrieveTasks();
    }
*/
/* Call the adapter's setTasks method using the result of the loadAllTasks method from the taskDao
        adapter.setTasks(db.tasksDao().loadAllTasks());
*/
    // Refactor to a more suitable name such as setupViewModel
    private void setupViewModel() {
        // (9.5) Remove the logging and the call to loadAllTasks, this is done in the ViewModel now
        // (9.6) Declare a ViewModel variable and initialize it by calling ViewModelProviders.of
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // (9.7) Observe the LiveData object in the ViewModel
        viewModel.getTasks().observe(this, new Observer<List<TaskEntity>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntity> tasks) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                adapter.setTasks(tasks);
            }
        });
    }
// (9.8) This method is not retrieving the tasks any more.
/*
    private void retrieveTasks() {
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        // (7.3) Fix compile issue by wrapping the return type with LiveData
        LiveData<List<TaskEntity>> tasks = db.tasksDao().loadAllTasks();
        // (7.5) Observe tasks and move the logic from runOnUiThread to onChanged
        tasks.observe(this, new Observer<List<TaskEntity>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntity> tasks) {
                Log.d(TAG, "Receiving database update from LiveData");
                adapter.setTasks(tasks);
            }
        });
    }
*/
/*
    // (5.5) Define method retrieveTasks
    // (7.4) Extract all this logic outside the Executor and remove the Executor
    private void retrieveTasks() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<TaskEntity> tasks = db.tasksDao().loadAllTasks();
                // We will be able to simplify this once we learn more
                // about Android Architecture Components
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setTasks(tasks);
                    }
                });
            }
        });
    }
*/
}
