package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentGenerator;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentParameters;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings.WeightingsCalculator;

import java.util.Arrays;
import java.util.List;

/**
 * Prepares the model run by updating the disease extent and recalculating weightings.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManager {
    private static final Logger LOGGER = Logger.getLogger(ModelRunManager.class);
    private static final String STARTING_MODEL_PREP = "Starting model run preparation for disease %d";
    private static final String NOT_STARTING_MODEL_PREP = "Model run preparation will not be executed for disease %d;" +
            " a week has not elapsed since last model run preparation on %s";

    private ModelRunGatekeeper modelRunGatekeeper;
    private LastModelRunPrepDateManager lastModelRunPrepDateManager;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private WeightingsCalculator weightingsCalculator;
    private ModelRunRequester modelRunRequester;

    public ModelRunManager(ModelRunGatekeeper modelRunGatekeeper,
                           LastModelRunPrepDateManager lastModelRunPrepDateManager,
                           DiseaseExtentGenerator diseaseExtentGenerator,
                           WeightingsCalculator weightingsCalculator,
                           ModelRunRequester modelRunRequester) {
        this.modelRunGatekeeper = modelRunGatekeeper;
        this.lastModelRunPrepDateManager = lastModelRunPrepDateManager;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
    }

    @Transactional
    public List<Integer> getDiseaseGroupsWithOccurrences() {
        return Arrays.asList(87); ///CHECKSTYLE:SUPPRESS MagicNumberCheck - only Dengue hard-coded for now
    }

    /**
     * Prepares the model run by updating the disease extent, recalculating weightings and making the request.
     * @param diseaseGroupId The id of the disease group for which the model will be run.
     */
    @Transactional
    public void prepareModelRun(int diseaseGroupId) {
        DateTime lastModelRunPrepDate = lastModelRunPrepDateManager.getDate(diseaseGroupId);
        if (modelRunGatekeeper.dueToRun(lastModelRunPrepDate)) {
            DateTime modelRunPrepStartTime = DateTime.now();
            LOGGER.info(String.format(STARTING_MODEL_PREP, diseaseGroupId));
            executeModelRunPrep(lastModelRunPrepDate, diseaseGroupId);
            lastModelRunPrepDateManager.saveDate(modelRunPrepStartTime, diseaseGroupId);
        } else {
            LOGGER.info(String.format(NOT_STARTING_MODEL_PREP, diseaseGroupId, lastModelRunPrepDate));
        }
    }

    private void executeModelRunPrep(DateTime lastModelRunPrepDate, int diseaseGroupId) {
        ///CHECKSTYLE:OFF MagicNumberCheck - Values for Dengue hard-coded for now
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, new DiseaseExtentParameters(null, 5, 0.6, 5, 1));
        ///CHECKSTYLE:ON
        prepareDiseaseOccurrenceWeightings(lastModelRunPrepDate, diseaseGroupId);
        modelRunRequester.requestModelRun(diseaseGroupId);
    }

    private void prepareDiseaseOccurrenceWeightings(DateTime lastModelRunPrepDate, int diseaseGroupId) {
        // Task 1
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);
        // Task 2
        // Determine whether occurrences should come off DataValidator, and set their is_validated value to true
        // Task 3
        weightingsCalculator.updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);
    }
}
