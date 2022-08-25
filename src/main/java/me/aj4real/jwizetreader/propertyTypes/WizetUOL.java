package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.WizetContainer;
import me.aj4real.jwizetreader.WizetObject;
import me.aj4real.jwizetreader.WizetProperty;
import me.aj4real.jwizetreader.MalformedWizetFileException;

public record WizetUOL(String getName, WizetObject getParent, String value) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.UOL;
    }
    public WizetObject getReferencedObject() throws MalformedWizetFileException {
        String path = value;
        WizetContainer parent = (WizetContainer) getParent();
        return parent.get(path);
    }
}
