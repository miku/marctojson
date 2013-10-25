package de.ubl.marctojson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.cli.ParseException;

public class Utils {

    private static final String errMessage = "Syntax for key value pairs " +
    		"is key=value or key=value1,value2,...";

    /**
     * Parse a list of key=value or key=value,value,... pairs into a Map.
     * 
     * @param kvStrings
     *            , e.g. ["id=1", "colors=red,green,blue"]
     * @return A map, e.g. {"id" : "1", "colors": ["red", "green", "blue"]}
     * @throws ParseException
     */
    public static Map<String, Object> getMapForKeys(final String[] kvStrings)
            throws ParseException {
        // accept key=value or key=value,value,value
        final HashMap<String, Object> kvMap = new HashMap<String, Object>();
        for (String kvString : kvStrings) {
            if (kvString.endsWith("=")) {
                throw new ParseException(errMessage);
            }
            final String[] topLevelParts = kvString.split("=");
            if (topLevelParts.length != 2) {
                throw new ParseException(errMessage);
            }
            final String key = topLevelParts[0];
            final String valueOrValues = topLevelParts[1];

            final String[] values = valueOrValues.split(",");

            if (values.length < 0) {
                throw new ParseException("Key " + key + " has no value.");
            }

            if (values.length == 1) {
                kvMap.put(key, values[0]);
            } else {
                kvMap.put(key, new ArrayList<String>(Arrays.asList(values)));
            }
        }
        return kvMap;
    }

    public static boolean inSync(MarcFile inFile,
            FileWithCountableLines outFile) {
        long recordCount = inFile.getRecordCount();
        long lineCount = outFile.getLineCount();
        return recordCount == lineCount;
    }

    private static String combineBase(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    public static String combine(String... elements) {
        if (elements.length == 0) {
            return "";
        }
        if (elements.length == 1) {
            return elements[0];
        }
        if (elements.length == 2) {
            return combineBase(elements[0], elements[1]);
        }
        if (elements.length > 2) {
            String car = elements[0];
            String[] cdr = Arrays.copyOfRange(elements, 1, elements.length);
            return combine(car, combine(cdr));
        }
        return "";
    }

    public static boolean createDirectories(String dir) throws IOException {
        File fileObj = new File(dir);
        if (fileObj.exists()) {
            if (fileObj.isDirectory()) {
                return true;
            } else {
                throw new IOException("Could not create directory: " + dir);
            }
        } else {
            return fileObj.mkdirs();
        }
    }
    
    public static void dumpAvailableEncodings() {
		SortedMap<String, Charset> m = Charset.availableCharsets();
		Set<String> k = m.keySet();
		System.out.println("Canonical name, Display name,"
				+ " Can encode, Aliases");
		Iterator<String> i = k.iterator();
		while (i.hasNext()) {
			String n = (String) i.next();
			Charset e = (Charset) m.get(n);
			String d = e.displayName();
			boolean c = e.canEncode();
			System.out.print(n + ", " + d + ", " + c);
			Set<String> s = e.aliases();
			Iterator<String> j = s.iterator();
			while (j.hasNext()) {
				String a = (String) j.next();
				System.out.print(", " + a);
			}
			System.out.println("");
		}
		System.out.println("system file.encoding is: "
				+ System.getProperty("file.encoding", "unknown"));
		System.out.println("default charset name: "
				+ Charset.defaultCharset().name());

    }
    

}
