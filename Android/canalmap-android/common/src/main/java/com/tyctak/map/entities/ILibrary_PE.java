package com.tyctak.map.entities;

public interface ILibrary_PE {
    public enum enmPermissionGranted {
        Granted,
        Waiting,
        Refused,
        Standby
    }

    enmPermissionGranted checkPermissions(Boolean forceRequest, String[] permissions);
    boolean verifyPermissions(int[] grantResults);
}