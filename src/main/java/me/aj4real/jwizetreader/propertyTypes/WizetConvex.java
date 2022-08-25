package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.MalformedWizetFileException;
import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.WizetObject;
import me.aj4real.jwizetreader.file.WizetFileInputStream;
import me.aj4real.jwizetreader.WizetFile;

import java.util.HashMap;
import java.util.Map;

public class WizetConvex extends WizetPropertyBlock {
    private final Map<String, WizetObject> children = new HashMap<>();
    public WizetConvex(WizetFileInputStream is, long offset, String name, WizetObject parent, WizetFile parentFile) {
        super(is, offset, name, parent, parentFile);
    }
    @Override
    public void parse() throws MalformedWizetFileException {
        is.setPosition(super.offset);
        long entryCount = is.readCompressedInt();
        for (int i = 0; i < entryCount; i++) {
            WizetObject ob = parseBlock(super.is, super.name, super.offset, this);
            getProperties().put(ob.getName(), ob);
        }
    }
    public Map<String, WizetObject> getProperties() {
        return this.children;
    }

    @Override
    public PropertyType type() {
        return PropertyType.CONVEX;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public WizetObject getParent() {
        return this.parent;
    }
}
