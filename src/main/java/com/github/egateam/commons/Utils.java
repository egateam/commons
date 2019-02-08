/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import com.github.egateam.IntSpan;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Utils {

    private static File fileNameToFile(String value) {
        File file = new File(value);
        if ( !file.isFile() ) {
            throw new RuntimeException(String.format("The input file [%s] doesn't exist.", value));
        }
        return file;
    }

    /**
     * Returns a Map as "chromosome - length" pairs
     *
     * @param fileName chr.size
     * @param remove   remove "chr0" from chromosome names
     * @return a Map
     * @throws Exception
     */
    public static Map<String, Integer> readSizes(String fileName, boolean remove) throws Exception {
        HashMap<String, Integer> lengthOf = new HashMap<>();
        File                     file     = fileNameToFile(fileName);

        try ( BufferedReader reader = new BufferedReader(new FileReader(file)) ) {
            String line;
            while ( (line = reader.readLine()) != null ) {
                line = line.trim();
                String[] fields = line.split("\\t");
                if ( fields.length == 2 ) {
                    if ( remove ) fields[0] = fields[0].replaceFirst("chr0?", "");

                    lengthOf.put(fields[0], Integer.parseInt(fields[1]));
                }
            }
        }

        return lengthOf;
    }

    /**
     * Returns a Map as "chromosome - length" pairs
     *
     * @param fileName chr.size
     * @return a Map
     * @throws Exception
     */
    public static Map<String, Integer> readSizes(String fileName) throws Exception {
        return readSizes(fileName, false);
    }

    /**
     * Returns a List (new lines removed) containing file content
     *
     * @param fileName the name of input file
     * @return List
     * @throws Exception
     */
    public static List<String> readLines(String fileName) throws Exception {
        List<String> lines;
        if ( fileName.toLowerCase().equals("stdin") ) {
            lines = IOUtils.readLines(System.in);
        } else {
            lines = FileUtils.readLines(new File(fileName));
        }
        return lines;
    }

    /**
     * Write lines (appending new line to each strings) to file or screen
     *
     * @param fileName desired output filename. [stdout] for screen
     * @param lines    file contents.
     * @throws Exception
     */
    public static void writeLines(String fileName, List<String> lines) throws Exception {
        if ( fileName.toLowerCase().equals("stdout") )
            for ( String line : lines ) {
                System.out.println(line);
            }
        else {
            FileUtils.writeLines(new File(fileName), "UTF-8", lines, "\n");
        }
    }

    /**
     * Convert values of a Map from runlists to IntSpan objects
     *
     * @param map Map
     * @return a Map
     * @throws AssertionError
     */
    public static Map<String, IntSpan> toIntSpan(Map<String, String> map) throws AssertionError {
        Map<String, IntSpan> setOf = new HashMap<>();

        for ( Map.Entry<?, ?> entry : map.entrySet() ) {
            String chr = entry.getKey().toString();

            IntSpan intspan = new IntSpan(entry.getValue().toString());
            setOf.put(chr, intspan);
        }

        return setOf;
    }

    /**
     * Convert values of a Map from IntSpan objects to runlists
     *
     * @param map Map
     * @return a Map
     * @throws AssertionError
     */
    public static Map<String, String> toRunlist(Map<String, IntSpan> map) throws AssertionError {
        Map<String, String> runlistOf = new HashMap<>();

        for ( Map.Entry<?, ?> entry : map.entrySet() ) {
            String chr = entry.getKey().toString();

            String runlist = entry.getValue().toString();
            runlistOf.put(chr, runlist);
        }

        return runlistOf;
    }

    public static String[] splitTsvFields(String line) {
        line = line.trim();
        return line.split("\\t", -1);
    }

    public static String expendResource(String fileName) throws Exception {
        // http://stackoverflow.com/questions/5529532/how-to-get-a-test-resource-file
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if ( url != null ) {
            return new File(url.getPath()).toString();
        } else {
            throw new IOException(String.format("Resource file [%s] doesn't exist", fileName));
        }
    }

    /**
     * @param beg begin
     * @param end end
     * @return int[]{beg, end}
     */
    public static int[] begEnd(int beg, int end) {
        if ( beg > end ) {
            int temp = beg;
            beg = end;
            end = temp;
        }

        if ( beg == 0 ) {
            beg = 1;
        }

        return new int[]{beg, end};
    }

    /**
     * @param tier_of tiers of covered regions
     * @param beg begin
     * @param end end
     */
    public static void bumpCoverage(Map<Integer, IntSpan> tier_of, int beg, int end) {
        int[]   begEnd     = Utils.begEnd(beg, end);
        IntSpan intSpanNew = new IntSpan(begEnd[0], begEnd[1]);

        int max_tier = Collections.max(tier_of.keySet());

        // reach max coverage in full sequence
        if ( tier_of.get(-1).equals(tier_of.get(max_tier)) ) {
            return;
        }

        // remove intSpanNew from uncovered regions
        tier_of.get(0).subtract(intSpanNew);

        for ( int i = 1; i <= max_tier; i++ ) {
            IntSpan intSpanI = tier_of.get(i).intersect(intSpanNew);
            tier_of.get(i).add(intSpanNew);

            int j = i + 1;
            if ( j > max_tier ) {
                break;
            }

            intSpanNew = intSpanI.copy();
        }
    }

    /**
     * @param tier_of tiers of covered regions
     */
    public static void uniqCoverage(Map<Integer, IntSpan> tier_of) {
        int max_tier = Collections.max(tier_of.keySet());

        for ( int i = 1; i < max_tier; i++ ) {
            IntSpan intSpanCur  = tier_of.get(i);
            IntSpan intSpanNext = tier_of.get(i + 1);
            intSpanCur.subtract(intSpanNext);
        }
    }

}
