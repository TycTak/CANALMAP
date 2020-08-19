package com.tyctak.map.entities;

public class _Role {
    public enum enmRoles {
        Publisher,
        Administrator,
        Reviewer,
        Premium
    }

    public boolean isAuthorised = false;
    public int Radius = 0;
    public double Longitude = 0.0;
    public double Latitude = 0.0;
}