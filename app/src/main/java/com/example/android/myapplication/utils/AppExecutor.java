package com.example.android.myapplication.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {

    private final static Executor networkIO = Executors.newFixedThreadPool(3);

    private final static Executor mainThread = new MainThreadExecutor();

    private AppExecutor() {

    }

    public static Executor networkIO() {
        return networkIO;
    }

    public static Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
