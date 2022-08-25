package me.aj4real.jwizetreader;

import java.io.IOException;

public class MalformedWizetFileException extends IOException {
    public MalformedWizetFileException(String message, WizetFile file) {
        super(message);
    }
    public MalformedWizetFileException(String message, Throwable cause, WizetFile file) {
        super(message, cause);
    }
}
