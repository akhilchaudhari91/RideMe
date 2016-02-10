package ridemecabs.Exception;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by akhil on 02-01-2016.
 */
public class BaseAppCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {
                        //Do your own error handling here

                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            ); //Delegates to Android's error handling
                        else
                            System.exit(2); //Prevents the service/app from freezing
                    }
                });
    }
}
