package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static java.util.Map.Entry;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Return the set of occurrences to be used in the model run, satisfying Minimum Data Spread conditions.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequesterHelper {

    private DiseaseService diseaseService;
    private LocationService locationService;

    // Minimum Data Spread parameters for the disease group
    private List<DiseaseOccurrence> allOccurrences;
    private int minDataVolume;
    private Integer minDistinctCountries;
    private Integer highFrequencyThreshold;
    private Integer minHighFrequencyCountries;
    private Boolean occursInAfrica;

    // Reference structures used to compare values against in MDS checks
    private List<Integer> countriesOfInterest;
    private Set<Integer> countriesWithAtLeastOneOccurrence;     // For disease groups using all countries
    private Map<Integer, Integer> occurrenceCountPerCountry;    // For disease groups using only the African countries

    public ModelRunRequesterHelper(DiseaseService diseaseService, LocationService locationService, int diseaseGroupId) {
        this.diseaseService = diseaseService;
        this.locationService = locationService;
        initialise(diseaseGroupId);
    }

    // Set the MDS calculation parameters for the specified disease group.
    private void initialise(int diseaseGroupId) {
        allOccurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        minDataVolume = diseaseGroup.getMinDataVolume();
        minDistinctCountries = diseaseGroup.getMinDistinctCountries();
        highFrequencyThreshold = diseaseGroup.getHighFrequencyThreshold();
        minHighFrequencyCountries = diseaseGroup.getMinHighFrequencyCountries();
        occursInAfrica = diseaseGroup.occursInAfrica();
    }

    /**
     * Gets the list of occurrences to be used in the model run.
     * @return The list of occurrences with which to run the model,
     * or null if the MDS thresholds are not met and the model should not run.
     */
    public List<DiseaseOccurrence> selectModelRunDiseaseOccurrences() {
        List<DiseaseOccurrence> occurrences = null;
        if (minDataVolumeSatisfied()) {
            occurrences = selectSubset();
            if (occursInAfrica != null) {
                occurrences = occursInAfrica ? refineSubsetForAfricanDiseaseGroup(occurrences) :
                                               refineSubsetForOtherDiseaseGroup(occurrences);
            }
        }
        return occurrences;
    }

    private boolean minDataVolumeSatisfied() {
        return (allOccurrences.size() >= minDataVolume);
    }

    // Select subset of n most recent occurrences (allOccurrences list is sorted by occurrence date)
    private List<DiseaseOccurrence> selectSubset() {
        return allOccurrences.subList(0, minDataVolume);
    }

    // If MDS is not met, continue to select points until it does, unless we run out of points.
    private List<DiseaseOccurrence> refineSubsetForAfricanDiseaseGroup(List<DiseaseOccurrence> occurrences) {
        if (parametersNotNull(minDistinctCountries, highFrequencyThreshold, minHighFrequencyCountries)) {
            countriesOfInterest = locationService.getCountriesForMinDataSpreadCalculation();
            constructOccurrenceCountPerCountryMap(occurrences);
            while (!minDataSpreadCheckForAfricanDiseaseGroup()) {
                if (occurrences.equals(allOccurrences)) {
                    return null;
                }

                int n = occurrences.size();
                occurrences = allOccurrences.subList(0, n + 1);
                addCountryToOccurrenceCountMap(occurrences.get(n).getLocation().getCountryGaulCode());
            }
        }
        return occurrences;
    }

    private static boolean parametersNotNull(Integer... args) {
        List<Integer> values = Arrays.asList(args);
        List<Integer> notNullValues = filter(notNullValue(), values);
        return (values.size() == notNullValues.size());
    }

    private void constructOccurrenceCountPerCountryMap(List<DiseaseOccurrence> occurrences) {
        occurrenceCountPerCountry = new HashMap<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            Integer countryGaulCode = occurrence.getLocation().getCountryGaulCode();
            addCountryToOccurrenceCountMap(countryGaulCode);
        }
    }

    // Only "Countries of Interest" are added to the map - with their corresponding occurrence count
    private void addCountryToOccurrenceCountMap(int gaulCode) {
        if (countriesOfInterest.contains(gaulCode)) {
            int value = (occurrenceCountPerCountry.containsKey(gaulCode)) ? occurrenceCountPerCountry.get(gaulCode) : 0;
            occurrenceCountPerCountry.put(gaulCode, value + 1);
        }
    }

    private boolean minDataSpreadCheckForAfricanDiseaseGroup() {
        Set<Integer> distinctCountries = occurrenceCountPerCountry.keySet();
        boolean distinctCountriesCheck = distinctCountries.size() >= minDistinctCountries;
        Set<Integer> highFrequencyOccurrenceCountries = extractHighFrequencyCountries();
        boolean highFrequencyCountriesCheck = highFrequencyOccurrenceCountries.size() >= minHighFrequencyCountries;
        return (distinctCountriesCheck & highFrequencyCountriesCheck);
    }

    private Set<Integer> extractHighFrequencyCountries() {
        Set<Integer> set = new HashSet<>();
        for (Entry<Integer, Integer> entry : occurrenceCountPerCountry.entrySet()) {
            if (entry.getValue() >= highFrequencyThreshold) {
                set.add(entry.getKey());
            }
        }
        return set;
    }

    private List<DiseaseOccurrence> refineSubsetForOtherDiseaseGroup(List<DiseaseOccurrence> occurrences) {
        if (minDistinctCountries != null) {
            extractDistinctGaulCodes(occurrences);
            while (!minDataSpreadCheckForOtherDiseaseGroup()) {
                if (occurrences.equals(allOccurrences)) {
                    return null;
                }

                int n = occurrences.size();
                occurrences = allOccurrences.subList(0, n + 1);
                countriesWithAtLeastOneOccurrence.add(occurrences.get(n).getLocation().getCountryGaulCode());
            }
        }
        return occurrences;
    }

    private void extractDistinctGaulCodes(List<DiseaseOccurrence> occurrences) {
        Set<Location> locations = new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
        List<Integer> gaulCodes = convert(locations, new Converter<Location, Integer>() {
            public Integer convert(Location location) { return location.getCountryGaulCode(); }
        });
        countriesWithAtLeastOneOccurrence = new HashSet<>(gaulCodes);
    }

    private boolean minDataSpreadCheckForOtherDiseaseGroup() {
        return countriesWithAtLeastOneOccurrence.size() >= minDistinctCountries;
    }

}
