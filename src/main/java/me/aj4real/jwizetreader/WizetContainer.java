package me.aj4real.jwizetreader;

import me.aj4real.jwizetreader.propertyTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface WizetContainer extends WizetObject {
    Map<String, WizetObject> getProperties();
    void parse() throws MalformedWizetFileException;
    boolean isParsed();
    default int size() throws MalformedWizetFileException {
        if(!isParsed()) parse();
        return getProperties().size();
    }
    default List<WizetObject> values() throws MalformedWizetFileException {
        if(!isParsed()) parse();
        return new ArrayList<>(getProperties().values());
    }
    default List<String> getKeys() throws MalformedWizetFileException {
        if(!isParsed()) parse();
        return new ArrayList<>(getProperties().keySet());
    }
    default boolean isNull(String... path) throws MalformedWizetFileException {
        WizetObject o = get(path);
        if(o == null || o instanceof WizetNull) {
            return true;
        }
        return false;
    }
    default WizetCanvas getCanvas(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return null;
        return ((WizetCanvas)get(path));
    }
    default double getDouble(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return 0;
        return ((WizetDouble)get(path)).value();
    }
    default float getFloat(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return 0;
        return ((WizetFloat)get(path)).value();
    }
    default int getInt(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return 0;
        return ((WizetInteger)get(path)).value();
    }
    default long getLong(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return 0;
        return ((WizetLong)get(path)).value();
    }
    default short getShort(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return 0;
        return ((WizetShort)get(path)).value();
    }
    default String getString(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return null;
        return ((WizetString)get(path)).value();
    }
    default WizetVector getVector(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return null;
        return (WizetVector)get(path);
    }
    default WizetContainer getContainer(String... path) throws MalformedWizetFileException {
        if(isNull(path)) return null;
        return (WizetContainer)get(path);
    }
    default WizetObject get(String... path) throws MalformedWizetFileException {
        if(!isParsed()) parse();
        WizetObject o = this;
        for (int i = 0; i < path.length; i++) {
            if(o instanceof WizetContainer) {
                WizetContainer c = ((WizetContainer)o);
                if(!c.isParsed()) c.parse();
                o = c.getProperties().get(path[i]);
            }
        }
        return o;
    }
}
