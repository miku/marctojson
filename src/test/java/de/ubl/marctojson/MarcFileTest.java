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

public class MarcFileTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRecordCount() throws IOException {
		// 423_records.mrc
		InputStream is = MarcFileTest.class.getResourceAsStream("/423_records.mrc");
		File tempFile = File.createTempFile("marctojson-", ".mrc");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is,os);
		os.close();
		MarcFile marc = new MarcFile(tempFile.getAbsolutePath());
		assertThat(marc.getRecordCount(), is(423L));

		// loc.natlib.consortium.hvd_001400145.marc.mrc
		is = MarcFileTest.class.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		tempFile = File.createTempFile("marctojson-", ".mrc");
		os = new FileOutputStream(tempFile);
		IOUtils.copy(is,os);
		os.close();
		marc = new MarcFile(tempFile.getAbsolutePath());
		assertThat(marc.getRecordCount(), is(1L));

	}
}
