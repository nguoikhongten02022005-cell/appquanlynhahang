package com.example.quanlynhahang.helper;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseTaskRunner {

    public interface Task<T> {
        T run();
    }

    public interface Callback<T> {
        void onComplete(T result);
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public <T> void execute(Task<T> task, Callback<T> callback) {
        executorService.execute(() -> {
            T result = task.run();
            mainHandler.post(() -> callback.onComplete(result));
        });
    }
}
