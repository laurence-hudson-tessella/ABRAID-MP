package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for "Point Geometry" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#point
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonPointGeometry extends GeoJsonGeometry {

    public GeoJsonPointGeometry(double longitude, double latitude, GeoJsonCrs crs, List<Double> bbox) {
        super(GeoJsonGeometryType.POINT, extractCoordinates(longitude, latitude), crs, bbox);
    }

    private static List<Double> extractCoordinates(double longitude, double latitude) {
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(longitude);
        coordinates.add(latitude);
        return coordinates;
    }
}