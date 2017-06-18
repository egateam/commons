/*
 * Copyright (c) 2017 by Qiang Wang.
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Objects;

public class OvlpTest {

    private static class TestData {
        final String line;
        final Ovlp   expected;

        TestData(String line, Ovlp expected) {
            this.line = line;
            this.expected = expected;
        }
    }

    private static final TestData[] lineTests =
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

    @Test(description = "tsv lines")
    public void testLine() throws Exception {
        for ( TestData t : lineTests ) {
            Ovlp ovlp     = new Ovlp(t.line);
            Ovlp expected = t.expected;

            Assert.assertEquals(ovlp.toString(), t.line);
            Assert.assertEquals(ovlp.toString(), expected.toString());
        }
    }

}
