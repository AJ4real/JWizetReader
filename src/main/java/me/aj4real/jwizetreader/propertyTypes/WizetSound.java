package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;

public record WizetSound(String getName, WizetObject getParent, long offset, long size) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.SOUND;
    }
    public Object value() {
        return null;
    }
}
