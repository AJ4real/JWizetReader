package me.aj4real.jwizetreader.file;

import me.aj4real.jwizetreader.*;
import me.aj4real.jwizetreader.propertyTypes.WizetPropertyBlock;

import java.util.Map;

public class WizetImageDataEntry implements WizetContainer {
    protected final WizetFileInputStream is;
    protected final long offset;
    private final WizetFile parentFile;
    private boolean parsed = false;
    private final String name;
    private final WizetContainer parent;
    private final WizetPropertyBlock root;
    public WizetImageDataEntry(WizetFileInputStream is, String name, WizetContainer parent, long offset, WizetFile parentFile) {
        this.is = is;
        this.name = name;
        this.parent = parent;
        this.offset = offset;
        this.parentFile = parentFile;
        this.root = new WizetPropertyBlock(is, offset, name, parent, parentFile);
    }
    public boolean isParsed() {
        return this.parsed;
    }
    @Override
    public PropertyType type() {
        return PropertyType.IMAGE;
    }
    public Map<String, WizetObject> getProperties() {
        return root.getProperties();
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
        if(getName().endsWith(".lua")) return; //TODO
        is.setPosition(offset);
        byte byteIdentifier = is.readByte();
        String strIdentifier = is.readString();
        int intIdentifier = is.readShort();
        if(!strIdentifier.equalsIgnoreCase("Property") || byteIdentifier != 0x73 || intIdentifier != 0) {
            if(!strIdentifier.equalsIgnoreCase("Property")) {
                throw new MalformedWizetFileException("Invalid Image entry; Needs 'Property', Found '" + (strIdentifier.length() > 16 ? strIdentifier.substring(0, "Property".length()) : strIdentifier) + "'", parentFile);
            }
            if(byteIdentifier != 0x73) {
                throw new MalformedWizetFileException("Unsupported Image entry type, Needs '115', Found " + byteIdentifier, parentFile);
            }
            if(intIdentifier != 0) {
                throw new MalformedWizetFileException("Unsupported Image entry identifier, Needs '0', Found " + intIdentifier, parentFile);
            }
        }
        root.parse();
        this.parsed = true;
    }
}
