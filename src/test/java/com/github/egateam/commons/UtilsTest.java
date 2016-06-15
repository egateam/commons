/*
 * Copyright (c) 2016 by Qiang Wang.
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class UtilsTest {
    @Test
    public void testReadSizes() throws Exception {
        File                 fileName = new ExpandResource("chr.sizes").invokeFile();
        Map<String, Integer> lengthOf = Utils.readSizes(fileName);

        assertTrue(lengthOf.size() == 16);
        assertTrue(lengthOf.get("II") == 813184);
    }

    @Test
    public void testReadLines() throws Exception {
        File         fileName = new ExpandResource("chr.sizes").invokeFile();
        List<String> lines = Utils.readLines(fileName);

        assertTrue(lines.size() == 16);
    }

}
