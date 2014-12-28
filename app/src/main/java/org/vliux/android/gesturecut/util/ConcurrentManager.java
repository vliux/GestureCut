package org.vliux.android.gesturecut.util;

import android.os.AsyncTask;
import android.os.Looper;

import org.vliux.android.gesturecut.AppConstant;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by vliux on 8/7/14.
 */
public class ConcurrentManager {
    private static ExecutorService sExecutorService = Executors.newFixedThreadPool(AppConstant.CONCURRENT_POOL_SIZE);

    public static Future submitRunnable(Runnable runnable){
        return sExecutorService.submit(runnable);
    }

    public static IJob submitJob(IBizCallback bizCallback,
                                 IUiCallback uiCallback, Object... params){
        if(Looper.getMainLooper() != Looper.myLooper()){
            throw new IllegalStateException("submitJob() must be invoked on main thread");
        }

        JobAsyncTask jobAsyncTask = new JobAsyncTask(bizCallback, uiCallback);
        return (IJob)jobAsyncTask.executeOnExecutor(sExecutorService, params);
    }

    public static interface IUiCallback<Result> {
        public void onPreExecute();
        public void onPostExecute(Result result);
        public void onPregressUpdate(int percent);
        public void onCancelled();
    }

    public static interface IBizCallback<Result> {
        public Result onBusinessLogicAsync(IJob job, Object... params);
    }

    public static interface IJob{
        public boolean isJobCancelled();
        public void publishJobProgress(int percent);
        public void cancelJob();
    }

    /**
     * AsyncTask-based job.
     */
    private static class JobAsyncTask<Result> extends AsyncTask<Object, Integer, Result> implements IJob {
        private WeakReference<IUiCallback<Result>> mUiCallbackRef;
        private WeakReference<IBizCallback<Result>> mBizCallbackRef;

        public JobAsyncTask(IBizCallback<Result> bizCallback, IUiCallback<Result> uiCallback){
            mBizCallbackRef = new WeakReference<IBizCallback<Result>>(bizCallback);
            mUiCallbackRef = new WeakReference<IUiCallback<Result>>(uiCallback);
        }

        @Override
        public boolean isJobCancelled() {
            return isCancelled();
        }

        @Override
        public void cancelJob() {
            cancel(true);
        }

        @Override
        public void publishJobProgress(int percent) {
            publishProgress(percent);
        }

        @Override
        protected Result doInBackground(Object... params) {
            IBizCallback<Result> bizCallback = mBizCallbackRef.get();
            if(null != bizCallback) {
                return bizCallback.onBusinessLogicAsync(this, params);
            }else{
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            IUiCallback<Result> uiCallback = mUiCallbackRef.get();
            if(null != uiCallback) {
                uiCallback.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            IUiCallback<Result> uiCallback = mUiCallbackRef.get();
            if(null != uiCallback) {
                uiCallback.onPostExecute(result);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            IUiCallback<Result> uiCallback = mUiCallbackRef.get();
            if(null != uiCallback) {
                uiCallback.onPregressUpdate(values[0]);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            IUiCallback<Result> uiCallback = mUiCallbackRef.get();
            if(null != uiCallback) {
                uiCallback.onCancelled();
            }
        }

        @Override
        protected void onCancelled(Result result) {
            super.onCancelled(result);
            IUiCallback<Result> uiCallback = mUiCallbackRef.get();
            if(null != uiCallback) {
                uiCallback.onCancelled();
            }
        }
    }
}
