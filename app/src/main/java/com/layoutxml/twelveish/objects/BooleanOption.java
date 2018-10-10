/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.objects;

public class BooleanOption {

    private String name;
    private String key;
    private Boolean bool;
    private Boolean defaultOption;

    public Boolean getDefaultOption() {
        return defaultOption;
    }

    public void setDefaultOption(Boolean defaultOption) {
        this.defaultOption = defaultOption;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
