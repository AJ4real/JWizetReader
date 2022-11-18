package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.PropertyType;

public record WizetDouble(String getName, WizetObject getParent, Double value) implements WizetProperty {
    @Override
    public PropertyType type() {
        return PropertyType.DOUBLE;
    }
}
