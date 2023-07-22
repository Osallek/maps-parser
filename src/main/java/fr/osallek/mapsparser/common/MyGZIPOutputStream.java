package fr.osallek.mapsparser.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class MyGZIPOutputStream extends GZIPOutputStream {

    public MyGZIPOutputStream(OutputStream out) throws IOException {
        super(out, 1024 * 1024);
        this.def.setLevel(9);
    }
}
