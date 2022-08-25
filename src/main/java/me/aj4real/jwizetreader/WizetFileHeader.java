package me.aj4real.jwizetreader;

public record WizetFileHeader (String name, WizetFile parent, String pkg1, long size, int start, String copyright) {
}
