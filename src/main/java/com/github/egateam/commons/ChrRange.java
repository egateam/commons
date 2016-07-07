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

import java.util.*;
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

    private boolean isValid = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public int getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
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

    public boolean isValid() {
        return isValid;
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
        isValid = true;
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
            isValid = true;
        }

        if ( chr != null && start != null ) {
            if ( end == null ) {
                end = start;
            }
            intSpan.addPair(start, end);
        } else {
            String[] parts = header.split("\\s+", 2);
            chr = parts[0];
            isEmpty = false;
            isValid = false;
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

    public void standardize(boolean onlyEssential) {
        if ( chr != null && start != null ) {
            if ( strand == null ) {
                strand = "+";
            }
            if ( Objects.equals(strand, "1") ) {
                strand = "+";
            }
            if ( Objects.equals(strand, "-1") ) {
                strand = "-";
            }
        }

        if ( onlyEssential ) {
            others = new HashMap<>();
        }
    }

    public void standardize() {
        standardize(false);
    }

    private String encode() {
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

        if ( !others.isEmpty() ) {
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

    @Override
    public String toString() {
        return encode();
    }
}
