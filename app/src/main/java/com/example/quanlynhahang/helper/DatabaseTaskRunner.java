package com.example.quanlynhahang.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseTaskRunner {

    private static final String TAG = "DatabaseTaskRunner";

    public interface Task<T> {
        T run();
    }

    public interface Callback<T> {
        void onComplete(T result);
    }

    public interface ErrorCallback {
        void onError(Exception e);
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    public <T> void execute(Task<T> task, Callback<T> callback) {
        execute(task, callback, null);
    }

    public <T> void execute(Task<T> task, Callback<T> callback, ErrorCallback errorCallback) {
        if (isShutdown.get()) {
            Log.w(TAG, "ExecutorService already shut down, skipping task");
            return;
        }

        executorService.execute(() -> {
            if (isShutdown.get()) {
                return;
            }

            try {
                T result = task.run();
                final T finalResult = result;
                mainHandler.post(() -> {
                    if (!isShutdown.get() && callback != null) {
                        callback.onComplete(finalResult);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error executing task", e);
                if (errorCallback != null) {
                    final Exception finalError = e;
                    mainHandler.post(() -> {
                        if (!isShutdown.get()) {
                            errorCallback.onError(finalError);
                        }
                    });
                }
            }
        });
    }

    public void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            executorService.shutdown();
            Log.i(TAG, "ExecutorService shut down");
        }
    }

    public boolean isShutdown() {
        return isShutdown.get();
    }
}