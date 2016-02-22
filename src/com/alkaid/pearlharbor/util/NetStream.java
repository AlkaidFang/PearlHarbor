package com.alkaid.pearlharbor.util;

public class NetStream
{
    private ByteBuffer mReadBuffer;
    private byte[] mReadBufferTemp;
    private ByteBuffer mWriteBuffer;

    private volatile boolean mPipeInIdle;
    private volatile boolean mPipeOutIdle;

    public NetStream(int bufferSize)
    {
        mReadBuffer = new ByteBuffer(bufferSize); // 主读数据区
        mReadBufferTemp = new byte[bufferSize / 2]; // 读缓存区
        mWriteBuffer = new ByteBuffer(bufferSize); // 主写数据区

        mPipeInIdle = true;
        mPipeOutIdle = true;
    }

    public byte[] asyncPipeIn()
    {
    	mPipeInIdle = false;
        return mReadBufferTemp;
    }

    public boolean asyncPipeInIdle()
    {
    	return mPipeInIdle;
    }

    public void FinishedIn(int length)
    {
        mReadBuffer.Push(mReadBufferTemp, length);
        mPipeInIdle = true;
    }

    public byte[] asyncPipeOut()
    {
        mPipeOutIdle = false;
        return mWriteBuffer.Buffer();
    }

    public boolean asyncPipeOutIdle()
    {
    	return mPipeOutIdle;
    }

    public void FinishedOut(int length)
    {
        mWriteBuffer.Pop(length);
        mPipeOutIdle = false;
    }

    public byte[] inStream()
    {
    	return mReadBuffer.Buffer();
    }

    public int inStreamLength()
    {
    	return mReadBuffer.DataSize();
    }

    public byte[] OutStream()
    {
    	return mWriteBuffer.Buffer();
    }

    public int OutStreamLength()
    {
        return mWriteBuffer.DataSize();
    }

    public void PushInStream(byte[] buffer)
    {
        mReadBuffer.Push(buffer);
    }
    
    public void PushInStream(byte[] buffer, int length)
    {
    	mReadBuffer.Push(buffer, length);
    }

    public void PopInStream(int length)
    {
        mReadBuffer.Pop(length);
    }

    public void PushOutStream(byte[] buffer)
    {
        mWriteBuffer.Push(buffer);
    }

    public void PopOutStream(int length)
    {
        mWriteBuffer.Pop(length);
    }

    public void Clear()
    {
        mReadBuffer.Clear();
        mWriteBuffer.Clear();
    }
}
