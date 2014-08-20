package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.geotools.coverage.grid.GridCoverage2D;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;

import java.util.List;

/**
 * Adds validation parameters to a disease occurrence. Marks it for manual validation (via the Data Validator GUI)
 * if appropriate.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DiseaseOccurrenceValidationServiceImpl implements DiseaseOccurrenceValidationService {
    private EnvironmentalSuitabilityHelper esHelper;
    private DistanceFromDiseaseExtentHelper dfdeHelper;

    public DiseaseOccurrenceValidationServiceImpl(EnvironmentalSuitabilityHelper esHelper,
                                                  DistanceFromDiseaseExtentHelper dfdeHelper) {
        this.esHelper = esHelper;
        this.dfdeHelper = dfdeHelper;
    }

    /**
     * Adds validation parameters to a disease occurrence, if the occurrence is eligible for validation.
     * If automatic model runs are enabled, all validation parameters are set. If they are disabled, then only
     * isValidated is set to true which marks it as ready for an initial model run (when requested).
     * @param occurrence The disease occurrence.
     * @return True if the disease occurrence is eligible for validation, otherwise false.
     */
    @Override
    public boolean addValidationParametersWithChecks(DiseaseOccurrence occurrence) {
        if (isEligibleForValidation(occurrence)) {
            if (automaticModelRunsEnabled(occurrence)) {
                addValidationParameters(occurrence, null);
            } else {
                occurrence.setValidated(true);
            }
            return true;
        }
        return false;
    }

    /**
     * Adds validation parameters to a list of disease occurrences (without checks). Each occurrence must belong to
     * the same disease group.
     * @param occurrences The list of disease occurrences.
     */
    @Override
    public void addValidationParameters(List<DiseaseOccurrence> occurrences) {
        DiseaseGroup diseaseGroup = validateAndGetDiseaseGroup(occurrences);
        if (diseaseGroup != null) {
            // Get the latest mean prediction raster for the disease group, and then use it to add validation parameters
            // to all occurrences
            GridCoverage2D raster = esHelper.getLatestMeanPredictionRaster(diseaseGroup);
            for (DiseaseOccurrence occurrence : occurrences) {
                addValidationParameters(occurrence, raster);
            }
        }
    }

    private void addValidationParameters(DiseaseOccurrence occurrence, GridCoverage2D raster) {
        occurrence.setEnvironmentalSuitability(esHelper.findEnvironmentalSuitability(occurrence, raster));
        occurrence.setDistanceFromDiseaseExtent(dfdeHelper.findDistanceFromDiseaseExtent(occurrence));
        occurrence.setMachineWeighting(findMachineWeighting(occurrence));
        occurrence.setValidated(findIsValidated(occurrence));
    }

    private boolean isEligibleForValidation(DiseaseOccurrence occurrence) {
        return (occurrence != null) && (occurrence.getLocation() != null) && occurrence.getLocation().hasPassedQc();
    }

    private boolean automaticModelRunsEnabled(DiseaseOccurrence occurrence) {
        return occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled();
    }

    private Double findMachineWeighting(DiseaseOccurrence occurrence) {
        if (noModelRunsYet(occurrence)) {
            return null;
        }
        // For now, all machine weightings are null
        return null;
    }

    private boolean findIsValidated(DiseaseOccurrence occurrence) {
        if (noModelRunsYet(occurrence)) {
            return true;
        }
        // For now hardcode to true, but the proper behaviour will be implemented in a future story.
        return true;
    }

    private boolean noModelRunsYet(DiseaseOccurrence occurrence) {
        return (occurrence.getEnvironmentalSuitability() == null) &&
               (occurrence.getDistanceFromDiseaseExtent() == null);
    }

    private DiseaseGroup validateAndGetDiseaseGroup(List<DiseaseOccurrence> occurrences) {
        DiseaseGroup diseaseGroup = null;
        if (occurrences != null && occurrences.size() > 0) {
            // Get the disease group of the first occurrence in the list
            diseaseGroup = occurrences.get(0).getDiseaseGroup();
            // Ensure that all other occurrences have the same disease group
            for (int i = 1; i < occurrences.size(); i++) {
                DiseaseGroup otherDiseaseGroup = occurrences.get(i).getDiseaseGroup();
                if (otherDiseaseGroup == null || !diseaseGroup.getId().equals(otherDiseaseGroup.getId())) {
                    throw new RuntimeException("All occurrences must have the same disease group");
                }
            }
        }
        return diseaseGroup;
    }
}
