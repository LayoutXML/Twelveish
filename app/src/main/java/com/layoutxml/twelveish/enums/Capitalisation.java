package com.layoutxml.twelveish.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Capitalisation {
    TITLE_CASE(0, "Title Case"),
    UPPERCASE(1, "UPPERCASE"),
    LOWERCASE(2, "lowercase"),
    FIRST_TITLE_CASE(3, "First word title case"),
    LINE_TITLE_CASE(4, "First word in every\nLine title case");

    private final int index;
    private final String label;
}
