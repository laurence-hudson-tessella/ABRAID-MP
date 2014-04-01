package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * A DTO for the properties on a uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence object.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonDiseaseOccurrenceFeatureProperties {
    private final String locationName;
    private final GeoJsonAlert alert;
    private final DateTime diseaseOccurrenceStartDate;

    public GeoJsonDiseaseOccurrenceFeatureProperties(DiseaseOccurrence occurrence) {
        this.locationName = occurrence.getLocation().getName();
        this.diseaseOccurrenceStartDate = occurrence.getOccurrenceStartDate();
        this.alert = new GeoJsonAlert(occurrence.getAlert());
    }

    public DateTime getDiseaseOccurrenceStartDate() {
        return diseaseOccurrenceStartDate;
    }

    public GeoJsonAlert getAlert() {
        return alert;
    }

    public String getLocationName() {
        return locationName;
    }
}