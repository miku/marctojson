package de.ubl.marctojson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class FatRecordTest {
	
	@Before
	public void setUp() throws Exception {
		System.setProperty("org.marc4j.marc.MarcFactory",
				"de.ubl.marctojson.FatMarcFactory");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSha1() throws Exception {
		InputStream is = FatRecordTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));
		// sha1sum src/test/resources/*
		// // UTF-8 -> UTF-8
		assertThat(record.sha1(),
				is("a3b65e6841cfb65d55b9ed464d9c48700ec16bac"));

		// check that the other encoding yield different content
		// UTF-8 -> ISO8859-1, should FAIL
		is = FatRecordTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		record = (FatRecord) reader.next();
		assertThat(record.sha1("ISO8859-1"),
				is(not("a3b65e6841cfb65d55b9ed464d9c48700ec16bac")));

		// ISO8859_1 -> UTF-8, should FAIL
		is = FatRecordTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		reader = new MarcStreamReader(is, "ISO8859_1");
		assertThat(reader, is(notNullValue()));
		record = (FatRecord) reader.next();
		assertThat(record.sha1(),
				is(not("a3b65e6841cfb65d55b9ed464d9c48700ec16bac")));

		// ISO8859_1 -> ISO8859-1, should be OK
		is = FatRecordTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		reader = new MarcStreamReader(is, "ISO8859_1");
		assertThat(reader, is(notNullValue()));
		record = (FatRecord) reader.next();
		assertThat(record.sha1("ISO8859-1"),
				is("a3b65e6841cfb65d55b9ed464d9c48700ec16bac"));

	}

	@Test
	public void testSerializeARecordWithUTF8Chars() throws Exception {

		InputStream is = FatRecordTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");
		
		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();
		System.out.println(tempFile.getAbsolutePath());

		is = FatRecordTest.class
				.getResourceAsStream("/loc.natlib.consortium.hvd_001400145.marc.mrc");

		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));

		// UTF-8 -> UTF-8
		String fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.UTF_8);
		String fromSerialize = record.serialize("UTF-8");
		// assertThat(fromFile, is(fromSerialize));

		// a few failure cases
		// ISO_8859_1 -> UTF-8
		fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.ISO_8859_1);
		fromSerialize = record.serialize("UTF-8");
		assertThat(fromFile, is(not(fromSerialize)));

		// ISO_8859_1 -> ISO8859-1
		fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.ISO_8859_1);
		fromSerialize = record.serialize("ISO8859-1");
		assertThat(fromFile, is(not(fromSerialize)));

		// US-ASCII -> US-ASCII
		fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.US_ASCII);
		fromSerialize = record.serialize("US-ASCII");
		assertThat(fromFile, is(not(fromSerialize)));
	}

	@Test
	public void testSerializeAVanillaRecord() throws Exception {
		InputStream is = FatRecordTest.class.getResourceAsStream("/vanilla.mrc");

		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();

		is = FatRecordTest.class.getResourceAsStream("/vanilla.mrc");

		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));

		// UTF-8 -> UTF-8
		String fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.UTF_8);
		String fromSerialize = record.serialize("UTF-8");
		assertThat(fromFile, is(fromSerialize));

		// a few failure cases
		// ISO_8859_1 -> UTF-8
		fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.ISO_8859_1);
		fromSerialize = record.serialize("UTF-8");
		assertThat(fromFile, is(not(fromSerialize)));

		// ISO_8859_1 -> UTF-8
		fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.ISO_8859_1);
		fromSerialize = record.serialize("ISO8859-1");
		assertThat(fromFile, is(not(fromSerialize)));

		// US-ASCII -> US-ASCII
		fromFile = Files.toString(new File(tempFile.getAbsolutePath()),
				Charsets.US_ASCII);
		fromSerialize = record.serialize("US-ASCII");
		assertThat(fromFile, is(not(fromSerialize)));

	}

	@Test
	public void testVanillaToMap() throws Exception {

		// 01013nam a22002652a 4500
		// 001 9781848063334
		// 003 UK-WkNB
		// 005 20130604000000.0
		// 007 ta
		// 008 130525e201306uuxxk | |||||||0|0 eng|d
		// 020 $a 9781848063334 : $c £16.00
		// 020 $a 1848063334 : $c £16.00
		// 040 $a UK-WkNB $b eng $c UK-WkNB
		// 072 7 $a TNK $2 bicssc
		// 072 7 $a HOU $2 eflch
		// 100 1 $a Dunster, A.
		// 245 10 $a Applications, performance characteristics and environmental
		// benefits of alkali-activated binder concretes / $c A. Dunster, K.
		// Quillin.
		// 260 $a Bracknell : $b IHS BRE Press : $b [distributor] IHS BRE Press,
		// $c 2013.
		// 300 $a 8 p. ; $c 30x21 cm.
		// 365 $a 02 $b 16.00 $c GBP $d 00 $h Z 16.00 0.0 16.00 0.00 $j GB $k
		// xxk $m Construction Research Communications Ltd $2 onix-pt
		// 366 $b 20130605 $c NP 20130525 $d 20130630 $j GB $k xxk $m
		// Construction Research Communications Ltd $2 UK-WkNB
		// 500 $a Pamphlet.
		// 650 7 $a Building construction & materials. $2 bicssc
		// 650 7 $a House and Home. $2 eflch
		// 700 1 $a Quillin, K.

		InputStream is = FatRecordTest.class.getResourceAsStream("/vanilla.mrc");

		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();

		is = FatRecordTest.class.getResourceAsStream("/vanilla.mrc");

		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));

		Map<String, Object> map = record.toMap();
		assertThat(map, is(notNullValue()));

		Set<String> expectedKeys = new HashSet<String>(
				Arrays.asList(new String[] { "001", "003", "005", "007", "008",
						"020", "040", "072", "100", "245", "260", "300", "365",
						"366", "500", "650", "700", "leader" }));

		Set<String> keys = map.keySet();
		assertThat(keys, is(expectedKeys));
		assertThat(keys.size(), is(expectedKeys.size()));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testVanillaToMapWithMetadata() throws Exception {

		// 01013nam a22002652a 4500
		// 001 9781848063334
		// 003 UK-WkNB
		// 005 20130604000000.0
		// 007 ta
		// 008 130525e201306uuxxk | |||||||0|0 eng|d
		// 020 $a 9781848063334 : $c £16.00
		// 020 $a 1848063334 : $c £16.00
		// 040 $a UK-WkNB $b eng $c UK-WkNB
		// 072 7 $a TNK $2 bicssc
		// 072 7 $a HOU $2 eflch
		// 100 1 $a Dunster, A.
		// 245 10 $a Applications, performance characteristics and environmental
		// benefits of alkali-activated binder concretes / $c A. Dunster, K.
		// Quillin.
		// 260 $a Bracknell : $b IHS BRE Press : $b [distributor] IHS BRE Press,
		// $c 2013.
		// 300 $a 8 p. ; $c 30x21 cm.
		// 365 $a 02 $b 16.00 $c GBP $d 00 $h Z 16.00 0.0 16.00 0.00 $j GB $k
		// xxk $m Construction Research Communications Ltd $2 onix-pt
		// 366 $b 20130605 $c NP 20130525 $d 20130630 $j GB $k xxk $m
		// Construction Research Communications Ltd $2 UK-WkNB
		// 500 $a Pamphlet.
		// 650 7 $a Building construction & materials. $2 bicssc
		// 650 7 $a House and Home. $2 eflch
		// 700 1 $a Quillin, K.

		InputStream is = FatRecordTest.class.getResourceAsStream("/vanilla.mrc");

		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();

		is = MarcFileTest.class.getResourceAsStream("/vanilla.mrc");

		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));

		Map<String, Object> map = record.toMap(new HashMap<String, Object>());
		assertThat(map, is(notNullValue()));

		Set<String> expectedKeys = new HashSet<String>(
				Arrays.asList(new String[] { "content", "content_type", "sha1",
						"original", "meta" }));

		Set<String> expectedContentKeys = new HashSet<String>(
				Arrays.asList(new String[] { "001", "003", "005", "007", "008",
						"020", "040", "072", "100", "245", "260", "300", "365",
						"366", "500", "650", "700", "leader" }));

		Set<String> keys = map.keySet();
		assertThat(keys, is(expectedKeys));
		assertThat(map.get("content_type").toString(), is("application/marc"));
		assertThat(map.get("sha1").toString(), is("9d003e3ae301bb035082d6a548803cd282fde46e"));
		assertThat(map.get("original").toString().length(), is(record.serialize().length()));
		assertThat(((Map<String, Object>) map.get("meta")).isEmpty(), is(true));
		
		Map<String, Object> contentMap = (Map<String, Object>) map.get("content");
		Set<String> contentKeys = contentMap.keySet();
		assertThat(contentKeys, is(expectedContentKeys));
		assertThat(keys.size(), is(expectedKeys.size()));
	}
	
	
	@Test
	public void testSubfieldsToLists() throws Exception {
		// 01013nam a22002652a 4500
		// 001 9781848063334
		// 003 UK-WkNB
		// 005 20130604000000.0
		// 007 ta
		// 008 130525e201306uuxxk | |||||||0|0 eng|d
		// 020 $a 9781848063334 : $c £16.00
		// 020 $a 1848063334 : $c £16.00
		// 040 $a UK-WkNB $b eng $c UK-WkNB
		// 072 7 $a TNK $2 bicssc
		// 072 7 $a HOU $2 eflch
		// 100 1 $a Dunster, A.
		// 245 10 $a Applications, performance characteristics and environmental
		// benefits of alkali-activated binder concretes / $c A. Dunster, K.
		// Quillin.
		// 260 $a Bracknell : $b IHS BRE Press : $b [distributor] IHS BRE Press,
		// $c 2013.
		// 300 $a 8 p. ; $c 30x21 cm.
		// 365 $a 02 $b 16.00 $c GBP $d 00 $h Z 16.00 0.0 16.00 0.00 $j GB $k
		// xxk $m Construction Research Communications Ltd $2 onix-pt
		// 366 $b 20130605 $c NP 20130525 $d 20130630 $j GB $k xxk $m
		// Construction Research Communications Ltd $2 UK-WkNB
		// 500 $a Pamphlet.
		// 650 7 $a Building construction & materials. $2 bicssc
		// 650 7 $a House and Home. $2 eflch
		// 700 1 $a Quillin, K.

		InputStream is = FatRecordTest.class.getResourceAsStream("/vanilla.mrc");

		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();

		is = FatRecordTest.class.getResourceAsStream("/vanilla.mrc");

		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));

		Map<String, Object> map = record.toMap(new HashMap<String, Object>());
		assertThat(map, is(notNullValue()));
		
		Map<String, Object> contentMap = (Map<String, Object>) map.get("content");
		Object obj = contentMap.get("650");
		assertThat(obj, is(notNullValue()));
		try {
			Collection f650 = (Collection) obj;
			assertThat(f650.size(), is(2));
			Iterator it = f650.iterator();
			Object firstEntry = it.next();
			Object secondEntry = it.next();
			// Arrays.asList(new String[] { "Hello" })
			// this must be a List, finally
			assertThat(firstEntry.getClass().getName(), is("java.util.HashMap"));
			assertThat(secondEntry.getClass().getName(), is("java.util.HashMap"));
		} catch(ClassCastException cce) {
			fail();
		}
		// assertThat(f650.getClass(), is(Collection.class));
	}
	
	@Test
	public void testRepeatedFields() throws Exception {
//		00066     2200049   4500
//		020    $a 123
//		020    $a 321
		InputStream is = FatRecordTest.class.getResourceAsStream("/repeatedfields.mrc");

		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();

		is = FatRecordTest.class.getResourceAsStream("/repeatedfields.mrc");

		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));
		
		Map<String, Object> map = record.toMap();
		Collection<Map<String, Object>> f020 = (Collection<Map<String, Object>>) map.get("020");
		assertThat(f020.size(), is(2));
	}
	
	@Test
	public void testRepeatedSubfields() throws IOException {
//		00051     2200037   4500
//		020    $a 123 $a 321

		InputStream is = FatRecordTest.class.getResourceAsStream("/repeatedsubfields.mrc");

		File tempFile = File.createTempFile("marctojson-", ".txt");
		OutputStream os = new FileOutputStream(tempFile);
		IOUtils.copy(is, os);
		os.close();

		is = FatRecordTest.class.getResourceAsStream("/repeatedsubfields.mrc");

		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		assertThat(reader, is(notNullValue()));
		FatRecord record = (FatRecord) reader.next();
		assertThat(record, is(notNullValue()));
		
		Map<String, Object> map = record.toMap();
		Collection<Map<String, Object>> f020 = (Collection<Map<String, Object>>) map.get("020");
		assertThat(f020.size(), is(2)); // cf. https://intern.finc.info/issues/1310
	}

}
