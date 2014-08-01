package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunRequesterException;

import java.util.Map;

/**
 * Service interface to support the workflow surrounding a model run request.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunWorkflowService {
    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when manually triggering a model run.
     * @param diseaseGroupId The disease group ID.
     * @param batchEndDate If validator parameter batching should happen after the model run is completed,
     * this is the end date for batching.
     * @throws ModelRunRequesterException if the model run could not be requested.
     */
    void prepareForAndRequestManuallyTriggeredModelRun(int diseaseGroupId, DateTime batchEndDate)
            throws ModelRunRequesterException;

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when automatically triggering one or more model runs.
     * @param diseaseGroupId The disease group ID.
     * @throws ModelRunRequesterException if the model run could not be requested.
     */
    void prepareForAndRequestAutomaticModelRun(int diseaseGroupId) throws ModelRunRequesterException;

    /**
     * Gets the new weighting for each active expert.
     * @return A map from expert ID to the new weighting value.
     */
    Map<Integer, Double> calculateExpertsWeightings();

    /**
     * Saves the new weighting for each expert.
     * @param newExpertsWeightings The map from expert to the new weighting value.
     */
    void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings);

    /**
     * Generates the disease extent for the specified disease group.
     * @param diseaseGroup The disease group.
     */
    void generateDiseaseExtent(DiseaseGroup diseaseGroup);
}
