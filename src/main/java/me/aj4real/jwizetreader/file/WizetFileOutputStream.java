package me.aj4real.jwizetreader.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WizetFileOutputStream {

    private RandomAccessFile buffer;
    private byte[] key;
    private int hash;
    private boolean noEncryption;
    public WizetFileOutputStream(File in, boolean noEncryption, byte[] key, int hash) throws IOException {
        assert in != null;
        assert key != null;
        this.buffer = new RandomAccessFile(in, "r");
        this.key = key;
        this.noEncryption = noEncryption;
        this.hash = hash;
    }
    public void dispose() throws IOException {
        this.buffer.close();
    }
    public long getPosition() {
        try {
            return buffer.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPosition(long pos) {
        try {
            buffer.seek(pos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeBytes(byte[] input) {
    }
    public void writeNullTerminatedString(String input) {
    }
    public void writeDouble(double input) {
    }
    public void writeFloat(float input) {
    }
    public void writeChar(char input) {
    }
    public void writeShort(short input) {
    }
    public void writeUnsignedShort(short input) {
    }
    public void writeCompressedInt(int input) {
    }
    public void writeCompressedLong(long input) {
    }
    public void writeByte(byte input) {
    }
    public void writeLong(long input) {
    }
    public void writeStringBlock(String input) {
    }
    public void writeString(String input) {
    }
    public void writeEcryptedString(String input) {
    }
    public void writeInt(int input) {
    }
    public void writeOffset(long input) {
    }
}
