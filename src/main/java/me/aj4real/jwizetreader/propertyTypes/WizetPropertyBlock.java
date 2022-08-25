package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.file.WizetFileInputStream;
import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.WizetContainer;
import me.aj4real.jwizetreader.WizetFile;
import me.aj4real.jwizetreader.WizetObject;
import me.aj4real.jwizetreader.MalformedWizetFileException;

import java.util.HashMap;
import java.util.Map;

public class WizetPropertyBlock implements WizetContainer {
    private final Map<String, WizetObject> children = new HashMap<>();
    public final WizetFileInputStream is;
    public long offset;
    protected final String name;
    protected final WizetObject parent;
    protected final WizetFile parentFile;
    boolean parsed = false;
    public WizetPropertyBlock(WizetFileInputStream is, long offset, String name, WizetObject parent, WizetFile parentFile) {
        this.is = is;
        this.offset = offset;
        this.name = name;
        this.parent = parent;
        this.parentFile = parentFile;
        parentFile.COUNTER.incrementAndGet();
    }
    public boolean isParsed() {
        return this.parsed;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public PropertyType type() {
        return PropertyType.BLOCK;
    }
    @Override
    public WizetObject getParent() {
        return this.parent;
    }
    public Map<String, WizetObject> getProperties() {
        return this.children;
    }

    @Override
    public void parse() throws MalformedWizetFileException {
        if(!isParsed()) {
            this.parsed = true;
            parsePropertyList(is, offset);
        }
    }
    protected void parsePropertyList(WizetFileInputStream is, long offset) throws MalformedWizetFileException {
        long entryCount = is.readCompressedInt();
        for (int i = 0; i < entryCount; i++) {
            String name = is.readStringBlock(offset);
            byte type = is.readByte();
            WizetObject value = null;
            try {
                switch(type) {
                    case 9: {//Extended
                        long eob = is.readInt() + is.getPosition();
                        value = parseBlock(is, name, offset, this);
                        is.setPosition(eob);
                        break;
                    }
                    case 20: {//Compressed Long
                        value = new WizetLong(name, this, is.readCompressedLong());
                        break;
                    }
                    case 2:
                    case 11: {//UShort
                        value = new WizetShort(name, this, is.readShort());
                        break;
                    }
                    case 3:
                    case 19: {//UInt
                        value = new WizetInteger(name, this, is.readCompressedInt());
                        break;
                    }
                    case 8: {//String
                        value = new WizetString(name, this, is.readStringBlock(offset));
                        break;
                    }
                    case 4: {//Float
                        value = new WizetFloat(name, this, is.readByte() == -128 ? is.readFloat() : 0f);
                        break;
                    }
                    case 5: {//Double
                        value = new WizetDouble(name, this, is.readDouble());
                        break;
                    }
                    case 0: {//Null
                        value = new WizetNull(name, this);
                        break;
                    }
                    default:
                        throw new MalformedWizetFileException(getPath() + ": Unsupported Image Property type " + type + " with name " + name, parentFile);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new MalformedWizetFileException(getPath() + ": Failed to get property for " + name, e, parentFile);
            }
            getProperties().put(name, value);
            parentFile.COUNTER.incrementAndGet();
        }
    }
    protected WizetObject parseBlock(WizetFileInputStream is, String imgName, long offset, WizetObject parent) throws MalformedWizetFileException {
        String name = "";
        byte nameType = is.readByte();
        switch(nameType) {
            case 0x1B:
                name = is.readStringAtOffset(offset + is.readInt());
                break;
            case 0x73:
                name = is.readString();
                break;
            default:
                throw new MalformedWizetFileException("Failed to get Image Property block name, Needed " + 0x1B + " or " + 0x73 + ", Found " + nameType, parentFile);
        }
        switch(name) {
            case "Property": {
                WizetPropertyBlock sub = new WizetPropertyBlock(is, offset, imgName, parent, parentFile);
                is.setPosition(is.getPosition() + 2);
                sub.parse();
                return sub;
            }
            case "Canvas": {
                is.setPosition(is.getPosition()+1);
                WizetCanvas sub = new WizetCanvas(is, offset, imgName, parent, parentFile);
                long pos = is.getPosition();
                sub.parse();
                is.setPosition(pos);
                return sub;
            }
            case "Shape2D#Vector2D": {
                long x = is.readCompressedInt();
                long y = is.readCompressedInt();
                WizetVector sub = new WizetVector(imgName, parent, x, y);
                return sub;
            }
            case "Shape2D#Convex2D": {
                WizetConvex sub = new WizetConvex(is, is.getPosition(), imgName, parent, parentFile);
                return sub;
            }
            case "Sound_DX8": {
                long currentOffset = is.getPosition();
                long len = is.readCompressedInt();
                long len2 = is.readCompressedInt();
                boolean readMp3 = false;
                if(readMp3){
                    byte[] mp3 = is.readBytes(len);
                } else {
                    is.setPosition(is.getPosition() + len);
                }
                WizetSound sub = new WizetSound(imgName, parent, currentOffset, len);
                return sub;
            }
            case "UOL": {
                is.setPosition(is.getPosition() + 1);
                byte b = is.readByte();
                switch(b) {
                    case 0: {
                        return new WizetUOL(imgName, parent, is.readString());
                    }
                    case 1: {
                        return new WizetUOL(imgName, parent, is.readStringAtOffset(offset + is.readInt()));
                    }
                    default: {
                        throw new MalformedWizetFileException("Unsupported UOL type: " + b, parentFile);
                    }
                }
            }
            default:
                throw new MalformedWizetFileException("Unknown PropertyBlock type: " + name, parentFile);
        }
    }
}
