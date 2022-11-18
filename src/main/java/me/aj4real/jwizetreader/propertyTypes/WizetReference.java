package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;

import java.io.IOException;

public record WizetReference(String getName, WizetObject getParent, String value) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.UOL;
    }
    public WizetObject getReferencedObject() throws IOException {
        String path = value;
        WizetContainer parent = (WizetContainer) getParent();
        return parent.get(path);
    }
}
