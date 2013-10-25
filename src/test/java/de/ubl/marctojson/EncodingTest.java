package de.ubl.marctojson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

// cf. https://intern.finc.info/issues/1672
public class EncodingTest {

	@Before
	public void setup() {
		System.setProperty("org.marc4j.marc.MarcFactory",
				"de.ubl.marctojson.FatMarcFactory");
	}

	@Test
	public void testSaneUmlautEncoding() throws Exception {
		InputStream is = MarcFileTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		File tempFile = File.createTempFile("marctojson-", ".txt");
		File outputFile = File.createTempFile("marctojson-", ".json");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();
		Converter converter = new Converter();
		converter.convert(tempFile.getAbsolutePath(), "UTF-8",
				outputFile.getAbsolutePath(), "UTF-8");
		String text = Files.toString(new File(outputFile.getAbsolutePath()),
				Charsets.UTF_8);
		assertThat(text, containsString("Sinfonie für 2 Violinen"));
	}

	@Test
	public void testInvalidUmlautEncoding() throws Exception {
		InputStream is = MarcFileTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		File tempFile = File.createTempFile("marctojson-", ".txt");
		File outputFile = File.createTempFile("marctojson-", ".json");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();
		Converter converter = new Converter();
		converter.convert(tempFile.getAbsolutePath(), "ISO8859_1",
				outputFile.getAbsolutePath(), "UTF-8");
		String text = Files.toString(new File(outputFile.getAbsolutePath()),
				Charsets.UTF_8);
		assertThat(text, not(containsString("Sinfonie für 2 Violinen")));
		assertThat(text, containsString("Sinfonie fÃ¼r 2 Violinen"));
	}
}
