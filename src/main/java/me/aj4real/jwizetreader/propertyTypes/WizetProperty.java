package me.aj4real.jwizetreader.propertyTypes;

public sealed interface WizetProperty extends WizetObject permits WizetDouble, WizetFloat, WizetInteger, WizetLong, WizetNull, WizetShort, WizetSound, WizetString, WizetReference, WizetVector {
    String getName();
    String toString();
    Object value();
}
