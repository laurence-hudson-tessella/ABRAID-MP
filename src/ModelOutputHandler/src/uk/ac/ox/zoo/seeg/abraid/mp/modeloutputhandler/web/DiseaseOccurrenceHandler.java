package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.List;

/**
 * Handles disease occurrences. Specifically, if a batch end date is specified in the model run, it sets the
 * "validation parameters" (e.g. environmental suitability, distance from disease extent) for the disease occurrences up
 * until the end date.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandler {
    private static final Logger LOGGER = Logger.getLogger(DiseaseOccurrenceHandler.class);
    private static final String STARTING_HANDLING_LOG_MESSAGE = "Model run %d: starting disease occurrence handling";
    private static final String INITIAL_BATCH_LOG_MESSAGE = "Model run %d: this is the initial batch, so setting " +
            "final weighting to null for %d occurrence(s) of disease group %d (%s)";
    private static final String VALIDATION_LOG_MESSAGE =
            "Model run %d: setting validation parameters for %d occurrence(s) of disease group %d (%s) " +
            "(batch end date %s)";
    private static final String VALIDATION_COMPLETED_LOG_MESSAGE =
            "Model run %d: setting validation parameters completed";
    private static final String LOG_DATE_FORMAT = "dd MMM yyyy";
    private static final String NO_HANDLING_LOG_MESSAGE = "Model run %d: no disease occurrence handling to do";

    private DiseaseService diseaseService;
    private ModelRunService modelRunService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    public DiseaseOccurrenceHandler(DiseaseService diseaseService, ModelRunService modelRunService,
                                    DiseaseOccurrenceValidationService diseaseOccurrenceValidationService) {
        this.diseaseService = diseaseService;
        this.modelRunService = modelRunService;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
    }

    /**
     * Handles disease occurrences for disease group setup (i.e. automatic model runs not yet enabled).
     * @param modelRun The model run.
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(ModelRun modelRun) {
        // Reload the model run, because this may be a new transaction and we may need to save the model run later on
        modelRun = modelRunService.getModelRunByName(modelRun.getName());
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(modelRun.getDiseaseGroupId());

        // If disease group is in the setup phase, set validation parameters on a batch of disease occurrences
        if (didModelRunComplete(modelRun) && !areAutomaticModelRunsEnabled(diseaseGroup)) {
            LOGGER.info(String.format(STARTING_HANDLING_LOG_MESSAGE, modelRun.getId()));
            initialiseBatchingIfNecessary(modelRun, diseaseGroup);
            handleBatch(modelRun, diseaseGroup);
        } else {
            LOGGER.info(String.format(NO_HANDLING_LOG_MESSAGE, modelRun.getId()));
        }
    }

    private void initialiseBatchingIfNecessary(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        // If no batch of disease occurrences has completed for this disease group, initialise the batching process by
        // setting the final weightings of all disease occurrences to null
        if (!modelRunService.hasBatchingEverCompleted(diseaseGroup.getId())) {
            List<DiseaseOccurrence> diseaseOccurrences =
                    diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroup.getId());
            LOGGER.info(String.format(INITIAL_BATCH_LOG_MESSAGE, modelRun.getId(), diseaseOccurrences.size(),
                    diseaseGroup.getId(), diseaseGroup.getName()));

            for (DiseaseOccurrence occurrence : diseaseOccurrences) {
                occurrence.setFinalWeighting(null);
                occurrence.setFinalWeightingExcludingSpatial(null);
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    private void handleBatch(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        DateTime batchEndDate = modelRun.getBatchEndDate();
        if (batchEndDate != null) {
            // Ensure that the batch end date is at the very end of the day
            DateTime batchEndDateWithMaximumTime = getBatchEndDateWithMaximumTime(batchEndDate);

            // Get the occurrences that we want to batch, and then set their validation parameters
            List<DiseaseOccurrence> occurrences =
                    diseaseService.getDiseaseOccurrencesForBatching(diseaseGroup.getId(), batchEndDateWithMaximumTime);
            LOGGER.info(String.format(VALIDATION_LOG_MESSAGE, modelRun.getId(), occurrences.size(),
                    diseaseGroup.getId(), diseaseGroup.getName(),
                    batchEndDateWithMaximumTime.toString(LOG_DATE_FORMAT)));
            setValidationParametersForOccurrencesBatch(occurrences);
            setModelRunBatchingParameters(modelRun, occurrences.size());
            LOGGER.info(String.format(VALIDATION_COMPLETED_LOG_MESSAGE, modelRun.getId()));
        }
    }

    private boolean didModelRunComplete(ModelRun modelRun) {
        return modelRun.getStatus() == ModelRunStatus.COMPLETED;
    }

    private boolean areAutomaticModelRunsEnabled(DiseaseGroup diseaseGroup) {
        return diseaseGroup.isAutomaticModelRunsEnabled();
    }

    private DateTime getBatchEndDateWithMaximumTime(DateTime batchEndDate) {
        return batchEndDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1);
    }

    private void setValidationParametersForOccurrencesBatch(List<DiseaseOccurrence> occurrences) {
        if (occurrences.size() > 0) {
            diseaseOccurrenceValidationService.addValidationParameters(occurrences);
            for (DiseaseOccurrence occurrence : occurrences) {
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    private void setModelRunBatchingParameters(ModelRun modelRun, int batchedOccurrenceCount) {
        modelRun.setBatchingCompletedDate(DateTime.now());
        modelRun.setBatchOccurrenceCount(batchedOccurrenceCount);
        modelRunService.saveModelRun(modelRun);
    }
}