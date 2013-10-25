package de.ubl.marctojson;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UtilsTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMapForKeysAcceptsValidInput() throws ParseException {
        Map<String[], Map<String, Object>> expected = new HashMap<String[], Map<String, Object>>();
        
        Map<String, Object> testMap1 = new HashMap<String, Object>();
        testMap1.put("hello", "world");
        expected.put(new String[] { "hello=world" }, testMap1);

        Map<String, Object> testMap2 = new HashMap<String, Object>();
        Collection<String> sublist = new ArrayList<String>();
        sublist.add("world");
        sublist.add("domination");
        testMap2.put("hello", sublist);
        expected.put(new String[] { "hello=world,domination" }, testMap2);

        Map<String, Object> testMap3 = new HashMap<String, Object>();
        sublist = new ArrayList<String>();
        sublist.add("a");
        sublist.add("b");
        sublist.add("1");
        sublist.add("2");
        testMap3.put("x", sublist);
        expected.put(new String[] { "x=a,b,1,2" }, testMap3);

        Map<String, Object> testMap4 = new HashMap<String, Object>();
        testMap4.put("hello", "@1");
        expected.put(new String[] { "hello=@1" }, testMap4);

        for (String[] key : expected.keySet()) {
            assertThat(Utils.getMapForKeys(key), is(expected.get(key)));
        }
    }

    @Test
    public void testGetMapForKeysRejectsInvalidInput() throws ParseException {
        Collection<String[]> examples = new ArrayList<String[]>();
        examples.add(new String[]{"hello="});
        examples.add(new String[]{"hello=world=hey"});
        examples.add(new String[]{"hello=world="});
        examples.add(new String[]{"hello=@@=="});
        for (String[] example : examples) {
            try {
            Utils.getMapForKeys(example);
            fail(Arrays.toString(example) + " should have caused a " + ParseException.class.getCanonicalName());
            } catch (ParseException ex) {}
        }
    }
    
    @Test
    public void testCombine() {
        assertThat(Utils.combine(), is(""));
        assertThat(Utils.combine("/home"), is("/home"));
        assertThat(Utils.combine("/home/test"), is("/home/test"));
        assertThat(Utils.combine("/home/test", "tmp"), is("/home/test/tmp"));
        assertThat(Utils.combine("/home/test", "tmp", "test.dat"), is("/home/test/tmp/test.dat"));
        assertThat(Utils.combine("a", "b", "c", "d", "e", "f", "g", "h"), is("a/b/c/d/e/f/g/h"));
        assertThat(Utils.combine("/a", "b", "c", "d"), is("/a/b/c/d"));
        assertThat(Utils.combine("/a", "/b", "c", "d"), is("/a/b/c/d"));
        assertThat(Utils.combine("a", "/b", "c", "d"), is("a/b/c/d"));
        assertThat(Utils.combine("", "", "", ""), is("/"));
        assertThat(Utils.combine("/a", "", "", ""), is("/a/"));
        assertThat(Utils.combine("", "/a", "", ""), is("/a"));
    }

}
