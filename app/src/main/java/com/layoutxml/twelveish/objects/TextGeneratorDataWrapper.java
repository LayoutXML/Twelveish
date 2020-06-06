package com.layoutxml.twelveish.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TextGeneratorDataWrapper {
    private String mainText;
    private float baseXCoordinate;
    private float baseYCoordinate;
    private float textSize;
}
