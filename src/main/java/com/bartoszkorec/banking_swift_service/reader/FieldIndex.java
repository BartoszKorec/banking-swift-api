package com.bartoszkorec.banking_swift_service.reader;

import lombok.Getter;

@Getter
public enum FieldIndex {

    ISO2CODE(0),
    SWIFT_CODE(1),
    BANK_NAME(3),
    ADDRESS(4),
    COUNTRY(6);

    private final int index;

    FieldIndex(int index) {
        this.index = index;
    }
}
