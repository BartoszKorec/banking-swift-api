package com.bartoszkorec.banking_swift_service.processing;

import java.util.Comparator;

public class EndsWithXXXComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        boolean s1EndsWithXXX = s1.endsWith("XXX");
        boolean s2EndsWithXXX = s2.endsWith("XXX");

        if (s1EndsWithXXX && !s2EndsWithXXX) {
            return -1;
        } else if (!s1EndsWithXXX && s2EndsWithXXX) {
            return 1;
        } else {
            return s1.compareTo(s2);
        }    }
}
