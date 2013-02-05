package org.eventjuggler.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class IOUtils {

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte [] buf = new byte[32768];
        try {
            int rc = in.read(buf);
            while (rc != -1) {
                out.write(buf, 0, rc);
                rc = in.read(buf);
            }
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {}

            out.close();
        }
    }
}
