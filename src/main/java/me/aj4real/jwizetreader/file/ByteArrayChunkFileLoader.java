package me.aj4real.jwizetreader.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.WeakHashMap;

public final class ByteArrayChunkFileLoader extends FileLoader {

    public final int chunkSize;
    private final WeakHashMap<Integer, byte[]> map = new WeakHashMap<>();
    private final FileInputStream is;
    private long pointer = 0;

    public ByteArrayChunkFileLoader(File in, int chunkSize) throws IOException {
        super(in);
        this.is = new FileInputStream(in);
        this.chunkSize = chunkSize;
    }

    @Override
    public void dispose() throws IOException {
        map.clear();
        is.close();
    }

    @Override
    public byte readByte() throws IOException {
        int chunkNum = (int) (pointer / chunkSize);
        byte[] ret = map.computeIfAbsent(chunkNum, (i) -> {
            byte[] data = new byte[chunkSize];
            try {
                is.getChannel().position((long) i * chunkSize);
                is.read(data, 0, chunkSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        });
        byte b = ret[(int) (pointer % chunkSize)];
        pointer++;
        return b;
    }

    @Override
    public long getPosition() throws IOException {
        return this.pointer;
    }

    @Override
    public void setPosition(long pos) throws IOException {
        this.pointer = pos;
    }

}
