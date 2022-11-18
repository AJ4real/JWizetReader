package me.aj4real.jwizetreader.propertyTypes;

import me.aj4real.jwizetreader.MalformedWizetFileException;
import me.aj4real.jwizetreader.PropertyType;
import me.aj4real.jwizetreader.SharedConstants;
import me.aj4real.jwizetreader.file.WizetFileInputStream;
import me.aj4real.jwizetreader.WizetFile;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public final class WizetCanvas extends WizetPropertyBlock {
    private final Map<String, WizetObject> children = new HashMap<>();
    private int width, height, length, format;
    private BufferedImage image;
    private long imgStart, imgLength;
    public WizetCanvas(WizetFileInputStream is, long offset, String name, WizetObject parent, WizetFile parentFile) {
        super(is, offset, name, parent, parentFile);
    }
    @Override
    public void parse() throws IOException {
        byte b = is.readByte();
        if(b == 1) {
            is.setPosition(is.getPosition() + 2);
            super.parse();
        }
        width = is.readCompressedInt();
        height = is.readCompressedInt();
        int format = is.readCompressedInt();
        byte format2 = is.readByte();
        this.format = format + format2;
        is.setPosition(is.getPosition() + 4);
        long pngOffset = is.getPosition();
        length = is.readInt() - 1;
        is.setPosition(is.getPosition() + 1);
        if(length > 0) {
            imgStart = is.getPosition();
            imgLength = length;
            is.setPosition(pngOffset + length);
        }
        if(SharedConstants.PRELOAD_CANVAS_DATA && SharedConstants.KEEP_CANVAS_DATA_IN_MEMORY) getImage();
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
    public int getFormat() {
        return this.format;
    }
    public int getLength() {
        return this.length;
    }
    public BufferedImage getImage() throws IOException {
        if(this.image != null) return this.image;
        is.setPosition(imgStart);
        byte[] data = is.readBytes(imgLength);
        int sizeUncompressed = 0;
        int size8888;
        int maxWriteBuf = 2;

        byte[] writeBuf;

        switch (getFormat()) {
            case 1://4444
            case 513://565
                sizeUncompressed = getHeight() * getWidth() * 4;
                break;
            case 2://8888
                sizeUncompressed = getHeight() * getWidth() * 8;
                break;
            case 517://????
                sizeUncompressed = getHeight() * getWidth() / 128;
                break;
        }

        size8888 = getHeight() * getWidth() * 8;

        if (size8888 > maxWriteBuf) {
            maxWriteBuf = size8888;
        }
        writeBuf = new byte[maxWriteBuf];
        Inflater dec = new Inflater();
        dec.setInput(data);
        int declen;
        byte[] uc = new byte[sizeUncompressed];
        try {
            declen = dec.inflate(uc);
        } catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        }
        dec.end();
        if (getFormat() == 1) {
            for ( int i = 0; i < sizeUncompressed; i++) {
                byte low = (byte) (uc[i] & 0x0F);
                byte high = (byte) (uc[i] & 0xF0);
                writeBuf[(i << 1)] = (byte) (((low << 4) | low) & 0xFF);
                writeBuf[(i << 1) + 1] = (byte) (high | ((high >>> 4) & 0xF));
            }
        } else if (getFormat() == 2) {
            writeBuf = uc;
        } else if (getFormat() == 513) {
            for ( int i = 0; i < declen; i += 2) {
                byte bBits = (byte) ((uc[i] & 0x1F) << 3);
                byte gBits = (byte) (((uc[i + 1] & 0x07) << 5) | ((uc[i] & 0xE0) >> 3));
                byte rBits = (byte) (uc[i + 1] & 0xF8);

                writeBuf[(i << 1)] = (byte) (bBits | (bBits >> 5));
                writeBuf[(i << 1) + 1] = (byte) (gBits | (gBits >> 6));
                writeBuf[(i << 1) + 2] = (byte) (rBits | (rBits >> 5));
                writeBuf[(i << 1) + 3] = (byte) 0xFF;
            }
        } else if (getFormat() == 517) {
            byte b;
            int pixelIndex;
            for ( int i = 0; i < declen; i++) {
                for ( int j = 0; j < 8; j++) {
                    b = (byte) (((uc[i] & (0x01 << (7 - j))) >> (7 - j)) * 255);
                    for ( int k = 0; k < 16; k++) {
                        pixelIndex = (i << 9) + (j << 6) + k * 2;
                        writeBuf[pixelIndex] = b;
                        writeBuf[pixelIndex + 1] = b;
                        writeBuf[pixelIndex + 2] = b;
                        writeBuf[pixelIndex + 3] = (byte) 0xFF;
                    }
                }
            }
        }
        DataBufferByte imgData = new DataBufferByte(writeBuf, writeBuf.length / 2);
        SampleModel sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, this.getWidth(), this.getHeight(), 4, this.getWidth() * 4, new int[] { 2, 1, 0, 3 });
        WritableRaster imgRaster = Raster.createWritableRaster(sm, imgData, new Point(0, 0));
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        image.setData(imgRaster);
        if(SharedConstants.KEEP_CANVAS_DATA_IN_MEMORY) this.image = image;
        return image;
    }
    public Map<String, WizetObject> getProperties() {
        return this.children;
    }
    @Override
    public PropertyType type() {
        return PropertyType.CANVAS;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public WizetObject getParent() {
        return this.parent;
    }
    public void readData() {
    }
}
