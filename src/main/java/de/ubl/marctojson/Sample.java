package de.ubl.marctojson;

import java.nio.charset.Charset;

public class Sample {

    public static void main(String... args) {
        String s = "Hello";
        Charset cs = Charset.forName("LATIN-1");
        System.out.println(s.getBytes(cs));
    }
}
