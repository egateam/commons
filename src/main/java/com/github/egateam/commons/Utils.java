/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import com.github.egateam.IntSpan;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
     * @param file   a File object for chr.size
     * @param remove remove "chr0" from chromosome names
     * @return a Map
     * @throws Exception
     */
    public static Map<String, Integer> readSizes(File file, boolean remove) throws Exception {
        HashMap<String, Integer> lengthOf = new HashMap<>();

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
     * @param file a File object for chr.size
     * @return a Map
     * @throws Exception
     */
    public static Map<String, Integer> readSizes(File file) throws Exception {
        return readSizes(file, false);
    }

    /**
     * Returns a Map as "chromosome - length" pairs
     *
     * @param fileName chr.size
     * @return a Map
     * @throws Exception
     */
    public static Map<String, Integer> readSizes(String fileName) throws Exception {
        return readSizes(fileNameToFile(fileName), false);
    }

    /**
     * Returns a List (new lines removed) containing file content
     *
     * @param file a File object
     * @return List
     * @throws Exception
     */
    public static List<String> readLines(File file) throws Exception {
        List<String> lines = new ArrayList<>();

        try ( BufferedReader reader = new BufferedReader(new FileReader(file)) ) {
            String line;
            while ( (line = reader.readLine()) != null ) {
                line = line.trim();
                lines.add(line);
            }
        }

        return lines;
    }

    /**
     * Returns a List (new lines removed) containing file content
     *
     * @param fileName the name of input file
     * @return List
     * @throws Exception
     */
    public static List<String> readLines(String fileName) throws Exception {
        return readLines(fileNameToFile(fileName));
    }

    /**
     * Write lines (appending new line to each strings) to file or screen
     *
     * @param fileName desired output filename. [stdout] for screen
     * @param lines    file contents.
     * @throws Exception
     */
    public static void writeLines(String fileName, List<String> lines) throws Exception {
        if ( fileName.equals("stdout") )
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

    public static String expendResource(String fileName) throws Exception {
        // http://stackoverflow.com/questions/5529532/how-to-get-a-test-resource-file
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if ( url != null ) {
            return new File(url.getPath()).toString();
        } else {
            throw new IOException(String.format("Resource file [%s] doesn't exist", fileName));
        }
    }

    public static File expendResourceFile(String fileName) throws Exception {
        // http://stackoverflow.com/questions/5529532/how-to-get-a-test-resource-file
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if ( url != null ) {
            return new File(url.getPath());
        } else {
            throw new IOException(String.format("Resource file [%s] doesn't exist", fileName));
        }
    }
}
