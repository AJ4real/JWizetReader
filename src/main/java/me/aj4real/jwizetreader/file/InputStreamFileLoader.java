package me.aj4real.jwizetreader.file;

import java.io.*;

public final class InputStreamFileLoader extends FileLoader {
    private FileInputStream is;
    public InputStreamFileLoader(File in) throws IOException {
        super(in);
        this.is = new FileInputStream(in);
    }

    @Override
    public void dispose() throws IOException {
        is.close();
        is = null;
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) is.read();
    }

    @Override
    public void setPosition(long pos) throws IOException {
        this.is.getChannel().position(pos);
    }

    @Override
    public long getPosition() throws IOException {
        return is.getChannel().position();
    }
}
