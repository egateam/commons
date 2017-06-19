/*
 * Copyright (c) 2017 by Qiang Wang.
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class Ovlp {
    private String  fId;
    private String  gId;
    private Integer len;
    private Double  idt;

    private Integer fStr;
    private Integer fB;
    private Integer fE;
    private Integer fLen;

    private Integer gStr;
    private Integer gB;
    private Integer gE;
    private Integer gLen;

    private String contained;

    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public String getgId() {
        return gId;
    }

    public void setgId(String gId) {
        this.gId = gId;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public Double getIdt() {
        return idt;
    }

    public void setIdt(Double idt) {
        this.idt = idt;
    }

    public Integer getfStr() {
        return fStr;
    }

    public void setfStr(Integer fStr) {
        this.fStr = fStr;
    }

    public Integer getfB() {
        return fB;
    }

    public void setfB(Integer fB) {
        this.fB = fB;
    }

    public Integer getfE() {
        return fE;
    }

    public void setfE(Integer fE) {
        this.fE = fE;
    }

    public Integer getfLen() {
        return fLen;
    }

    public void setfLen(Integer fLen) {
        this.fLen = fLen;
    }

    public Integer getgStr() {
        return gStr;
    }

    public void setgStr(Integer gStr) {
        this.gStr = gStr;
    }

    public Integer getgB() {
        return gB;
    }

    public void setgB(Integer gB) {
        this.gB = gB;
    }

    public Integer getgE() {
        return gE;
    }

    public void setgE(Integer gE) {
        this.gE = gE;
    }

    public Integer getgLen() {
        return gLen;
    }

    public void setgLen(Integer gLen) {
        this.gLen = gLen;
    }

    public String getContained() {
        return contained;
    }

    public void setContained(String contained) {
        this.contained = contained;
    }

    /**
     * Constructs an empty object.
     */
    public Ovlp() {
    }

    public Ovlp(String fId, String gId, Integer len, Double idt,
                Integer fStr, Integer fB, Integer fE, Integer fLen,
                Integer gStr, Integer gB, Integer gE, Integer gLen,
                String contained) {
        this.fId = fId;
        this.gId = gId;
        this.len = len;
        this.idt = idt;
        this.fStr = fStr;
        this.fB = fB;
        this.fE = fE;
        this.fLen = fLen;
        this.gStr = gStr;
        this.gB = gB;
        this.gE = gE;
        this.gLen = gLen;
        this.contained = contained;
    }

    public Ovlp(String line) {
        parseOvlpLine(line);
    }

    public void parseOvlpLine(String line) {
        String[] fields = Utils.splitTsvFields(line);

        if ( fields.length == 13 ) {
            this.fId = fields[0];
            this.gId = fields[1];
            this.len = Integer.valueOf(fields[2]);
            this.idt = Double.valueOf(fields[3]);

            this.fStr = Integer.valueOf(fields[4]) == 0 ? 0 : 1;
            this.fB = Integer.valueOf(fields[5]);
            this.fE = Integer.valueOf(fields[6]);
            this.fLen = Integer.valueOf(fields[7]);

            this.gStr = Integer.valueOf(fields[8]) == 0 ? 0 : 1;
            this.gB = Integer.valueOf(fields[9]);
            this.gE = Integer.valueOf(fields[10]);
            this.gLen = Integer.valueOf(fields[11]);

            this.contained = fields[12];
        } else {
            throw new IllegalArgumentException("Number of fields should be 13");
        }
    }

    public void parsePafLine(String line) {
        String[] fields = Utils.splitTsvFields(line);

        if ( fields.length == 13 ) {
            this.fId = fields[0];
            this.gId = fields[5];
            this.len = Integer.valueOf(fields[10]);
            this.idt = Double.valueOf(fields[9]) / Double.valueOf(fields[10]);

            this.fStr = 0;
            this.fB = Integer.valueOf(fields[2]) + 1;
            this.fE = Integer.valueOf(fields[3]) + 1;
            this.fLen = Integer.valueOf(fields[1]);

            if ( Objects.equals(fields[4], "+") ) {
                this.gStr = 0;
                this.gB = Integer.valueOf(fields[7]) + 1;
                this.gE = Integer.valueOf(fields[8]) + 1;
            } else {
                this.gStr = 1;
                this.gB = Integer.valueOf(fields[8]) + 1;
                this.gE = Integer.valueOf(fields[7]) + 1;
            }
            this.gLen = Integer.valueOf(fields[6]);

            this.contained = "overlap";

            if ( this.fB == 1 ) {
                this.fB = 0;
            }
            if ( this.fE == 1 ) {
                this.fE = 0;
            }
            if ( this.gB == 1 ) {
                this.gB = 0;
            }
            if ( this.gE == 1 ) {
                this.gE = 0;
            }
        } else {
            throw new IllegalArgumentException("Number of fields should be 12 or more");
        }
    }

    public String toOvlpLine() {
        return String.format(
            "%s\t%s\t%d\t%.3f"
                + "\t%d\t%d\t%d\t%d"
                + "\t%d\t%d\t%d\t%d"
                + "\t%s",
            fId, gId, len, idt,
            fStr, fB, fE, fLen,
            gStr, gB, gE, gLen,
            contained
        );
    }

    @Override
    public String toString() {
        return toOvlpLine();
    }

}
