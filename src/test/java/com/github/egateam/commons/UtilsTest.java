/*
 * Copyright (c) 2016 by Qiang Wang.
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UtilsTest {

    // Store the original standard out before changing it.
    private final PrintStream           originalStdout = System.out;
    private final PrintStream           originalStderr = System.err;
    private       ByteArrayOutputStream stdoutContent  = new ByteArrayOutputStream();
    private       ByteArrayOutputStream stderrContent  = new ByteArrayOutputStream();

    @BeforeMethod
    public void beforeTest() {
        // Redirect all System.out to stdoutContent.
        System.setOut(new PrintStream(this.stdoutContent));
        System.setErr(new PrintStream(this.stderrContent));
    }

    @Test
    public void testReadSizes() throws Exception {
        File                 fileName = new ExpandResource("chr.sizes").invokeFile();
        Map<String, Integer> lengthOf = Utils.readSizes(fileName);

        Assert.assertTrue(lengthOf.size() == 16);
        Assert.assertTrue(lengthOf.get("II") == 813184);
    }

    @Test
    public void testReadLines() throws Exception {
        File         file  = new ExpandResource("chr.sizes").invokeFile();
        List<String> lines = Utils.readLines(file);

        Assert.assertTrue(lines.size() == 16);

        String fileName = new ExpandResource("chr.sizes").invoke();
        Assert.assertTrue(fileName.contains("sizes"));
    }

    @Test
    public void testWriteLines() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("1");
        lines.add("2");
        lines.add("xyz");

        Utils.writeLines("stdout", lines);
        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 3, "line count");
        Assert.assertTrue(this.stdoutContent.toString().contains("xyz"), "contents");
    }

    @AfterMethod
    public void afterTest() {
        // Put back the standard out.
        System.setOut(this.originalStdout);
        System.setErr(this.originalStderr);

        // Clear the stdoutContent.
        this.stdoutContent = new ByteArrayOutputStream();
        this.stderrContent = new ByteArrayOutputStream();
    }

}
