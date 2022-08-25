package me.aj4real.jwizetreader.file;

import me.aj4real.jwizetreader.SharedConstants;
import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.WizetContainer;
import me.aj4real.jwizetreader.WizetFile;
import me.aj4real.jwizetreader.WizetObject;
import me.aj4real.jwizetreader.MalformedWizetFileException;

import java.util.*;

public class WizetDirectoryDataEntry implements WizetContainer {
    private final WizetFileInputStream is;
    private final long offset;
    private final WizetFile parentFile;
    private final WizetContainer parent;
    private final String name;
    private boolean parsed = false;
    public WizetDirectoryDataEntry(WizetFileInputStream is, String name, WizetContainer parent, long size, long checksum, long offset, WizetFile parentFile) {
        this.is = is;
        this.name = name;
        this.parent = parent;
        this.offset = offset;
        this.parentFile = parentFile;
    }
    private final Map<String, WizetObject> children = new HashMap<>();
    public Map<String, WizetObject> getProperties() {
        return this.children;
    }
    public void align() {
        is.setPosition(offset);
    }
    @Override
    public PropertyType type() {
        return PropertyType.DIRECTORY;
    }
    public boolean isParsed() {
        return this.parsed;
    }
    public String getName() {
        return this.name;
    }
    public WizetContainer getParent() {
        return this.parent;
    }
    @Override
    public void parse() throws MalformedWizetFileException {
        if(isParsed()) return;
        parentFile.COUNTER.incrementAndGet();
        is.setPosition(offset);
        long entries = is.readCompressedInt();
        for (int i = 0; i < entries; i++) {
            byte type = is.readByte();
            String name = null;
            int size, checksum, offset;
            long rememberPos = 0;
            switch (type) {
                case 1://TODO
                    is.readInt();
                    is.readShort();
                    is.readOffset();
                    continue;
                case 2://File w/Offset
                    int stringOffset = is.readInt();
                    rememberPos = is.getPosition();
                    is.setPosition(parentFile.getHeader().start() + stringOffset);
                    type = is.readByte();
                    name = is.readString();
                    break;
                case 3://Directory
                case 4://File
                    name = is.readString();
                    rememberPos = is.getPosition();
                    break;
                default:
                    throw new MalformedWizetFileException("Unsupported Data Entry type, Found " + type + ", Needed 1-4", parentFile);
            }
            is.setPosition(rememberPos);
            size = is.readCompressedInt();
            checksum = is.readCompressedInt();
            offset = is.readOffset();
            if(type == 3) {
                WizetDirectoryDataEntry subDir = new WizetDirectoryDataEntry(is, name, this, size, checksum, offset, parentFile);
                long rememberPos2 = is.getPosition();
                is.setPosition(offset);
                subDir.parse();
                is.setPosition(rememberPos2);
                getProperties().put(name, subDir);
            } else {
                WizetImageDataEntry file = new WizetImageDataEntry(is, name, this, offset, parentFile);
                long rememberPos2 = is.getPosition();
                if(SharedConstants.PREPARSE_ALL_CONTAINERS) file.parse();
                getProperties().put(name, file);
                is.setPosition(rememberPos2);
            }
        }
        parsed = true;
    }
    public long getOffset() {
        return this.offset;
    }

}
