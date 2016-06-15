/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * @author Qiang Wang
 * @since 1.7
 */

package com.github.egateam.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChrRange {
    private String  name;
    private String  chrName;
    private String  chrStrand;
    private Integer chrStart;
    private Integer chrEnd;
    private Map<String, String> others  = new HashMap<>();
    private boolean             isEmpty = true;

    public String getName() {
        return name;
    }

    public String getChrName() {
        return chrName;
    }

    public String getChrStrand() {
        return chrStrand;
    }

    public int getChrStart() {
        return chrStart;
    }

    public int getChrEnd() {
        return chrEnd;
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

    public ChrRange(String chrName, int chrStart, int chrEnd) {
        this.chrName = chrName;
        this.chrStart = chrStart;
        this.chrEnd = chrEnd;
        isEmpty = false;
    }

    private static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public void decode(String header) throws RuntimeException {
        Pattern p = Pattern.compile("(?xi)" +
            "(?:(?<name>[\\w_]+)\\.)?" +
            "(?<chrName>[\\w-]+)" +
            "(?:\\((?<chrStrand>.+)\\))?" +
            "[:]" +
            "(?<chrStart>\\d+)" +
            "[_\\-]?" +
            "(?<chrEnd>\\d+)?"
        );
        Matcher m = p.matcher(header);

        if ( m.find() ) {
            name = m.group("name");
            chrName = m.group("chrName");
            chrStrand = m.group("chrStrand");
            chrStart = tryParse(m.group("chrStart"));
            chrEnd = tryParse(m.group("chrEnd"));
            isEmpty = false;
        }

        if ( chrName != null && chrStart != null ) {
            if ( chrEnd == null ) {
                chrEnd = chrStart;
            }
        } else {
            String[] parts = header.split("\\s+");
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
            if ( chrName != null ) {
                header += "." + chrName;
            }
        } else if ( chrName != null ) {
            header += chrName;
        }

        if ( chrStrand != null ) {
            header += "(" + chrStrand + ")";
        }

        if ( chrStart != null ) {
            header += ":" + chrStart;
            if ( !chrStart.equals(chrEnd) ) {
                header += "-" + chrEnd;
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
