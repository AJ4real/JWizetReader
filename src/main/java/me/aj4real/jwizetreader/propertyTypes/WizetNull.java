package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;

public record WizetNull(String getName, WizetObject getParent) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.NULL;
    }
    public Object value() {
        return null;
    }
}
