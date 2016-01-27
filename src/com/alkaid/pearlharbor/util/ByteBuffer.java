package com.alkaid.pearlharbor.util;

public class ByteBuffer
{
    private int _offset = 0;
    private byte[] _data = null;
    private Object _lock = null;

    public ByteBuffer(int length)
    {
        _offset = 0;
        _data = new byte[length];
        _lock = new Object();
    }

    private void Resize(int times)
    {
        byte[] swi = _data;
        _data = new byte[swi.length * times];
        System.arraycopy(swi, 0, _data, 0, swi.length);
        swi = null;
    }

    public void Push(byte[] data)
    {
        Push(data, data.length);
    }

    public void Push(byte[] data, int length)
    {
        if (length <= 0) return;

        synchronized(_lock)
        {
            if (_offset + length > _data.length)
            {
                int times = (_offset + length) / _data.length + 1;
                Resize(times);
            }
            System.arraycopy(data, 0, _data, _offset, length);
            _offset += length;
        }
    }

    public byte[] Pop()
    {
        return Pop(_offset);
    }

    public byte[] Pop(int length)
    {
        if (length > _offset || length <= 0) return null;

        byte[] ret = new byte[length];
        synchronized(_lock)
        {
        	System.arraycopy(_data, 0, ret, 0, length);

        	System.arraycopy(_data, length, _data, 0, _offset - length);
        	
            _offset -= length;
        }

        return ret;
    }

    public byte Get(int index)
    {
        return index < _offset ? _data[index] : 0;
    }

    public void Clear()
    {
        _offset = 0;
    }

    public byte[] Buffer()
    {
        /*byte[] ret = new byte[_offset];
        Array.Copy(_data, ret, _offset);
        return ret;*/
        return _data;
    }

    public int DataSize()
    {
        return _offset;
    }

    public int Size()
    {
        return _data.length;
    }
}
