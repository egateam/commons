/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import com.github.egateam.IntSpan;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChrRangeTest {
    private static class TestData {
        final String header;
        Map<String, String> expected = new HashMap<>();

        TestData(String header, Map<String, String> expected) {
            this.header = header;
            this.expected = expected;
        }
    }

    private static final TestData[] fasTests =
        {
            new TestData("S288c.I(+):27070-29557|species=yeast", new HashMap<String, String>() {{
                put("name", "S288c");
                put("chr", "I");
                put("strand", "+");
                put("start", "27070");
                put("end", "29557");
                put("species", "yeast");
            }}),
            new TestData("S288c.I(+):27070-29557", new HashMap<String, String>() {{
                put("name", "S288c");
                put("chr", "I");
                put("strand", "+");
                put("start", "27070");
                put("end", "29557");
            }}),
            new TestData("I(+):90-150", new HashMap<String, String>() {{
                put("chr", "I");
                put("strand", "+");
                put("start", "90");
                put("end", "150");
            }}),
            new TestData("S288c.I(-):190-200", new HashMap<String, String>() {{
                put("name", "S288c");
                put("chr", "I");
                put("strand", "-");
                put("start", "190");
                put("end", "200");
            }}),
            new TestData("I:1-100", new HashMap<String, String>() {{
                put("chr", "I");
                put("start", "1");
                put("end", "100");
            }}),
            new TestData("I:100", new HashMap<String, String>() {{
                put("chr", "I");
                put("start", "100");
                put("end", "100");
            }}),
            new TestData("S288c", new HashMap<String, String>() {{
                put("name", "S288c");
            }}),
        };

    private static final TestData[] faTests =
        {
            new TestData("S288c", new HashMap<String, String>() {{
                put("name", "S288c");
            }}),
            new TestData("S288c The baker's yeast", new HashMap<String, String>() {{
                put("name", "S288c");
            }}),
            new TestData("1:-100", new HashMap<String, String>() {{
                put("name", "1:-100");
            }}),
        };

    private static class TestData2 {
        final String   header;
        final ChrRange expected;

        TestData2(String header, ChrRange expected) {
            this.header = header;
            this.expected = expected;
        }
    }

    private static final TestData2[] fasTests2 =
        {
            new TestData2("I:1-100", new ChrRange("I", 1, 100)),
            new TestData2("I:100", new ChrRange("I", 100, 100)),
            new TestData2("II:100-101", new ChrRange("II", 100, 101)),
        };

    @Test(description = "fas headers")
    public void testFasHeader() throws Exception {
        for ( TestData t : fasTests ) {
            ChrRange            chrRange = new ChrRange(t.header);
            Map<String, String> expected = t.expected;

            Assert.assertFalse(chrRange.isEmpty());

            // decode
            if ( expected.containsKey("name") ) {
                Assert.assertEquals(chrRange.getName(), expected.get("name"));
            }
            if ( expected.containsKey("chr") ) {
                Assert.assertEquals(chrRange.getChr(), expected.get("chr"));
            }
            if ( expected.containsKey("strand") ) {
                Assert.assertEquals(chrRange.getStrand(), expected.get("strand"));
            }
            if ( expected.containsKey("start") ) {
                Assert.assertEquals(chrRange.getStart(), Integer.parseInt(expected.get("start")));
            }
            if ( expected.containsKey("end") ) {
                Assert.assertEquals(chrRange.getEnd(), Integer.parseInt(expected.get("end")));

                IntSpan intSpan = new IntSpan();
                intSpan.addPair(
                    Integer.parseInt(expected.get("start")),
                    Integer.parseInt(expected.get("end"))
                );
                Assert.assertEquals(chrRange.getIntSpan().toString(), intSpan.toString());
            }
            if ( expected.containsKey("species") ) {
                Assert.assertEquals(chrRange.getOthers().get("species"), expected.get("species"));
            }

            // encode
            Assert.assertEquals(chrRange.encode(), t.header);
            if ( !expected.containsKey("species") ) {
                Assert.assertEquals(chrRange.encode(true), t.header);
            }
        }
    }

    @Test(description = "fas headers take 2")
    public void testFasHeader2() throws Exception {
        for ( TestData2 t : fasTests2 ) {
            ChrRange chrRange = new ChrRange(t.header);
            ChrRange expected = t.expected;

            Assert.assertEquals(chrRange.toString(), expected.toString());
        }
    }

    @Test(description = "fa headers")
    public void testFaHeader() throws Exception {
        for ( TestData t : faTests ) {
            ChrRange            chrRange = new ChrRange(t.header);
            Map<String, String> expected = t.expected;

            // decode
            if ( expected.containsKey("name") ) {
                Assert.assertEquals(chrRange.getName(), expected.get("name"));
            }
        }
    }

    @Test(description = "headers from file")
    public void testReadLinesChr() throws Exception {
        File         file  = Utils.expendResourceFile("S288c.txt");
        List<String> lines = Utils.readLines(file);

        Assert.assertTrue(lines.size() == 6);

        for ( String s : lines ) {
            ChrRange chrRange = new ChrRange(s);
            Assert.assertFalse(chrRange.isEmpty());
            Assert.assertEquals(chrRange.encode(), s);
        }
    }

}
