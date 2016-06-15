/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ChrRangeTest {
    private static class TestData {
        String header;
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
                put("chrName", "I");
                put("chrStrand", "+");
                put("chrStart", "27070");
                put("chrEnd", "29557");
                put("species", "yeast");
            }}),
            new TestData("S288c.I(+):27070-29557", new HashMap<String, String>() {{
                put("name", "S288c");
                put("chrName", "I");
                put("chrStrand", "+");
                put("chrStart", "27070");
                put("chrEnd", "29557");
            }}),
            new TestData("I(+):90-150", new HashMap<String, String>() {{
                put("chrName", "I");
                put("chrStrand", "+");
                put("chrStart", "90");
                put("chrEnd", "150");
            }}),
            new TestData("S288c.I(-):190-200", new HashMap<String, String>() {{
                put("name", "S288c");
                put("chrName", "I");
                put("chrStrand", "-");
                put("chrStart", "190");
                put("chrEnd", "200");
            }}),
            new TestData("I:1-100", new HashMap<String, String>() {{
                put("chrName", "I");
                put("chrStart", "1");
                put("chrEnd", "100");
            }}),
            new TestData("I:100", new HashMap<String, String>() {{
                put("chrName", "I");
                put("chrStart", "100");
                put("chrEnd", "100");
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
        String   header;
        ChrRange expected;

        TestData2(String header, ChrRange expected) {
            this.header = header;
            this.expected = expected;
        }
    }

    private static final TestData2[] fasTests2 =
        {
            new TestData2("I:1-100", new ChrRange("I", 1, 100)),
            new TestData2("I:100", new ChrRange("I", 100, 100)),
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
            if ( expected.containsKey("chrName") ) {
                Assert.assertEquals(chrRange.getChrName(), expected.get("chrName"));
            }
            if ( expected.containsKey("chrStrand") ) {
                Assert.assertEquals(chrRange.getChrStrand(), expected.get("chrStrand"));
            }
            if ( expected.containsKey("chrStart") ) {
                Assert.assertEquals(chrRange.getChrStart(), Integer.parseInt(expected.get("chrStart")));
            }
            if ( expected.containsKey("chrEnd") ) {
                Assert.assertEquals(chrRange.getChrEnd(), Integer.parseInt(expected.get("chrEnd")));
            }
            if ( expected.containsKey("species") ) {
                Assert.assertEquals(chrRange.getOthers().get("species"), expected.get("species"));
            }

            // encode
            Assert.assertEquals(chrRange.encode(), t.header);
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
}
