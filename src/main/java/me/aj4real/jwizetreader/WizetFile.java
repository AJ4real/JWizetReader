package me.aj4real.jwizetreader;

import me.aj4real.jwizetreader.file.WizetDirectoryDataEntry;
import me.aj4real.jwizetreader.file.WizetFileInputStream;
import me.aj4real.jwizetreader.loader.ByteArrayChunkFileLoader;
import me.aj4real.jwizetreader.loader.FileLoader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class WizetFile {
    byte[] ivKey;
    int mapleVersion;
    private boolean x64 = false;
    private boolean x64withHeader = false;
    private final WizetFileInputStream is;
    private final WizetFileHeader header;
    private final WizetDirectoryDataEntry root;
    private final File file;
    public final AtomicInteger COUNTER = new AtomicInteger(0); //TODO: for testing
    public WizetFile(File in) throws IOException {
        this(in, new ByteArrayChunkFileLoader(in));
    }
    public WizetFile(File in, FileLoader loader) throws IOException {
        this.file = in;
        is = new WizetFileInputStream(this, loader);
        String pkg1 = is.readString(4);
        long size = is.readLong();
        int start = is.readInt();
        String copyright = is.readNullTerminatedString();
        if(start - is.getPosition() < 0) {
            throw new MalformedWizetFileException("Attempted to load " + in.getAbsolutePath() + ", Found invalid header.", this);
        }
        this.header = new WizetFileHeader(in.getName(), this, pkg1, size, start, copyright);
        int encVer = is.readUnsignedShort();
        if(encVer > 0xff) {
            this.x64 = true;
        } else if(encVer == 0x80) {
            if(header.size() >= 5) {
                is.setPosition(header.start());
                long propCount = is.readCompressedInt();
                if(propCount > 0 && (propCount & 0xff) == 0 && propCount <= 0xffff) {
                    x64 = true;
                }
            }
        } else if(encVer == 33) {
            x64 = true;
            x64withHeader = true;
        }
        if(x64) {
            encVer = Crypto.wzVersionHeader64bitStart;
        }
        mapleVersion = (x64 && !x64withHeader) ? Crypto.wzVersionHeader64bitStart : findVersion(encVer, -1);
        int j = x64 ? Crypto.wzVersionHeader64bitStart : 0;
        for (int i = j; i < Short.MAX_VALUE; i++) {
            short mFileVer = (short) i;
            int mVersionHash = findVersionHash(encVer, mFileVer);
            if(mVersionHash == 0) {
                continue;
            }
//            WizetDirectoryDataEntry testDirectory;
//            long pos = is.getPosition();
//            try {
//                testDirectory = new WizetDirectoryDataEntry(is, in.getName(), null, 0, 0, is.getPosition(), this);
//                testDirectory.parse();
//            } catch (Exception err) {
//                is.setPosition(pos);
//                continue;
//            }
            is.setHash(mVersionHash);
            break;
        }
        if(x64) {
            ivKey = new byte[4];
            is.setKey(new byte[65535]);
//            is.setPosition(header.start());
        } else {
            ivKey = Crypto.wzGmsIv;
            is.setKey(new byte[65535]);
        }
        root = new WizetDirectoryDataEntry(is, in.getName(), null, size, 0, is.getPosition(), this);
    }
    public boolean isX64() {
        return this.x64;
    }
    public int getMapleVersion() {
        return this.mapleVersion;
    }
    public byte[] getKey() {
        return this.ivKey;
    }
    public WizetFileHeader getHeader() {
        return this.header;
    }
    public File getFile() {
        return this.file;
    }
    public WizetDirectoryDataEntry getRoot() {
        return this.root;
    }
    public WizetFile parse() throws IOException {
        root.parse();
        return this;
    }

    private int findVersion(int encver, int start_version) {
        int sum;
        String versionStr;
        int a = 0, b = 0, c = 0, d = 0, e = 0, i = start_version, l = 0;
        do {
            i++;
            sum = 0;
            versionStr = Integer.toString(i);
            l = versionStr.length();
            for (int j = 0; j < l; j++) {
                sum <<= 5;
                sum += (int) versionStr.charAt(j) + 1;
            }
            a = (sum >> 24) & 0xFF;
            b = (sum >> 16) & 0xFF;
            c = (sum >> 8) & 0xFF;
            d = sum & 0xFF;
            e = 0xFF ^ a ^ b ^ c ^ d;
        } while (e != encver);

        return i;
    }
    private int findVersionHash(int encver, int realver) {
        int EncryptedVersionNumber = encver;
        int VersionNumber = realver;
        int VersionHash = 0;
        int DecryptedVersionNumber;
        String VersionNumberStr;
        int a, b, c, d, l;
        VersionNumberStr = String.valueOf(VersionNumber);
        l = VersionNumberStr.length();
        for (int i = 0; i < l; i++) {
            VersionHash = (32 * VersionHash) + VersionNumberStr.charAt(i) + 1;
        }
        if (encver == Crypto.wzVersionHeader64bitStart) {
            return VersionHash;
        }
        a = (VersionHash >> 24) & 0xFF;
        b = (VersionHash >> 16) & 0xFF;
        c = (VersionHash >> 8) & 0xFF;
        d = VersionHash & 0xFF;
        DecryptedVersionNumber = (0xff ^ a ^ b ^ c ^ d);
        int ret = EncryptedVersionNumber == DecryptedVersionNumber ? VersionHash : 0;
        return ret;
    }
}
