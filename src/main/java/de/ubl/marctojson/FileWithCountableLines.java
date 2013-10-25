package de.ubl.marctojson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class FileWithCountableLines extends FileWithCacheableMetadata {

    private static final long serialVersionUID = 7958659907454566605L;
    private static Logger logger = Logger.getLogger(MarcFile.class
            .getCanonicalName());

    public FileWithCountableLines(String pathname) {
        super(pathname);
    }

    public long getLineCount() {

        long lines = 0;
        if (!this.exists()) {
            return 0L;
        }
        if (this.length() == 0) {
            return 0L;
        }

        final String hrkey = "lines " + this.getAbsolutePath() + " " + this.lastModified();
        final String key = Base64.encodeBase64String(hrkey.getBytes());

        if (this.getCache() != null) {
            final Object cachedObject = this.getCache().getHashMap("default").get(key);
            if (cachedObject != null) {
                try {
                    lines = Long.parseLong(cachedObject.toString());
                    logger.debug("Cache hit on: " + key);
                    return lines;
                } catch (NumberFormatException e) {
                    // invalidate this cache item
                    this.getCache().getHashMap("default").remove(key);
                }
            }
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(this));
            while (reader.readLine() != null) {
                lines++;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            logger.debug("File not found: " + e);
        } catch (IOException e) {
            logger.debug("IOException: " + e);
        }

        if (this.getCache() != null) {
            this.getCache().getHashMap("default").put(key, lines);
            logger.debug("Updated cache: " + key);
            this.getCache().commit();
        }
        return lines;
    }
}
