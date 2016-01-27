package com.alkaid.pearlharbor.util;

import java.lang.reflect.Method;

public class AsyncThread
{
	public interface ThreadCallback
	{
		void onThreadLogicContext(AsyncThread thread);

		void onThreadFinishedContext();
		
	}
	
    public enum ThreadStatus
    {
        NONE,
        START,
        WORKING,
        STOP,
        FINISHED,
    }

    private volatile ThreadStatus mStatus; // Cause there only one method to change this value, no need to lock.
    private ThreadCallback mThreadCallback;
    //private Callback<AsyncThread> mContext;
    //private Callback mFinishedContext;
    private Thread mThread;
    private Object mExtraData;
    private boolean mAsyncFinished;

    public AsyncThread(ThreadCallback cb)
    {
        mStatus = ThreadStatus.NONE;
        mThreadCallback = cb;
        mThread = new Thread(new Runnable()
        {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ContextMask();
			}
        });
        mExtraData = null;
        mAsyncFinished = true;
    }

    public AsyncThread(ThreadCallback cb, Object extraData)
    {
        mStatus = ThreadStatus.NONE;
        mThreadCallback = cb;
        mThread = new Thread(new Runnable()
        {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ContextMask();
			}
        });
        mExtraData = extraData;
        mAsyncFinished = true;
    }

    /**
     * This function may cause a little perfermance problem.
     * */
    private void ContextMask()
    {
        mStatus = ThreadStatus.WORKING;
        mThreadCallback.onThreadLogicContext(this);

        if (mStatus == ThreadStatus.STOP)
        {
        	if (mAsyncFinished)
        	{
        		mThreadCallback.onThreadFinishedContext();
        	}

            mStatus = ThreadStatus.FINISHED;
        }
    }

    private void Release()
    {
    	mThreadCallback = null;
        mThread = null;
        mExtraData = null;
        mAsyncFinished = true;
    }

    public boolean Start()
    {
        if (null != mThread)
        {
            mStatus = ThreadStatus.START;
            mThread.start();

            return true;
        }

        return false;
    }

    /**
     * cb will be called in async-thread
     * */
    public void Stop()
    {
        if (IsWorking())
        {
            mStatus = ThreadStatus.STOP;
        }
    }

    /**
     * cb will be called in sync-thread
     * */
    public void SyncStop()
    {
        if (IsWorking())
        {
        	mAsyncFinished = false;
            mStatus = ThreadStatus.STOP;

            while (mStatus != ThreadStatus.FINISHED) ;
            mThreadCallback.onThreadFinishedContext();
        }
    }

    public boolean IsWorking()
    {
        return mStatus == ThreadStatus.WORKING;
    }

    public long GetThreadId()
    {
        return null == mThread ? -1 : mThread.getId();
    }

    public void SetExtraData(Object data)
    {
        mExtraData = data;
    }

    public Object GetExtraData()
    {
        return mExtraData;
    }
}