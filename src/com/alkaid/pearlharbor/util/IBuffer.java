package com.alkaid.pearlharbor.util;

public interface IBuffer<T>
{
    // add all
    void Push(T[] data);
    // add element
    void Push(T[] data, int length);

    // pop all
    T[] Pop();

    // pop length
    T[] Pop(int length);

    // data at index
    T Get(int index);

    // clear
    void Clear();

    // get area
    T[] Buffer();

    // data size
    int DataSize();

    // size
    int Size();
}
