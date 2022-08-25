package me.aj4real.jwizetreader.loader;

import java.io.File;
import java.io.IOException;

public abstract class FileLoader {
    protected final File in;
    public FileLoader(File in) {
        this.in = in;
    }
    public abstract void dispose() throws IOException;
    public abstract byte readByte() throws IOException ;
    public abstract void setPosition(long pos) throws IOException ;
    public abstract long getPosition() throws IOException ;
}
