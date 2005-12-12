package net.fortuna.ical4j;

import java.io.File;
import java.io.FileFilter;

/**
 * User: tobli
 * Date: Apr 10, 2005
 * Time: 8:18:02 PM
 */
public class FileOnlyFilter implements FileFilter {
    
    /* (non-Javadoc)
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(final File file) {
        // skip directories (including CVS)
        return file.isFile() && !file.isHidden();
    }
}
