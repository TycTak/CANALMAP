package com.tyctak.map.entities;

import java.util.UUID;

public class _MySettings {
    public enum enmCentreMyEntity {
        following,
        active,
        inactive
    }

    public enum enmFilter {
        all,
        entitytrader,
        favourite,
        socialmedia,
        none
    }

    public enum enmEntityMoved {
        PositionNotSent,
        StoppedNotSent,
        Sent
    }

    public _MySettings() {
        UUID uuid = UUID.randomUUID();
        EntityGuid = uuid.toString().replace("-", "");
    }

    public Integer LastZoomLevel = 9;
    public Double CentreLongitude = -1.321500;
    public Double CentreLatitude = 52.270603;
    public String EntityGuid;
    public Boolean IsDebug = false;
    public enmCentreMyEntity CentreMyEntity = enmCentreMyEntity.inactive;
    public Integer UpdateGps = 4;
    public Integer Accuracy = 0;
    public long StoppedTrigger;
    public long LastServerDate;
    public long CurrentServerDate;
    public boolean SendPosition = false;
    public enmFilter Filter = enmFilter.none;
    public boolean FilterPoi = false;
    public boolean Favourite = false;
    public boolean Shopping = false;
    public boolean SocialMedia = false;
    public long PremiumContent = 0;
    public boolean IsPremium = false;
    public enmEntityMoved EntityMoved = enmEntityMoved.Sent;
    public boolean Paused = false;
    public boolean Imported = false;
    public String InitQ;
    public String SessionPassword;
    public long LastLocalDate;
    public long CurrentLocalDate;
    public boolean IsPublisher = false;
    public long PublisherExpiry = 0;
    public double PublisherLongitude;
    public double PublisherLatitude;
    public int PublisherRadius = 0;
    public boolean IsReviewer = false;
    public long ReviewerExpiry = 0;
    public double ReviewerLongitude = 0.0;
    public double ReviewerLatitude = 0.0;
    public int ReviewerRadius = 0;
    public boolean IsAdministrator = false;
    public boolean IsSecurityPremium = false;
    public String SecurityFeatures;
    public String EncryptedEntityGuid;
}