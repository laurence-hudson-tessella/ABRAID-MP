package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * Represents a mapping between a GeoNames feature code and a location precision.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Immutable
public class GeoNamesLocationPrecision {
    @Id
    private String geoNamesFeatureCode;

    // The precision of this location.
    @Column
    @Enumerated(EnumType.STRING)
    private LocationPrecision locationPrecision;

    public String getGeoNamesFeatureCode() {
        return geoNamesFeatureCode;
    }

    public LocationPrecision getLocationPrecision() {
        return locationPrecision;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoNamesLocationPrecision that = (GeoNamesLocationPrecision) o;

        if (geoNamesFeatureCode != null ? !geoNamesFeatureCode.equals(that.geoNamesFeatureCode) : that.geoNamesFeatureCode != null)
            return false;
        if (locationPrecision != that.locationPrecision) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = geoNamesFeatureCode != null ? geoNamesFeatureCode.hashCode() : 0;
        result = 31 * result + (locationPrecision != null ? locationPrecision.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}