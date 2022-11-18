package me.aj4real.jwizetreader.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class RandomAccessFileLoader extends FileLoader {

    private final RandomAccessFile file;

    public RandomAccessFileLoader(File in) throws FileNotFoundException {
        super(in);
        this.file = new RandomAccessFile(in, "r");
    }

    @Override
    public void dispose() throws IOException {
        file.close();
    }

    @Override
    public byte readByte() throws IOException {
        return file.readByte();
    }

    @Override
    public void setPosition(long pos) throws IOException {
        file.seek(pos);
    }

    @Override
    public long getPosition() throws IOException {
        return file.getFilePointer();
    }
}
