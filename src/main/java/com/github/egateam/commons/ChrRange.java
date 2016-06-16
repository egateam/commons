/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * @author Qiang Wang
 * @since 1.7
 */

package com.github.egateam.commons;

import com.github.egateam.IntSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class ChrRange {

    private String  name;
    private String  chr;
    private String  strand;
    private Integer start;
    private Integer end;
    @SuppressWarnings("CanBeFinal")
    private Map<String, String> others = new HashMap<>();

    private IntSpan intSpan = new IntSpan();
    private boolean isEmpty = true;

    public String getName() {
        return name;
    }

    public String getChr() {
        return chr;
    }

    public String getStrand() {
        return strand;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public IntSpan getIntSpan() {
        return intSpan;
    }

    public Map<String, String> getOthers() {
        return others;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public ChrRange(String header) throws RuntimeException {
        decode(header);
    }

    public ChrRange(String chr, int start, int end) throws NumberFormatException {
        if ( start <= 0 || end <= 0 ) {
            throw new NumberFormatException("Positions on chromosomes should larger than 0");
        }
        if ( start > end ) {
            throw new NumberFormatException("Start shouldn't larger than End");
        }

        this.chr = chr;
        this.start = start;
        this.end = end;
        this.intSpan.addPair(start, end);
        isEmpty = false;
    }

    private static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    private void decode(String header) throws RuntimeException {
        Pattern p = Pattern.compile("(?xi)" +
            "(?:(?<name>[\\w_]+)\\.)?" +
            "(?<chr>[\\w-]+)" +
            "(?:\\((?<strand>.+)\\))?" +
            "[:]" +
            "(?<start>\\d+)" +
            "[_\\-]?" +
            "(?<end>\\d+)?"
        );
        Matcher m = p.matcher(header);

        if ( m.find() ) {
            name = m.group("name");
            chr = m.group("chr");
            strand = m.group("strand");
            start = tryParse(m.group("start"));
            end = tryParse(m.group("end"));
            isEmpty = false;
        }

        if ( chr != null && start != null ) {
            if ( end == null ) {
                end = start;
            }
            intSpan.addPair(start, end);
        } else {
            String[] parts = header.split("\\s+", 2);
            name = parts[0];
            isEmpty = false;
        }

        if ( header.contains("|") ) {
            String   nonEssential = header.replaceFirst(".+\\|", "");
            String[] parts        = nonEssential.split(";");
            for ( String part : parts ) {
                String[] kv = part.split("=");
                if ( kv.length == 2 ) {
                    others.put(kv[0], kv[1]);
                }
            }
        }
    }

    public String encode(boolean onlyEssential) {
        String header = "";

        if ( name != null ) {
            header += name;
            if ( chr != null ) {
                header += "." + chr;
            }
        } else if ( chr != null ) {
            header += chr;
        }

        if ( strand != null ) {
            header += "(" + strand + ")";
        }

        if ( start != null ) {
            header += ":" + start;
            if ( !start.equals(end) ) {
                header += "-" + end;
            }
        }

        if ( !onlyEssential && !others.isEmpty() ) {
            List<String> parts = new ArrayList<>();
            for ( Map.Entry<String, String> entry : others.entrySet() ) {
                parts.add(entry.getKey() + "=" + entry.getValue());
            }

            String  nonEssential = "";
            boolean firstFlag    = true;
            for ( String str : parts ) {
                if ( firstFlag ) {
                    nonEssential += str;
                    firstFlag = false;
                } else {
                    nonEssential += ";" + str;
                }
            }

            if ( !nonEssential.isEmpty() ) {
                header += "|" + nonEssential;
            }
        }

        return header;
    }

    public String encode() {
        return encode(false);
    }

    @Override
    public String toString() {
        return encode();
    }
}
