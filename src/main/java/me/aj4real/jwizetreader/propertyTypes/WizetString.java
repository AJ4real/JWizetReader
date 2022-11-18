package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;

public record WizetString(String getName, WizetObject getParent, String value) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.STRING;
    }
}
