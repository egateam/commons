/*
 * Copyright (c) 2017 by Qiang Wang.
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import org.testng.Assert;
import org.testng.annotations.Test;

public class OvlpTest {

    private static class TestData {
        final String line;
        final Ovlp   expected;

        TestData(String line, Ovlp expected) {
            this.line = line;
            this.expected = expected;
        }
    }

    private static final TestData[] lineOvlpTests =
        {
            new TestData(
                "anchor/282/0_2680\tanchor/306/0_2073\t36\t1.000\t0\t2644\t2680\t2680\t0\t0\t36\t2073\toverlap",
                new Ovlp("anchor/282/0_2680", "anchor/306/0_2073", 36, 1.000, 0, 2644, 2680, 2680, 0, 0, 36, 2073, "overlap")
            ),
            new TestData(
                "anchor148_9124\tpac7556_20928\t8327\t0.890\t0\t797\t9124\t9124\t0\t0\t8581\t20928\toverlap",
                new Ovlp("anchor148_9124", "pac7556_20928", 8327, 0.890, 0, 797, 9124, 9124, 0, 0, 8581, 20928, "overlap")
            ),
        };

    @Test(description = "ovlp lines")
    public void testOvlpLine() throws Exception {
        for ( TestData t : lineOvlpTests ) {
            Ovlp ovlp     = new Ovlp(t.line);
            Ovlp expected = t.expected;

            Assert.assertEquals(ovlp.toString(), t.line);
            Assert.assertEquals(ovlp.toString(), expected.toString());
        }
    }

    private static final TestData[] pafLineTests =
        {
            new TestData(
                "long/5059/0_25030\t25030\t6400\t14738\t+\tlong/9413/0_8928\t8928\t188\t8927\t1427\t8739\t255\tcm:i:168",
                new Ovlp("long/5059/0_25030", "long/9413/0_8928", 8739, 0.163, 0, 6401, 14739, 25030, 0, 189, 8928, 8928, "overlap")
            ),
        };

    @Test(description = "paf lines")
    public void testPafLine() throws Exception {
        for ( TestData t : pafLineTests ) {
            Ovlp ovlp     = new Ovlp();
            ovlp.parsePafLine(t.line);
            Ovlp expected = t.expected;

            Assert.assertEquals(ovlp.toString(), expected.toString());
        }
    }

}
