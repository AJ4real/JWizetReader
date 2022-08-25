package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.WizetObject;
import me.aj4real.jwizetreader.WizetProperty;

public record WizetVector(String getName, WizetObject getParent, long x, long y) implements WizetProperty {

    @Override
    public PropertyType type() {
        return PropertyType.VECTOR;
    }
    public Object value() {
        return null;
    }
}
