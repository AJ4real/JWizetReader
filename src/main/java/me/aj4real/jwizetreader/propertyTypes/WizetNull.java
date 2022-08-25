package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.WizetObject;
import me.aj4real.jwizetreader.WizetProperty;

public record WizetNull(String getName, WizetObject getParent) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.NULL;
    }
    public Object value() {
        return null;
    }
}
