package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the ModelRunDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private CovariateFileDao covariateFileDao;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Autowired
    private AdminUnitTropicalDao adminUnitTropicalDao;

    @Autowired
    private DiseaseExtentClassDao diseaseExtentClassDao;

    private ModelRun modelRunDengue1;
    private ModelRun modelRunDengue2;
    private ModelRun modelRunDengue3;
    private ModelRun modelRunDengue4;
    private ModelRun modelRunDengue5;
    private ModelRun modelRunDengue6;
    private ModelRun modelRunMalarias1;

    @Before
    public void setUp() {
        modelRunDengue1 = createModelRun("dengue 1", 87, ModelRunStatus.IN_PROGRESS, "host1", "2014-07-01", null);
        modelRunDengue2 = createModelRun("dengue 2", 87, ModelRunStatus.COMPLETED, "host1", "2014-07-01", "2014-07-04");
        modelRunDengue3 = createModelRun("dengue 3", 87, ModelRunStatus.COMPLETED, "host3", "2014-07-03", "2014-07-03");
        modelRunDengue4 = createModelRun("dengue 4", 87, ModelRunStatus.IN_PROGRESS, "host2", "2014-07-05", null);
        modelRunDengue5 = createModelRun("dengue 5", 87, ModelRunStatus.FAILED, "host3", "2014-07-06", "2014-07-05");
        modelRunMalarias1 = createModelRun("malarias 1", 202, ModelRunStatus.COMPLETED, "host3", "2014-07-07", "2014-07-08");
    }

    @Test
    public void saveAndReloadModelRunByName() {
        // Arrange
        String name = "test name";
        int diseaseGroupId = 87;
        String requestServer = "requestServer";
        DateTime requestDate = DateTime.now().minusHours(4);
        DateTime responseDate = DateTime.now();
        String outputText = "output text";
        String errorText = "error text";
        DateTime batchStartDate = DateTime.now().minusHours(6).minusDays(1);
        DateTime batchEndDate = DateTime.now().minusHours(6);
        DateTime batchingCompletionDate = DateTime.now().plusHours(2);
        DateTime occurrenceDataRangeStartDate = DateTime.now().minusHours(9);
        DateTime occurrenceDataRangeEndDate = DateTime.now().minusHours(8);
        int batchedOccurrenceCount = 1000;
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);

        ModelRun modelRun = new ModelRun(name, diseaseGroup, requestServer, requestDate,
                occurrenceDataRangeStartDate, occurrenceDataRangeEndDate);
        modelRun.setResponseDate(responseDate);
        modelRun.setOutputText(outputText);
        modelRun.setErrorText(errorText);
        modelRun.setBatchStartDate(batchStartDate);
        modelRun.setBatchEndDate(batchEndDate);
        modelRun.setBatchingCompletedDate(batchingCompletionDate);
        modelRun.setBatchOccurrenceCount(batchedOccurrenceCount);
        modelRunDao.save(modelRun);

        // Act
        flushAndClear();

        // Assert
        modelRun = modelRunDao.getByName(name);
        assertThat(modelRun).isNotNull();
        assertThat(modelRun.getName()).isEqualTo(name);
        assertThat(modelRun.getStatus()).isEqualTo(ModelRunStatus.IN_PROGRESS);
        assertThat(modelRun.getDiseaseGroupId()).isEqualTo(diseaseGroupId);
        assertThat(modelRun.getDiseaseGroup()).isEqualTo(diseaseGroup);
        assertThat(modelRun.getRequestServer()).isEqualTo(requestServer);
        assertThat(modelRun.getRequestDate()).isEqualTo(requestDate);
        assertThat(modelRun.getOutputText()).isEqualTo(outputText);
        assertThat(modelRun.getErrorText()).isEqualTo(errorText);
        assertThat(modelRun.getBatchStartDate()).isEqualTo(batchStartDate);
        assertThat(modelRun.getBatchEndDate()).isEqualTo(batchEndDate);
        assertThat(modelRun.getBatchingCompletedDate()).isEqualTo(batchingCompletionDate);
        assertThat(modelRun.getBatchOccurrenceCount()).isEqualTo(batchedOccurrenceCount);
        assertThat(modelRun.getOccurrenceDataRangeStartDate()).isEqualTo(occurrenceDataRangeStartDate);
        assertThat(modelRun.getOccurrenceDataRangeEndDate()).isEqualTo(occurrenceDataRangeEndDate);
    }

    @Test
    public void saveAndLoadCascadesToSubmodelStatistics() {
        // Arrange
        ModelRun run = createModelRun("name");
        modelRunDao.save(run);
        flushAndClear();
        run = modelRunDao.getByName("name");

        List<SubmodelStatistic> submodelStatistics = new ArrayList<>();
        submodelStatistics.add(createSubmodelStatistic(run));
        submodelStatistics.add(createSubmodelStatistic(run));
        submodelStatistics.add(createSubmodelStatistic(run));
        run.setSubmodelStatistics(submodelStatistics);
        modelRunDao.save(run);
        flushAndClear();

        // Act
        run = modelRunDao.getByName("name");

        // Assert
        assertThat(run.getSubmodelStatistics()).hasSize(3);
    }

    @Test
    public void saveAndLoadCascadesToInputDiseaseOccurrences() {
        // Arrange
        ModelRun run = createModelRun("name");
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getAll().subList(0, 6);
        run.setInputDiseaseOccurrences(occurrences);
        modelRunDao.save(run);
        flushAndClear();

        // Act
        run = modelRunDao.getByName("name");

        // Assert
        assertThat(run.getInputDiseaseOccurrences()).hasSize(occurrences.size());
        DiseaseOccurrence[] occurrences2 = new DiseaseOccurrence[occurrences.size()];
        occurrences2 = occurrences.toArray(occurrences2);
        assertThat(run.getInputDiseaseOccurrences()).containsOnly(occurrences2);
    }

    @Test
    public void saveAndLoadCascadesToInputDiseaseExtent() {
        // Arrange
        ModelRun run = createModelRun("name");
        List<ModelRunAdminUnitDiseaseExtentClass> extent = Arrays.asList(
                createAdminUnitDiseaseExtentClass(run, DiseaseExtentClass.ABSENCE, 194, null),
                createAdminUnitDiseaseExtentClass(run, DiseaseExtentClass.POSSIBLE_ABSENCE, 30, null),
                createAdminUnitDiseaseExtentClass(run, DiseaseExtentClass.PRESENCE, null, 906)
        );
        run.setInputDiseaseExtent(extent);
        modelRunDao.save(run);
        flushAndClear();

        // Act
        run = modelRunDao.getByName("name");

        // Assert
        assertThat(run.getInputDiseaseExtent()).hasSize(extent.size());
        ModelRunAdminUnitDiseaseExtentClass[] extent2 = new ModelRunAdminUnitDiseaseExtentClass[extent.size()];
        extent2 = extent.toArray(extent2);
        assertThat(run.getInputDiseaseExtent()).containsOnly(extent2);
    }

    @Test
    public void saveAndLoadCascadesToCovariateInfluence() {
        // Arrange
        ModelRun run = createModelRun("name");
        modelRunDao.save(run);
        flushAndClear();
        run = modelRunDao.getByName("name");

        List<CovariateInfluence> covariateInfluences = new ArrayList<>();
        covariateInfluences.add(createCovariateInfluence(createCovariateFile(1), run));
        covariateInfluences.add(createCovariateInfluence(createCovariateFile(2), run));
        covariateInfluences.add(createCovariateInfluence(createCovariateFile(3), run));
        run.setCovariateInfluences(covariateInfluences);
        modelRunDao.save(run);
        flushAndClear();

        // Act
        run = modelRunDao.getByName("name");

        // Assert
        assertThat(run.getCovariateInfluences()).hasSize(3);
    }

    @Test
    public void saveAndLoadCascadesToEffectCurveCovariateInfluence() {
        // Arrange
        ModelRun run = createModelRun("name");
        modelRunDao.save(run);
        flushAndClear();
        run = modelRunDao.getByName("name");

        List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences = new ArrayList<>();
        effectCurveCovariateInfluences.add(createEffectCurveCovariateInfluence(createCovariateFile(1), run));
        effectCurveCovariateInfluences.add(createEffectCurveCovariateInfluence(createCovariateFile(2), run));
        effectCurveCovariateInfluences.add(createEffectCurveCovariateInfluence(createCovariateFile(3), run));
        run.setEffectCurveCovariateInfluences(effectCurveCovariateInfluences);
        modelRunDao.save(run);
        flushAndClear();

        // Act
        run = modelRunDao.getByName("name");

        // Assert
        assertThat(run.getEffectCurveCovariateInfluences()).hasSize(3);
    }

    @Test
    public void getLastRequestedModelRunReturnsNullIfNoModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        ModelRun modelRun = modelRunDao.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getLastRequestedModelRunIgnoresIDsAndDoesNotRequireResponseDateToBeSet() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue1);
        modelRunDao.save(modelRunMalarias1);

        // Act
        ModelRun modelRun = modelRunDao.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isEqualTo(modelRunDengue4);
    }

    @Test
    public void getLastRequestedModelRunDoesNotRequireResponseDateToBeNull() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunMalarias1);
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue1);

        // Act
        ModelRun modelRun = modelRunDao.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isEqualTo(modelRunDengue5);
    }

    @Test
    public void getMostRecentlyRequestedModelRunWhichCompletedReturnsNullIfNoModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        ModelRun modelRun = modelRunDao.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getMostRecentlyRequestedModelRunWhichCompletedReturnsNullIfNoCompletedModelRuns() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunMalarias1);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunDengue1);

        // Act
        ModelRun modelRun = modelRunDao.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getMostRecentlyRequestedModelRunWhichCompletedReturnsCorrectValueIfCompletedModelRuns() {
        // Arrange
        int diseaseGroupId = 87;
        saveStandardRuns();

        // Act
        ModelRun modelRun = modelRunDao.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(modelRun).isEqualTo(modelRunDengue3);
    }

    @Test
    public void getMostRecentlyFinishedModelRunWhichCompletedReturnsNullIfNoModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        ModelRun modelRun = modelRunDao.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getMostRecentlyFinishedModelRunWhichCompletedReturnsNullIfNoCompletedModelRuns() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunMalarias1);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunDengue1);

        // Act
        ModelRun modelRun = modelRunDao.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getMostRecentlyFinishedModelRunWhichCompletedReturnsNullIfCompletedModelRunHasNoResponseDate() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDengue2.setResponseDate(null);
        modelRunDao.save(modelRunDengue2);

        // Act
        ModelRun modelRun = modelRunDao.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getMostRecentlyFinishedModelRunWhichCompletedReturnsCorrectValueIfCompletedModelRuns() {
        // Arrange
        int diseaseGroupId = 87;
        saveStandardRuns();

        // Act
        ModelRun modelRun = modelRunDao.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(modelRun).isEqualTo(modelRunDengue2);
    }

    @Test
    public void getCompletedModelRunsForDisplayReturnsCorrectCompletedModelRuns() {
        // Arrange
        DiseaseGroup dengue = diseaseGroupDao.getById(87);
        dengue.setAutomaticModelRunsStartDate(new DateTime(2014, 7, 2, 0, 0)); // Chosen to be between 2 model runs
        diseaseGroupDao.save(dengue);

        modelRunDao.save(modelRunDengue1);      // Not for display: IN_PROGRESS
        modelRunDao.save(modelRunDengue2);      // Not for display: Requested before automaticModelRunsStartDate
        modelRunDao.save(modelRunDengue3);      // For display: Requested after automaticModelRunsStartDate
        modelRunDao.save(modelRunDengue4);      // Not for display: IN_PROGRESS
        modelRunDao.save(modelRunDengue5);      // Not for display: FAILED
        modelRunDao.save(modelRunMalarias1);    // For display: COMPLETED and automaticModelRunsStartDate is null

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getCompletedModelRunsForDisplay();

        // Assert
        assertThat(modelRuns).containsOnly(modelRunDengue3, modelRunMalarias1);
    }

    @Test
    public void getCompletedModelRunsForDisplayReturnsEmptyIfNoCompletedModelRuns() {
        // Arrange
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunDengue1);

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getCompletedModelRunsForDisplay();

        // Assert
        assertThat(modelRuns).isEmpty();
    }

    @Test
    public void hasBatchingEverCompletedReturnsFalseIfNoBatchCompletedDatesExistForTheDiseaseGroup() {
        // Arrange
        modelRunMalarias1.setBatchingCompletedDate(DateTime.now());
        modelRunDao.save(modelRunMalarias1);

        // Act
        boolean result = modelRunDao.hasBatchingEverCompleted(87);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void hasBatchingEverCompletedReturnsTrueIfAtLeastOneBatchCompletedDateExists() {
        // Arrange
        modelRunDengue3.setBatchingCompletedDate(DateTime.now());
        modelRunDao.save(modelRunDengue3);

        // Act
        boolean result = modelRunDao.hasBatchingEverCompleted(87);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void getModelRunRequestServersByUsageGetsCorrectlyOrderedServers() {
        // Arrange
        modelRunDao.save(modelRunDengue1);
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunMalarias1);

        // Act
        List<String> result = modelRunDao.getModelRunRequestServersByUsage();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo("host3");
        assertThat(result.get(1)).isEqualTo("host2");
        assertThat(result.get(2)).isEqualTo("host1");
    }

    @Test
    public void getModelRunsForDiseaseGroupGetsCorrectSetOfModelRuns() {
        // Arrange
        modelRunDao.save(modelRunDengue1);
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunMalarias1);

        // Act
        Collection<ModelRun> result = modelRunDao.getModelRunsForDiseaseGroup(87);

        // Assert
        assertThat(result).hasSize(5);
        assertThat(result).contains(modelRunDengue1);
        assertThat(result).contains(modelRunDengue2);
        assertThat(result).contains(modelRunDengue3);
        assertThat(result).contains(modelRunDengue4);
        assertThat(result).contains(modelRunDengue5);
    }

    private void saveStandardRuns() {
        modelRunDao.save(modelRunMalarias1);
        modelRunDao.save(modelRunDengue1);
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
    }

    @Test
    public void getFilteredModelRunsReturnsCorrectSetWithoutFilters() {
        // Arrange
        setModelRunStartDates("2014-07-02", "2014-07-02");
        saveStandardRuns();

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getFilteredModelRuns(null, null, null, null);
        // Assert
        assertThat(modelRuns).containsOnly(modelRunDengue3, modelRunMalarias1); // Only completed automatic runs
    }

    @Test
    public void getFilteredModelRunsReturnsCorrectSetWithNameFilter() {
        // Arrange
        setModelRunStartDates("2014-06-28", "2014-07-02");
        saveStandardRuns();

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getFilteredModelRuns("dengue 2", null, null, null);
        // Assert
        assertThat(modelRuns).containsOnly(modelRunDengue2);
    }

    @Test
    public void getFilteredModelRunsReturnsCorrectSetWithDiseaseFilter() {
        // Arrange
        setModelRunStartDates("2014-06-28", "2014-07-02");
        saveStandardRuns();

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getFilteredModelRuns(null, 87, null, null);
        // Assert
        assertThat(modelRuns).containsOnly(modelRunDengue2, modelRunDengue3);
    }

    @Test
    public void getFilteredModelRunsReturnsCorrectSetWithMinDateFilter() {
        // Arrange
        setModelRunStartDates("2014-06-28", "2014-07-02");
        saveStandardRuns();

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getFilteredModelRuns(null, null, new LocalDate("2014-07-04"), null);
        // Assert
        assertThat(modelRuns).containsExactly(modelRunMalarias1, modelRunDengue2);
    }

    @Test
    public void getFilteredModelRunsReturnsCorrectSetWithMaxDateFilter() {
        // Arrange
        setModelRunStartDates("2014-06-28", "2014-07-02");
        saveStandardRuns();

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getFilteredModelRuns(null, null, null, new LocalDate("2014-07-03"));
        // Assert
        assertThat(modelRuns).containsOnly(modelRunDengue3);
    }

    @Test
    public void getFilteredModelRunsReturnsCorrectSetWithNormalFilters() {
        // Arrange
        setModelRunStartDates("2014-06-28", "2014-07-02");
        saveStandardRuns();

        // Act
        Collection<ModelRun> modelRuns = modelRunDao.getFilteredModelRuns(null, 87, new LocalDate("2014-07-03"), new LocalDate("2014-07-03"));
        // Assert
        assertThat(modelRuns).containsOnly(modelRunDengue3);
    }

    private void setModelRunStartDates(String dengueDate, String malariaDate) {
        int diseaseGroupId = 87;
        DiseaseGroup dengue = diseaseGroupDao.getById(diseaseGroupId);
        DiseaseGroup malaria = diseaseGroupDao.getById(202);
        dengue.setAutomaticModelRunsStartDate(new DateTime(dengueDate));
        malaria.setAutomaticModelRunsStartDate(new DateTime(malariaDate));
        diseaseGroupDao.save(dengue);
        diseaseGroupDao.save(malaria);
    }

    private ModelRunAdminUnitDiseaseExtentClass createAdminUnitDiseaseExtentClass(
            ModelRun run, String diseaseExtentClass,
            Integer adminUnitTropicalGaul, Integer adminUnitGlobalGaul) {
        ModelRunAdminUnitDiseaseExtentClass value = new ModelRunAdminUnitDiseaseExtentClass();
        value.setModelRun(run);
        value.setDiseaseExtentClass(diseaseExtentClassDao.getByName(diseaseExtentClass));
        value.setAdminUnitGlobal(adminUnitGlobalGaul == null ? null : adminUnitGlobalDao.getByGaulCode(adminUnitGlobalGaul));
        value.setAdminUnitTropical(adminUnitTropicalGaul == null ? null : adminUnitTropicalDao.getByGaulCode(adminUnitTropicalGaul));
        return value;
    }

    private SubmodelStatistic createSubmodelStatistic(ModelRun modelRun) {
        SubmodelStatistic submodelStatistic = new SubmodelStatistic();
        submodelStatistic.setModelRun(modelRun);
        return submodelStatistic;
    }

    private CovariateInfluence createCovariateInfluence(CovariateFile covariateFile, ModelRun modelRun) {
        CovariateInfluence covariateInfluence = new CovariateInfluence();
        covariateInfluence.setModelRun(modelRun);
        covariateInfluence.setCovariateFile(covariateFile);
        covariateInfluence.setMeanInfluence(1.0);
        covariateInfluence.setLowerQuantile(2.0);
        covariateInfluence.setUpperQuantile(3.0);
        return covariateInfluence;
    }

    private EffectCurveCovariateInfluence createEffectCurveCovariateInfluence(CovariateFile covariateFile, ModelRun modelRun) {
        EffectCurveCovariateInfluence effectCurveCovariateInfluence = new EffectCurveCovariateInfluence();
        effectCurveCovariateInfluence.setModelRun(modelRun);
        effectCurveCovariateInfluence.setCovariateFile(covariateFile);
        effectCurveCovariateInfluence.setMeanInfluence(1.0);
        effectCurveCovariateInfluence.setLowerQuantile(2.0);
        effectCurveCovariateInfluence.setUpperQuantile(3.0);
        effectCurveCovariateInfluence.setCovariateValue(4.0);
        return effectCurveCovariateInfluence;
    }

    private ModelRun createModelRun(String name) {
        return new ModelRun(name, diseaseGroupDao.getById(87), "host", DateTime.now(), DateTime.now(), DateTime.now());
    }

    private ModelRun createModelRun(String name, int diseaseGroupId, ModelRunStatus status, String requestServer,
                                           String requestDate, String responseDate) {
        ModelRun modelRun = new ModelRun(name, diseaseGroupDao.getById(diseaseGroupId), requestServer, new LocalDate(requestDate).toDateTimeAtStartOfDay(), DateTime.now(), DateTime.now());
        modelRun.setStatus(status);
        modelRun.setResponseDate(new LocalDate(responseDate).toDateTimeAtStartOfDay());
        return modelRun;
    }

    private CovariateFile createCovariateFile(int idx) {
        CovariateFile covariate = new CovariateFile("name" + idx, false, false, "info" + idx);
        CovariateSubFile covariateSubFile = new CovariateSubFile(covariate, null, "file" + idx);
        covariate.setFiles(Arrays.asList(covariateSubFile));
        covariateFileDao.save(covariate);
        return covariate;
    }
}
