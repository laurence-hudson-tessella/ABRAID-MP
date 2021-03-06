package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.geonames.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a GeoName, as returned by the GeoNames web service.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoName {
    @JsonProperty(value = "geonameId")
    private Integer geoNameId;
    @JsonProperty(value = "fcode")
    private String featureCode;
    private GeoNameStatus status;

    public GeoName() {
    }

    public GeoName(Integer geoNameId, String featureCode) {
        this.geoNameId = geoNameId;
        this.featureCode = featureCode;
    }

    public Integer getGeoNameId() {
        return geoNameId;
    }

    public void setGeoNameId(Integer geoNameId) {
        this.geoNameId = geoNameId;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public GeoNameStatus getStatus() {
        return status;
    }

    public void setStatus(GeoNameStatus status) {
        this.status = status;
    }
}
