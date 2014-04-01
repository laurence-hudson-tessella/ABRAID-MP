package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

import java.util.List;

/**
 * A DTO for a list of uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence expressed as a "FeatureCollection".
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonDiseaseOccurrenceFeatureCollection extends GeoJsonFeatureCollection {
    public GeoJsonDiseaseOccurrenceFeatureCollection(List<DiseaseOccurrence> occurrences) {
        super(extractOccurrenceFeatures(occurrences), GeoJsonNamedCrs.createEPSG4326(), null);
    }

    private static List<GeoJsonFeature> extractOccurrenceFeatures(List<DiseaseOccurrence> occurrences) {
        return Lambda.convert(occurrences, new Converter<DiseaseOccurrence, GeoJsonFeature>() {
            public GeoJsonFeature convert(DiseaseOccurrence occurrence) {
                return new GeoJsonDiseaseOccurrenceFeature(occurrence);
            }
        });
    }
}