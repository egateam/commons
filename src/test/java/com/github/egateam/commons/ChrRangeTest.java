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
import java.util.Objects;

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
        };

    @Test(description = "fas headers")
    public void testFasHeader() throws Exception {
        for ( TestData t : fasTests ) {
            ChrRange            chrRange    = new ChrRange(t.header);
            Map<String, String> expected    = t.expected;
            ChrRange            newChrRange = new ChrRange("DUMMY");

            Assert.assertFalse(chrRange.isEmpty());
            Assert.assertTrue(chrRange.isValid());

            // decode
            if ( expected.containsKey("name") ) {
                Assert.assertEquals(chrRange.getName(), expected.get("name"));
                newChrRange.setName(expected.get("name"));
            }
            if ( expected.containsKey("chr") ) {
                Assert.assertEquals(chrRange.getChr(), expected.get("chr"));
                newChrRange.setChr(expected.get("chr"));
            }
            if ( expected.containsKey("strand") ) {
                Assert.assertEquals(chrRange.getStrand(), expected.get("strand"));
                newChrRange.setStrand(expected.get("strand"));
            }
            if ( expected.containsKey("start") ) {
                Assert.assertEquals(chrRange.getStart(), Integer.parseInt(expected.get("start")));
                newChrRange.setStart(Integer.parseInt(expected.get("start")));
            }
            if ( expected.containsKey("end") ) {
                Assert.assertEquals(chrRange.getEnd(), Integer.parseInt(expected.get("end")));
                newChrRange.setEnd(Integer.parseInt(expected.get("end")));

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
            Assert.assertEquals(chrRange.toString(), t.header);
            if ( !expected.containsKey("species") ) {
                Assert.assertEquals(chrRange.toString(), t.header);
                Assert.assertEquals(newChrRange.toString(), t.header);
            }
        }
    }

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

    @Test(description = "fas headers take 2")
    public void testFasHeader2() throws Exception {
        for ( TestData2 t : fasTests2 ) {
            ChrRange chrRange = new ChrRange(t.header);
            ChrRange expected = t.expected;

            Assert.assertTrue(Objects.equals(chrRange.toString(), expected.toString()));

            chrRange.standardize();
            Assert.assertFalse(Objects.equals(chrRange.toString(), expected.toString()));

            expected.standardize();
            Assert.assertTrue(Objects.equals(chrRange.toString(), expected.toString()));
        }
    }

    private static final TestData[] faTests =
        {
            new TestData("IV", new HashMap<String, String>() {{
                put("chr", "IV");
            }}),
            new TestData("S288c The baker's yeast", new HashMap<String, String>() {{
                put("chr", "S288c");
            }}),
            new TestData("1:-100", new HashMap<String, String>() {{
                put("chr", "1:-100");
            }}),
        };

    @Test(description = "fa headers")
    public void testFaHeader() throws Exception {
        for ( TestData t : faTests ) {
            ChrRange            chrRange = new ChrRange(t.header);
            Map<String, String> expected = t.expected;

            // decode
            if ( expected.containsKey("chr") ) {
                Assert.assertEquals(chrRange.getChr(), expected.get("chr"));
            }
        }
    }

    @Test(description = "headers from file")
    public void testReadLinesChr() throws Exception {
        String       fileName = Utils.expendResource("S288c.txt");
        List<String> lines    = Utils.readLines(fileName);

        Assert.assertEquals(lines.size(), 7);

        for ( String s : lines ) {
            ChrRange chrRange = new ChrRange(s);
            Assert.assertFalse(chrRange.isEmpty());
            Assert.assertEquals(chrRange.toString(), s);
        }
    }

}
