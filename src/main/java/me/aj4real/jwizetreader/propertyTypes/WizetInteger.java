package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.WizetObject;
import me.aj4real.jwizetreader.WizetProperty;

public record WizetInteger(String getName, WizetObject getParent, Integer value) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.INTEGER;
    }
}
