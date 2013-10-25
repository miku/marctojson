package de.ubl.marctojson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;

public class Converter {

	private static Logger logger = Logger.getLogger(Converter.class
			.getCanonicalName());

	public long convert(String inFilename, String outFilename) throws Exception {
		return convert(inFilename, outFilename, new HashMap<String, Object>());
	}

	public long convert(String inFilename, String outFilename,
			Map<String, Object> metadata) throws Exception {
		return convert(inFilename, "UTF-8", outFilename, "UTF-8", metadata);
	}

	public long convert(InputStream in, Writer writer,
			Map<String, Object> metadata) throws Exception {
		return convert(in, "UTF-8", writer, metadata);
	}

	public long convert(InputStream in, String inputEncoding, Writer writer, Map<String, Object> metadata) throws Exception {
		final MarcReader reader = new MarcStreamReader(in, inputEncoding);
		long counter = 0;
		while (reader.hasNext()) {
			final FatRecord record = (FatRecord) reader.next();
			try {
				writer.write(record.toJson(metadata));
				writer.write("\n");
				counter += 1;
			} catch (IOException ex) {
				logger.error(ex.getLocalizedMessage());
				logger.debug(ex.getCause());
				System.exit(1);
			}
		}
        writer.close();

		return counter;
	}
	
	public long convert(String inputFilename, String inputEncoding,
			String outputFilename, String outputEncoding) throws Exception {
		return convert(inputFilename, inputEncoding, outputFilename, outputEncoding, new HashMap<String, Object>());
	}

	public long convert(String inputFilename, String inputEncoding,
			String outputFilename, String outputEncoding,
			Map<String, Object> metadata) throws Exception {
		
		// write to stdout by default, unless ...
		Writer writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(System.out,
					outputEncoding));
		} catch (UnsupportedEncodingException ex) {
			System.err.println("unsupported encoding: "
					+ ex.getLocalizedMessage()
					+ " --list-encodings shows available encodings");
			System.exit(1);
		}

		// .. some outputFilename is given
		if (outputFilename != null) {
			try {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outputFilename), outputEncoding));
			} catch (FileNotFoundException ex) {
				System.err.println(ex.getLocalizedMessage());
				System.exit(1);
			}
		}

        File fileObj = new File(inputFilename);
        if (!fileObj.exists()) {
            throw new Exception("Input not found: " + inputFilename);
        }

		InputStream in = new FileInputStream(inputFilename);

		logger.debug("in: " + inputFilename + " (" + inputEncoding + ")");
		logger.debug("out: " + outputFilename + " (" + outputEncoding + ")");

		return convert(in, inputEncoding, writer, metadata);
	}
}
