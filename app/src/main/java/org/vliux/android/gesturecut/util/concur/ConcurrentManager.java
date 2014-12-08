package org.vliux.android.gesturecut.util.concur;

import android.os.AsyncTask;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by vliux on 8/7/14.
 */
public class ConcurrentManager {
    private static ExecutorService sExecutorService = Executors.newFixedThreadPool(3);

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

    public static abstract class IUiCallback<Result> {
        public abstract void onPreExecute();
        public abstract void onPostExecute(Result result);
        public abstract void onPregressUpdate(int percent);
        public abstract void onCancelled();

        private IJob mJob;
        void setJob(IJob job){
            mJob = job;
        }

        public IJob getJob() {
            return mJob;
        }
    }

    public static interface IBizCallback<Result> {
        public Result onBusinessLogicAsync(IJob job, Object... params);
    }

    public static interface IJob{
        public boolean isJobCancelled();
        public void publishJobProgress(int percent);
        public void cancelJob();
    }

    private static class JobAsyncTask<Result> extends AsyncTask<Object, Integer, Result> implements IJob {
        private IUiCallback<Result> mUiCallbackRef;
        private IBizCallback<Result> mBizCallbackRef;

        public JobAsyncTask(IBizCallback<Result> bizCallback, IUiCallback<Result> uiCallback){
            uiCallback.setJob(this);
            mBizCallbackRef = bizCallback;
            mUiCallbackRef = uiCallback;
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
            if(null != mBizCallbackRef) {
                return mBizCallbackRef.onBusinessLogicAsync(this, params);
            }else{
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(null != mUiCallbackRef) {
                mUiCallbackRef.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if(null != mUiCallbackRef) {
                mUiCallbackRef.onPostExecute(result);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(null != mUiCallbackRef) {
                mUiCallbackRef.onPregressUpdate(values[0]);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(null != mUiCallbackRef) {
                mUiCallbackRef.onCancelled();
            }
        }

        @Override
        protected void onCancelled(Result result) {
            super.onCancelled(result);
            if(null != mUiCallbackRef) {
                mUiCallbackRef.onCancelled();
            }
        }
    }
}
