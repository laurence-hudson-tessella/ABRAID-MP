package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;

/**
 * Adds validation parameters to a disease occurrence. Marks it for manual validation (via the Data Validator GUI)
 * if appropriate.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DiseaseOccurrenceValidationServiceImpl implements DiseaseOccurrenceValidationService {
    private NativeSQL nativeSQL;

    public DiseaseOccurrenceValidationServiceImpl(NativeSQL nativeSQL) {
        this.nativeSQL = nativeSQL;
    }

    /**
     * Adds validation parameters to a disease occurrence - only during the automated process.
     * If automatic model runs is not enabled, and the occurrence's location has passed QC then only set is_validated to
     * true, to ensure the occurrence is used in the manually requested model run.
     * @param occurrence The disease occurrence.
     * @return True if the disease occurrence is eligible for validation, otherwise false.
     */
    public boolean addValidationParameters(DiseaseOccurrence occurrence) {
        if (isEligibleForValidation(occurrence)) {
            if (automaticModelRunsEnabled(occurrence)) {
                occurrence.setEnvironmentalSuitability(findEnvironmentalSuitability(occurrence));
                occurrence.setDistanceFromDiseaseExtent(findDistanceFromDiseaseExtent(occurrence));
                occurrence.setMachineWeighting(findMachineWeighting(occurrence));
                occurrence.setValidated(findIsValidated(occurrence));
            } else {
                occurrence.setValidated(true);
            }
            return true;
        }
        return false;
    }

    private boolean isEligibleForValidation(DiseaseOccurrence occurrence) {
        return (occurrence != null) && (occurrence.getLocation() != null) && occurrence.getLocation().hasPassedQc();
    }

    private boolean automaticModelRunsEnabled(DiseaseOccurrence occurrence) {
        return occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled();
    }

    private Double findEnvironmentalSuitability(DiseaseOccurrence occurrence) {
        return nativeSQL.findEnvironmentalSuitability(occurrence.getDiseaseGroup().getId(),
                occurrence.getLocation().getGeom());
    }

    private Double findDistanceFromDiseaseExtent(DiseaseOccurrence occurrence) {
        DistanceFromDiseaseExtentHelper helper = new DistanceFromDiseaseExtentHelper(nativeSQL);
        return helper.findDistanceFromDiseaseExtent(occurrence);
    }

    private Double findMachineWeighting(DiseaseOccurrence occurrence) {
        // For now, hardcode all machine weightings to 0.7
        return 0.7; ///CHECKSTYLE:SUPPRESS MagicNumberCheck
    }

    private boolean findIsValidated(DiseaseOccurrence occurrence) {
        // For now hardcode to true, but the proper behaviour will be implemented in a future story.
        return true;
    }
}
