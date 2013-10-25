package de.ubl.marctojson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;

public class MarcFile extends FileWithCacheableMetadata {

    private static final long serialVersionUID = -7898002614768444648L;
    private static Logger logger = Logger.getLogger(MarcFile.class
            .getCanonicalName());

    public MarcFile(String pathname) {
        super(pathname);
    }

    public long getRecordCount() {
        InputStream in = null;
        try {
            in = new FileInputStream(this);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            // System.exit(1);
            return 0L;
        }

        final String hrkey = "records " + this.getAbsolutePath() + " " + this.lastModified();
        final String key = Base64.encodeBase64String(hrkey.getBytes());
        
        long counter = 0;

        if (this.getCache() != null) {
            final Object cachedObject = this.getCache().getHashMap("default").get(key);
            if (cachedObject != null) {
                try {
                    counter = Long.parseLong(cachedObject.toString());
                    in.close();
                    logger.debug("Cache hit on: " + key);
                    return counter;
                } catch (NumberFormatException e) {
                    logger.debug("Invalidating (corrupt) cache key: " + key
                            + ": " + cachedObject);
                    this.getCache().getHashMap("default").remove(key);
                } catch (IOException e) {
                    logger.warn("Could not close file: "
                            + this.getAbsolutePath());
                }
            }
        }

        try {
            final MarcReader reader = new MarcStreamReader(in);
            while (reader.hasNext()) {
                reader.next();
                counter += 1;
            }
            if (this.getCache() != null) {
                this.getCache().getHashMap("default").put(key, counter);
                logger.debug("Updated cache key: " + key);
                this.getCache().commit();
            }
            in.close();
        } catch (IOException e) {
            logger.warn("Could not close file: " + this.getAbsolutePath());
        }
        return counter;
    }
}
