package me.aj4real.jwizetreader.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.WeakHashMap;

public class ByteArrayChunkFileLoader extends FileLoader {

    public static int CHUNK_SIZE = 16384;
    private WeakHashMap<Integer, byte[]> map = new WeakHashMap<>();
    private final FileInputStream is;
    private long pointer = 0;
    private long size;

    public ByteArrayChunkFileLoader(File in) throws IOException {
        super(in);
        this.is = new FileInputStream(in);
        this.size = is.available();
    }

    @Override
    public void dispose() throws IOException {
        map.clear();
        is.close();
    }

    @Override
    public byte readByte() throws IOException {
        int chunkNum = (int) (pointer / CHUNK_SIZE);
        byte[] ret = map.computeIfAbsent(chunkNum, (i) -> {
            byte[] data = new byte[CHUNK_SIZE];
            try {
                is.getChannel().position((long) i * CHUNK_SIZE);
                is.read(data, 0, CHUNK_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        });
        byte b = ret[(int) (pointer % CHUNK_SIZE)];
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
