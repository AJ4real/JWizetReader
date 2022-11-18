package me.aj4real.jwizetreader.file;

import me.aj4real.jwizetreader.Crypto;
import me.aj4real.jwizetreader.WizetFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WizetFileInputStream {

    private final WizetFile file;
    private final FileLoader loader;
    private byte[] key;
    private int hash;
    private boolean noEncryption = false;
    public WizetFileInputStream(WizetFile in, FileLoader loader) {
        this.file = in;
        this.loader = loader;
    }
    public void setKey(byte[] key) {
        this.key = key;
        for (int i = 0; i < key.length; i++) {
            if (key[i] == 0) {
                noEncryption = true;
                break;
            }
        }
    }
    public void setHash(int key) {
        this.hash = key;
    }

    public void dispose() throws IOException {
        loader.dispose();
    }
    public long getPosition() throws IOException {
        return loader.getPosition();
    }
    public void setPosition(long pos) throws IOException {
        loader.setPosition(pos);
    }
    public byte readByte() throws IOException {
        return loader.readByte();
    }

    public byte[] readBytes(long num) throws IOException {
        byte[] ret = new byte[(int) num];
        for (int x = 0; x < num; x++) {
            ret[x] = readByte();
        }
        return ret;
    }
    public String readString(long size) throws IOException {
        char[] ret = new char[(int) size];
        for (int x = 0; x < size; x++) {
            ret[x] = (char) readByte();
        }
        return String.valueOf(ret);
    }
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }
    public float readFloat() throws IOException {
        return ByteBuffer.wrap(readBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    public short readShort() throws IOException {
        int byte1, byte2;
        byte1 = readByte();
        byte2 = readByte();
        return (short) ((byte2 << 8) + byte1);
    }
    public int readUnsignedShort() throws IOException {
        short current = readShort();
        return current & 0xFF;
    }
    public int readCompressedInt() throws IOException {
        byte b = readByte();
        return b == Byte.MIN_VALUE ? readInt() : b;
    }
    public long readCompressedLong() throws IOException {
        byte b = readByte();
        return b == Byte.MIN_VALUE ? readLong() : b;
    }
    public long readLong() throws IOException {
        long ret = 0;
        for (int i = 0; i < 8; i++) {
            ret |= ((long) (readByte() & 0xff) << (i * 8));
        }
        return ret;
    }
    public int readInt() throws IOException {
        int ret = 0;
        for (int i = 0; i < 4; i++) {
            ret |= ((readByte() & 0xff) << (i * 8));
        }
        return ret;
    }

    public final String readNullTerminatedString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte b = 1;
        while (b != 0) {
            b = readByte();
            baos.write(b);
        }
        byte[] buf = baos.toByteArray();
        char[] chrBuf = new char[buf.length];
        for (int x = 0; x < buf.length; x++) {
            chrBuf[x] = (char) buf[x];
        }
        return String.valueOf(chrBuf);
    }
    public String readStringBlock(long offset) throws IOException {
        byte b = readByte();

        switch(b) {
            case 0:
            case 0x73: {
                return readString();
            }
            case 1:
            case 0x1B: {
                return readStringAtOffset(offset + readInt());
            }
            default:
                return null;
        }
    }
    public String readStringAtOffset(long offset) throws IOException {
        long rememberPos = getPosition();
        setPosition(offset);
        String ret = readString();
        setPosition(rememberPos);
        return ret;
    }
    public String readString() throws IOException {
        byte smallLength = readByte();
        if (smallLength == 0) {
            return "";
        }
        long length;
        StringBuilder retString = new StringBuilder();
        if (smallLength > 0) {
            //
            short mask = (short) 0xAAAA;
            length = smallLength == Byte.MAX_VALUE ? readInt() : smallLength;
            if (length <= 0) {
                return "";
            }
            if (noEncryption) {
                for (int i = 0; i < length; i++) {
                    short encryptedChar = readShort();
                    encryptedChar ^= mask;
                    retString.append((char)encryptedChar);
                    mask++;
                }
            } else {
                for (int i = 0; i < length; i++) {
                    short encryptedChar = readShort();
                    encryptedChar ^= mask;
                    encryptedChar ^= (short)((key[i * 2 + 1] << 8) + key[i * 2]);
                    retString.append((char)encryptedChar);
                    mask++;
                }
            }
        } else {
            byte mask = (byte) 0xAA;
            if (smallLength == Byte.MIN_VALUE) {
                length = readInt();
            } else {
                length = -smallLength;
            }
            if (length <= 0) {
                return "";
            }
            if (noEncryption) {
                for (int i = 0; i < length; i++) {
                    byte encryptedChar = readByte();
                    encryptedChar ^= mask;
                    retString.append((char)encryptedChar);
                    mask++;
                }
            } else {
                for (int i = 0; i < length; i++) {
                    byte encryptedChar = readByte();
                    encryptedChar ^= mask;
//                    encryptedChar ^= key[i];
                    retString.append((char)encryptedChar);
                    mask++;
                }
            }
        }
        return retString.toString();
    }
    public int readOffset() throws IOException {
        int off = (int) (0xFFFFFFFF & getPosition());
        off -= file.getHeader().start();
        off = ~off;
        off *= hash;
        off -= Crypto.wzOffsetConstant;
        off = (off << ((byte) (off & 0x1F))) | (off >>> (32 - ((byte) (off & 0x1F))));
        off ^= readInt();
        off &= 0xFFFFFFFF;
        off += file.getHeader().start() * 2;
        return off;
    }
    /*

82
4294967273
4293726676
2815480423
3899208659
3899208659
299
419

     */


}
