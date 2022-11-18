package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;

public record WizetVector(String getName, WizetObject getParent, long x, long y) implements WizetProperty {

    @Override
    public PropertyType type() {
        return PropertyType.VECTOR;
    }
    public Object value() {
        return null;
    }
}
