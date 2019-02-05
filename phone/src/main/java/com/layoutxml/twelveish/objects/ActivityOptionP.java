/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 */

package com.layoutxml.twelveish.objects;

public class ActivityOptionP {

    private String name;
    private Integer icon;
    private Class activity;
    private String extra;
    private String extra2;

    public Class getActivity() {
        return activity;
    }

    public void setActivity(Class activity) {
        this.activity = activity;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
