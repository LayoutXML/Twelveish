package com.layoutxml.twelveish.objects;

public class Triple<T, T1, T2> {

    public final T first;
    public final T1 second;
    public final T2 third;

    public Triple(T first, T1 second, T2 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
