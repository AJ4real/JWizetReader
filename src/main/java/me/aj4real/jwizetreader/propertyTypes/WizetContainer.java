package me.aj4real.jwizetreader.propertyTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public sealed interface WizetContainer extends WizetObject permits WizetDirectoryDataEntry, WizetImageDataEntry, WizetPropertyBlock {
    Map<String, WizetObject> getProperties();
    void parse() throws IOException;
    boolean isParsed();
    default int size() throws IOException {
        if(!isParsed()) parse();
        return getProperties().size();
    }
    default List<WizetObject> values() throws IOException {
        if(!isParsed()) parse();
        return new ArrayList<>(getProperties().values());
    }
    default List<String> getKeys() throws IOException {
        if(!isParsed()) parse();
        return new ArrayList<>(getProperties().keySet());
    }
    default boolean isNull(String... path) throws IOException {
        WizetObject o = get(path);
        return o == null || o instanceof WizetNull;
    }
    default WizetCanvas getCanvas(String... path) throws IOException {
        if(isNull(path)) return null;
        return ((WizetCanvas)get(path));
    }
    default double getDouble(String... path) throws IOException {
        if(isNull(path)) return 0.0;
        return ((WizetDouble)get(path)).value();
    }
    default float getFloat(String... path) throws IOException {
        if(isNull(path)) return 0;
        return ((WizetFloat)get(path)).value();
    }
    default int getInt(String... path) throws IOException {
        if(isNull(path)) return 0;
        return ((WizetInteger)get(path)).value();
    }
    default long getLong(String... path) throws IOException {
        if(isNull(path)) return 0;
        return ((WizetLong)get(path)).value();
    }
    default short getShort(String... path) throws IOException {
        if(isNull(path)) return 0;
        return ((WizetShort)get(path)).value();
    }
    default String getString(String... path) throws IOException {
        if(isNull(path)) return null;
        return ((WizetString)get(path)).value();
    }
    default WizetVector getVector(String... path) throws IOException {
        if(isNull(path)) return null;
        return (WizetVector)get(path);
    }
    default WizetContainer getContainer(String... path) throws IOException {
        if(isNull(path)) return null;
        return (WizetContainer)get(path);
    }
    default WizetObject get(String... path) throws IOException {
        if(!isParsed()) parse();
        WizetObject o = this;
        for (int i = 0; i < path.length; i++) {
            if(o instanceof WizetContainer c) {
                if(!c.isParsed()) c.parse();
                o = c.getProperties().get(path[i]);
            }
        }
        return o;
    }
}
