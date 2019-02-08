/*
 * Copyright (c) 2016 by Qiang Wang.
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import com.github.egateam.IntSpan;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
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
        String               fileName = Utils.expendResource("chr.sizes");
        Map<String, Integer> lengthOf = Utils.readSizes(fileName);

        Assert.assertTrue(lengthOf.size() == 16);
        Assert.assertTrue(lengthOf.get("II") == 813184);
    }

    @Test
    public void testReadSizesRemove() throws Exception {
        String               fileName = Utils.expendResource("chr.chr.sizes");
        Map<String, Integer> lengthOf = Utils.readSizes(fileName, true);

        Assert.assertTrue(lengthOf.size() == 16);
        Assert.assertTrue(lengthOf.get("II") == 813184);
    }

    @Test
    public void testReadLines() throws Exception {
        String       fileName = Utils.expendResource("chr.sizes");
        List<String> lines    = Utils.readLines(fileName);

        Assert.assertTrue(lines.size() == 16);
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

    @Test
    public void testIntSpanConverters() throws Exception {
        Map<String, String> singleRunlist = new HashMap<>();
        singleRunlist.put("II", "327069-327703");
        singleRunlist.put("VII", "778784-779515,878539-879235");

        // toIntSpan
        Map<String, IntSpan> singleSet = Utils.toIntSpan(singleRunlist);

        Assert.assertEquals(singleSet.size(), 2);

        for ( Map.Entry<?, ?> entry : singleRunlist.entrySet() ) {
            String key   = entry.getKey().toString();
            String value = entry.getValue().toString();

            Assert.assertTrue(singleSet.get(key).toString().equals(value));
        }

        // toRunlist
        Map<String, String> singleRunlistAgain = Utils.toRunlist(singleSet);

        Assert.assertEquals(singleRunlistAgain.size(), 2);

        for ( Map.Entry<?, ?> entry : singleRunlist.entrySet() ) {
            String key   = entry.getKey().toString();
            String value = entry.getValue().toString();

            Assert.assertTrue(singleRunlistAgain.get(key).equals(value));
        }

    }

    @Test
    public void testBegEnd() {
        int[]   begEnd     = Utils.begEnd(10, 1);
        Assert.assertEquals(begEnd[0], 1);
        Assert.assertEquals(begEnd[1], 10);
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
