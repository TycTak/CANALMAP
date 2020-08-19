package com.tyctak.map.entities;

public class _File {
    private String name;
    private byte[] contents;
    private String reference;

    public _File(String pName, String pReference) {
        name = pName;
        reference = pReference;
    }

    public _File(String pName, byte[] pContents, String pReference) {
        name = pName;
        contents = pContents;
        reference = pReference;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] value) {
        contents = value;
    }

    public String getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }
}
