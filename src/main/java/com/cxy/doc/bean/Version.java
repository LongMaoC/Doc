package com.cxy.doc.bean;

public class Version implements Comparable<Version> {
    public Float key;
    public String value;

    public Version(Float key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(Version o) {
        if (key > o.key) {
            return 1;
        } else if (key < o.key) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Version{" +
                "key=" + key +
                ", value='" + value + '\'' +
                '}';
    }
}
