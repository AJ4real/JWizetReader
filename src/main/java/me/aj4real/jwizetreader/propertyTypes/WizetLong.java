package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;

public record WizetLong(String getName, WizetObject getParent, Long value) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.LONG;
    }
}
