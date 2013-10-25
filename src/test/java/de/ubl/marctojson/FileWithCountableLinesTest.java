package de.ubl.marctojson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class FileWithCountableLinesTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRecordCount() throws IOException {
		InputStream is = MarcFileTest.class.getResourceAsStream("/5_lines.txt");
		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is,os);
		os.close();
		FileWithCountableLines textFile = new FileWithCountableLines(tempFile.getAbsolutePath());
		assertThat(textFile.getLineCount(), is(5L));
	}
}
