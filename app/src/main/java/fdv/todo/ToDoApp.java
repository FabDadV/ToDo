package fdv.todo;

import android.app.Application;
import com.facebook.stetho.Stetho;

public class ToDoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
