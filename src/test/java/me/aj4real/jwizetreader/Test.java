package me.aj4real.jwizetreader;

import me.aj4real.jwizetreader.file.ByteArrayChunkFileLoader;
import me.aj4real.jwizetreader.file.FileLoader;

import java.io.File;

public class Test {

    public static void main(String... args) throws Throwable {
        SharedConstants.PREPARSE_ALL_CONTAINERS = true;
        SharedConstants.PRELOAD_CANVAS_DATA = true;
        SharedConstants.KEEP_CANVAS_DATA_IN_MEMORY = false;
        File[] folders = {
                new File("D:\\maplestory\\235\\WZ"),
                new File("D:\\maplestory\\229.1\\client"),
                new File("D:\\maplestory\\203.4\\client"),
                new File("D:\\maplestory\\176\\client")
        };
        for (int i = 0; i < folders.length; i++) {
            File in = folders[i];
            System.out.println("Loading resource archive files...");
            long start = System.currentTimeMillis();
            doFile(in);
            System.out.println("Done loading Wizet Archive files... Took " + (System.currentTimeMillis() - start) + " ms");
        }
    }
    public static void doFile(File file) throws Throwable {
        if(file.isDirectory()) {
            for (File f : file.listFiles()) {
                doFile(f);
            }
            return;
        }
        Boolean[] checks = {
                !file.getName().endsWith(".wz"),
                file.getName().equalsIgnoreCase("data.wz"),
                file.getName().equalsIgnoreCase("list.wz")
        };
        for (Boolean b : checks) if(b) return;
        long fStart = System.currentTimeMillis();
        FileLoader loader = null;
//        loader = new InputStreamFileLoader(file);       // results: very slow, moderate memory usage
        loader = new ByteArrayChunkFileLoader(file, 16384);    // results: fast, moderate memory usage
//        loader = new InMemoryFileLoader(file);          // results: extremely fast, high memory usage
//        loader = new RandomAccessFileLoader(file);      // results: very slow, almost no memory usage
        WizetFile object = new WizetFile(file, loader);
        object.parse();
        System.out.println("Done loading " + file.getCanonicalPath() + " with " + object.COUNTER.get() + " entries... Took " + (System.currentTimeMillis() - fStart) + " ms");
    }
}
