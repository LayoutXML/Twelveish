package com.layoutxml.twelveish.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DateOrder {
    MDY(0),
    DMY(1),
    YMD(2),
    YDM(3);

    private final int index;
}
