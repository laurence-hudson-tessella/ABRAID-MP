package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * Adds validation parameters to a disease occurrence. Marks it for manual validation (via the Data Validator GUI)
 * if appropriate.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseOccurrenceValidationService {
    /**
     * Adds validation parameters to a disease occurrence, if the occurrence is eligible for validation.
     * If automatic model runs are enabled, all validation parameters are set. If they are disabled, then only
     * isValidated is set to true which marks it as ready for an initial model run (when requested).
     * @param occurrence The disease occurrence.
     * @return True if the disease occurrence is eligible for validation, otherwise false.
     */
    boolean addValidationParametersWithChecks(DiseaseOccurrence occurrence);

    /**
     * Adds validation parameters to a disease occurrence (without checks).
     * @param occurrence The disease occurrence.
     */
    void addValidationParameters(DiseaseOccurrence occurrence);
}
