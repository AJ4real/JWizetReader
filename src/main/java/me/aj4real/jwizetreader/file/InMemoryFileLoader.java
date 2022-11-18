package me.aj4real.jwizetreader.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public final class InMemoryFileLoader extends FileLoader {

    WeakReference<byte[]> data;
    long pointer;
    public InMemoryFileLoader(File in) throws IOException {
        super(in);
        data = new WeakReference<>(new FileInputStream(in).readAllBytes());
    }

    @Override
    public void dispose() {
        data.enqueue();
        while(data.get() != null) {}
    }

    @Override
    public byte readByte() throws IOException {
        if(data.get() == null) throw new OutOfMemoryError("The JVM Garbage Collector has recycled this file loader because it was out of memory, Please use " + RandomAccessFileLoader.class.getCanonicalName() + " to load Wizet files.");
        return data.get()[(int) pointer++];
    }

    @Override
    public void setPosition(long pos) throws IOException {
        this.pointer = pos;
    }

    @Override
    public long getPosition() throws IOException {
        return this.pointer;
    }
}
