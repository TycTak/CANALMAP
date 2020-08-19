package com.tyctak.map.entities;

public class _Association {
    public enum enmType {
        IsNone,
        IsBoth,
        IsImage,
        IsText
    }

    public enum enmSize {
        small,
        medium,
        large
    }

    public String Name;
    public Object Marker;
}
