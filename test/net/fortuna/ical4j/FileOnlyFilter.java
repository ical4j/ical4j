package net.fortuna.ical4j;

import java.io.FileFilter;
import java.io.File;

/**
 * User: tobli
 * Date: Apr 10, 2005
 * Time: 8:18:02 PM
 */
public class FileOnlyFilter implements FileFilter {
    public boolean accept(final File file) {
        // skip directories (including CVS)
        boolean accept = file.isFile();

        if (accept) {
            // ignore hidden files
            accept = !file.isHidden();
        }

        return accept;
    }
}
