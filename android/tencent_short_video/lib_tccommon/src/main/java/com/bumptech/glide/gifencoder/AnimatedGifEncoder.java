//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bumptech.glide.gifencoder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AnimatedGifEncoder {
    private static final String TAG = "AnimatedGifEncoder";
    private static final double MIN_TRANSPARENT_PERCENTAGE = 4.0D;
    private int width;
    private int height;
    private Integer transparent = null;
    private int transIndex;
    private int repeat = -1;
    private int delay = 0;
    private boolean started = false;
    private OutputStream out;
    private Bitmap image;
    private byte[] pixels;
    private byte[] indexedPixels;
    private int colorDepth;
    private byte[] colorTab;
    private boolean[] usedEntry = new boolean[256];
    private int palSize = 7;
    private int dispose = -1;
    private boolean closeStream = false;
    private boolean firstFrame = true;
    private boolean sizeSet = false;
    private int sample = 10;
    private boolean hasTransparentPixels;

    public AnimatedGifEncoder() {
    }

    public void setDelay(int ms) {
        this.delay = Math.round((float)ms / 10.0F);
    }

    public void setDispose(int code) {
        if (code >= 0) {
            this.dispose = code;
        }

    }

    public void setRepeat(int iter) {
        if (iter >= 0) {
            this.repeat = iter;
        }

    }

    public void setTransparent(int color) {
        this.transparent = color;
    }

    public boolean addFrame(Bitmap im) {
        if (im != null && this.started) {
            boolean ok = true;

            try {
                if (!this.sizeSet) {
                    this.setSize(im.getWidth(), im.getHeight());
                }

                this.image = im;
                this.getImagePixels();
                this.analyzePixels();
                if (this.firstFrame) {
                    this.writeLSD();
                    this.writePalette();
                    if (this.repeat >= 0) {
                        this.writeNetscapeExt();
                    }
                }

                this.writeGraphicCtrlExt();
                this.writeImageDesc();
                if (!this.firstFrame) {
                    this.writePalette();
                }

                this.writePixels();
                this.firstFrame = false;
            } catch (IOException var4) {
                ok = false;
            }

            return ok;
        } else {
            return false;
        }
    }

    public boolean finish() {
        if (!this.started) {
            return false;
        } else {
            boolean ok = true;
            this.started = false;

            try {
                this.out.write(59);
                this.out.flush();
                if (this.closeStream) {
                    this.out.close();
                }
            } catch (IOException var3) {
                ok = false;
            }

            this.transIndex = 0;
            this.out = null;
            this.image = null;
            this.pixels = null;
            this.indexedPixels = null;
            this.colorTab = null;
            this.closeStream = false;
            this.firstFrame = true;
            return ok;
        }
    }

    public void setFrameRate(float fps) {
        if (fps != 0.0F) {
            this.delay = Math.round(100.0F / fps);
        }

    }

    public void setQuality(int quality) {
        if (quality < 1) {
            quality = 1;
        }

        this.sample = quality;
    }

    public void setSize(int w, int h) {
        if (!this.started || this.firstFrame) {
            this.width = w;
            this.height = h;
            if (this.width < 1) {
                this.width = 320;
            }

            if (this.height < 1) {
                this.height = 240;
            }

            this.sizeSet = true;
        }
    }

    public boolean start(OutputStream os) {
        if (os == null) {
            return false;
        } else {
            boolean ok = true;
            this.closeStream = false;
            this.out = os;

            try {
                this.writeString("GIF89a");
            } catch (IOException var4) {
                ok = false;
            }

            return this.started = ok;
        }
    }

    public boolean start(String file) {
        boolean ok = true;

        try {
            this.out = new BufferedOutputStream(new FileOutputStream(file));
            ok = this.start(this.out);
            this.closeStream = true;
        } catch (IOException var4) {
            ok = false;
        }

        return this.started = ok;
    }

    private void analyzePixels() {
        int len = this.pixels.length;
        int nPix = len / 3;
        this.indexedPixels = new byte[nPix];
        NeuQuant nq = new NeuQuant(this.pixels, len, this.sample);
        this.colorTab = nq.process();

        int k;
        for(k = 0; k < this.colorTab.length; k += 3) {
            byte temp = this.colorTab[k];
            this.colorTab[k] = this.colorTab[k + 2];
            this.colorTab[k + 2] = temp;
            this.usedEntry[k / 3] = false;
        }

        k = 0;

        for(int i = 0; i < nPix; ++i) {
            int index = nq.map(this.pixels[k++] & 255, this.pixels[k++] & 255, this.pixels[k++] & 255);
            this.usedEntry[index] = true;
            this.indexedPixels[i] = (byte)index;
        }

        this.pixels = null;
        this.colorDepth = 8;
        this.palSize = 7;
        if (this.transparent != null) {
            this.transIndex = this.findClosest(this.transparent);
        } else if (this.hasTransparentPixels) {
            this.transIndex = this.findClosest(0);
        }

    }

    private int findClosest(int color) {
        if (this.colorTab == null) {
            return -1;
        } else {
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            int minpos = 0;
            int dmin = 16777216;
            int len = this.colorTab.length;

            for(int i = 0; i < len; ++i) {
                int dr = r - (this.colorTab[i++] & 255);
                int dg = g - (this.colorTab[i++] & 255);
                int db = b - (this.colorTab[i] & 255);
                int d = dr * dr + dg * dg + db * db;
                int index = i / 3;
                if (this.usedEntry[index] && d < dmin) {
                    dmin = d;
                    minpos = index;
                }
            }

            return minpos;
        }
    }

    private void getImagePixels() {
        int w = this.image.getWidth();
        int h = this.image.getHeight();
        if (w != this.width || h != this.height) {
            Bitmap temp = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
            Canvas canvas = new Canvas(temp);
            canvas.drawBitmap(temp, 0.0F, 0.0F, (Paint)null);
            this.image = temp;
        }

        int[] pixelsInt = new int[w * h];
        this.image.getPixels(pixelsInt, 0, w, 0, 0, w, h);
        this.pixels = new byte[pixelsInt.length * 3];
        int pixelsIndex = 0;
        this.hasTransparentPixels = false;
        int totalTransparentPixels = 0;
        int[] arr$ = pixelsInt;
        int len$ = pixelsInt.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            int pixel = arr$[i$];
            if (pixel == 0) {
                ++totalTransparentPixels;
            }

            this.pixels[pixelsIndex++] = (byte)(pixel & 255);
            this.pixels[pixelsIndex++] = (byte)(pixel >> 8 & 255);
            this.pixels[pixelsIndex++] = (byte)(pixel >> 16 & 255);
        }

        double transparentPercentage = (double)(100 * totalTransparentPixels) / (double)pixelsInt.length;
        this.hasTransparentPixels = transparentPercentage > 4.0D;
        if (Log.isLoggable("AnimatedGifEncoder", 3)) {
            Log.d("AnimatedGifEncoder", "got pixels for frame with " + transparentPercentage + "% transparent pixels");
        }

    }

    private void writeGraphicCtrlExt() throws IOException {
        this.out.write(33);
        this.out.write(249);
        this.out.write(4);
        byte transp;
        int disp;
        if (this.transparent == null && !this.hasTransparentPixels) {
            transp = 0;
            disp = 0;
        } else {
            transp = 1;
            disp = 2;
        }

        if (this.dispose >= 0) {
            disp = this.dispose & 7;
        }

        disp <<= 2;
        this.out.write(0 | disp | 0 | transp);
        this.writeShort(this.delay);
        this.out.write(this.transIndex);
        this.out.write(0);
    }

    private void writeImageDesc() throws IOException {
        this.out.write(44);
        this.writeShort(0);
        this.writeShort(0);
        this.writeShort(this.width);
        this.writeShort(this.height);
        if (this.firstFrame) {
            this.out.write(0);
        } else {
            this.out.write(128 | this.palSize);
        }

    }

    private void writeLSD() throws IOException {
        this.writeShort(this.width);
        this.writeShort(this.height);
        this.out.write(240 | this.palSize);
        this.out.write(0);
        this.out.write(0);
    }

    private void writeNetscapeExt() throws IOException {
        this.out.write(33);
        this.out.write(255);
        this.out.write(11);
        this.writeString("NETSCAPE2.0");
        this.out.write(3);
        this.out.write(1);
        this.writeShort(this.repeat);
        this.out.write(0);
    }

    private void writePalette() throws IOException {
        this.out.write(this.colorTab, 0, this.colorTab.length);
        int n = 768 - this.colorTab.length;

        for(int i = 0; i < n; ++i) {
            this.out.write(0);
        }

    }

    private void writePixels() throws IOException {
        LZWEncoder encoder = new LZWEncoder(this.width, this.height, this.indexedPixels, this.colorDepth);
        encoder.encode(this.out);
    }

    private void writeShort(int value) throws IOException {
        this.out.write(value & 255);
        this.out.write(value >> 8 & 255);
    }

    private void writeString(String s) throws IOException {
        for(int i = 0; i < s.length(); ++i) {
            this.out.write((byte)s.charAt(i));
        }

    }
}
