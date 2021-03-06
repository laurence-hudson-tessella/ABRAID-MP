package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the DiseaseGroupDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;

    @Autowired
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    @Autowired
    private AdminUnitReviewDao adminUnitReviewDao;

    @Autowired
    private AdminUnitTropicalDao adminUnitTropicalDao;

    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Autowired
    private DiseaseExtentClassDao diseaseExtentClassDao;

    @Autowired
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;

    @Autowired
    private ExpertDao expertDao;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private AlertDao alertDao;

    @Test
    public void saveAndReloadDiseaseCluster() {
        // Arrange
        String diseaseClusterName = "Test disease cluster";
        String diseaseClusterPublicName = "Test disease cluster public name";
        String diseaseClusterShortName = "Short name";
        String diseaseClusterAbbreviation = "tdc";
        String mode = "Bhatt2013";
        int validatorDiseaseGroupId = 2;
        ValidatorDiseaseGroup validatorDiseaseGroup = validatorDiseaseGroupDao.getById(validatorDiseaseGroupId);
        DateTime lastModelRunPrepDate = DateTime.now().minusHours(2);
        int minNewLocations = 100;
        double maxEnvironmentalSuitability = 0.5;
        double minDistanceFromDiseaseExtent = 1;
        double maxEnvironmentalSuitabilityWithoutML = 0.4;
        double weighting = 0.5;

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        DiseaseExtent parameters = new DiseaseExtent(diseaseGroup);

        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.CLUSTER);
        diseaseGroup.setPublicName(diseaseClusterPublicName);
        diseaseGroup.setShortName(diseaseClusterShortName);
        diseaseGroup.setAbbreviation(diseaseClusterAbbreviation);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        diseaseGroup.setMinNewLocationsTrigger(minNewLocations);
        diseaseGroup.setMaxEnvironmentalSuitabilityForTriggering(maxEnvironmentalSuitability);
        diseaseGroup.setMinDistanceFromDiseaseExtentForTriggering(minDistanceFromDiseaseExtent);
        diseaseGroup.setMaxEnvironmentalSuitabilityWithoutML(maxEnvironmentalSuitabilityWithoutML);
        diseaseGroup.setWeighting(weighting);
        diseaseGroup.setUseMachineLearning(false);
        diseaseGroup.setGlobal(true);
        diseaseGroup.setPriorityDisease(true);
        diseaseGroup.setDiseaseExtentParameters(parameters);
        diseaseGroup.setModelMode(mode);

        // Act
        diseaseGroupDao.save(diseaseGroup);

        // Assert
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
        Integer id = diseaseGroup.getId();
        flushAndClear();
        diseaseGroup = diseaseGroupDao.getById(id);
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getName()).isEqualTo(diseaseClusterName);
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.CLUSTER);
        assertThat(diseaseGroup.getPublicName()).isEqualTo(diseaseClusterPublicName);
        assertThat(diseaseGroup.getShortName()).isEqualTo(diseaseClusterShortName);
        assertThat(diseaseGroup.getAbbreviation()).isEqualTo(diseaseClusterAbbreviation);
        assertThat(diseaseGroup.getModelMode()).isEqualTo(mode);
        assertThat(diseaseGroup.getValidatorDiseaseGroup()).isNotNull();
        assertThat(diseaseGroup.getValidatorDiseaseGroup().getId()).isEqualTo(validatorDiseaseGroupId);
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isEqualTo(lastModelRunPrepDate);
        assertThat(diseaseGroup.getMinNewLocationsTrigger()).isEqualTo(minNewLocations);
        assertThat(diseaseGroup.getMaxEnvironmentalSuitabilityForTriggering()).isEqualTo(maxEnvironmentalSuitability);
        assertThat(diseaseGroup.getMinDistanceFromDiseaseExtentForTriggering()).isEqualTo(minDistanceFromDiseaseExtent);
        assertThat(diseaseGroup.getMaxEnvironmentalSuitabilityWithoutML()).isEqualTo(
                maxEnvironmentalSuitabilityWithoutML);
        assertThat(diseaseGroup.useMachineLearning()).isFalse();
        assertThat(diseaseGroup.getWeighting()).isEqualTo(weighting);
        assertThat(diseaseGroup.isGlobal()).isTrue();
        assertThat(diseaseGroup.isPriorityDisease()).isTrue();
        assertThat(diseaseGroup.getParentGroup()).isNull();
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isEqualToIgnoringGivenFields(parameters, "lastValidatorExtentUpdateInputOccurrences");
        assertThat(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).isEmpty(); // Null should become empty collection
    }

    @Test
    public void saveAndReloadDiseaseMicroCluster() {
        // Arrange
        String diseaseClusterName = "Test disease microcluster";
        DiseaseGroup diseaseCluster = diseaseGroupDao.getById(1);

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        DiseaseExtent parameters = new DiseaseExtent(diseaseGroup);

        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.MICROCLUSTER);
        diseaseGroup.setParentGroup(diseaseCluster);
        diseaseGroup.setDiseaseExtentParameters(parameters);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        Integer id = diseaseGroup.getId();
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(id);
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getName()).isEqualTo(diseaseClusterName);
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.MICROCLUSTER);
        assertThat(diseaseGroup.getParentGroup()).isNotNull();
        assertThat(diseaseGroup.getParentGroup()).isEqualTo(diseaseCluster);
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
        assertThat(diseaseGroup.useMachineLearning()).isTrue();
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isEqualToIgnoringGivenFields(parameters, "lastValidatorExtentUpdateInputOccurrences");
        assertThat(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).isEmpty(); // Null should become empty collection
    }

    @Test
    public void saveAndReloadDisease() {
        // Arrange
        String diseaseName = "Test single disease";
        String diseaseMicroClusterName = "Test microcluster";
        DiseaseGroup diseaseCluster = diseaseGroupDao.getById(5);
        DiseaseGroup diseaseMicroCluster = new DiseaseGroup(diseaseCluster, diseaseMicroClusterName,
                DiseaseGroupType.MICROCLUSTER);
        DiseaseGroup disease = new DiseaseGroup(diseaseMicroCluster, diseaseName, DiseaseGroupType.SINGLE);

        DiseaseExtent parameters = new DiseaseExtent(disease);
        disease.setDiseaseExtentParameters(parameters);

        // Act
        diseaseGroupDao.save(diseaseMicroCluster);
        diseaseGroupDao.save(disease);
        Integer id = disease.getId();
        flushAndClear();

        // Assert
        disease = diseaseGroupDao.getById(id);
        assertThat(disease).isNotNull();
        assertThat(disease.getName()).isEqualTo(diseaseName);
        assertThat(disease.getGroupType()).isEqualTo(DiseaseGroupType.SINGLE);
        assertThat(disease.getParentGroup()).isNotNull();
        assertThat(disease.getParentGroup()).isEqualTo(diseaseMicroCluster);
        assertThat(disease.getParentGroup().getParentGroup()).isNotNull();
        assertThat(disease.getParentGroup().getParentGroup()).isEqualTo(diseaseCluster);
        assertThat(disease.getCreatedDate()).isNotNull();
        assertThat(disease.getDiseaseExtentParameters()).isEqualToIgnoringGivenFields(parameters, "lastValidatorExtentUpdateInputOccurrences");
        assertThat(disease.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).isEmpty(); // Null should become empty collection
    }

    @Test
    public void loadNonExistentDiseaseGroup() {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(-1);
        assertThat(diseaseGroup).isNull();
    }

    @Test
    public void getAllDiseaseGroups() {
        List<DiseaseGroup> diseaseGroups = diseaseGroupDao.getAll();
        assertThat(diseaseGroups).hasSize(391);
    }

    @Test
    public void getIdsForAutomaticModelRunsIsEmpty() {
        List<Integer> ids = diseaseGroupDao.getIdsForAutomaticModelRuns();
        assertThat(ids).hasSize(0);
    }

    @Test
    public void getIdsForAutomaticModelRuns() {
        int id = 87;
        setAutomaticModelRunsStartDate(id);

        List<Integer> ids = diseaseGroupDao.getIdsForAutomaticModelRuns();
        assertThat(ids).hasSize(1);
        assertThat(ids.get(0)).isEqualTo(id);
    }

    @Test
    public void getDiseaseGroupsNeedingOccurrenceReviewByExpert() {
        // Arrange
        setAuto(31);
        setAuto(112);
        setAuto(14);
        flushAndClear();
        int id1 = createOccurrenceNeedingReview(31); // reviewed
        int id2 = createOccurrenceNeedingReview(112); // reviewed
        int id3 = createOccurrenceNeedingReview(112); // only reviewed by other experts
        int id4 = createOccurrenceNeedingReview(22);  // not reviewed, but not auto
        int id5 = createOccurrenceNeedingReview(14); // not review
        flushAndClear();
        createOccurrenceReview(1, id1);
        createOccurrenceReview(1, id2);
        createOccurrenceReview(2, id3);
        flushAndClear();

        // Act
        List<DiseaseGroup> result = diseaseGroupDao.getDiseaseGroupsNeedingOccurrenceReviewByExpert(1);

        // Assert
        assertThat(extract(result, on(DiseaseGroup.class).getId())).containsOnly(112, 14);
    }

    private void createOccurrenceReview(int expertId, int occurrenceId) {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(
                expertDao.getById(expertId),
                diseaseOccurrenceDao.getById(occurrenceId),
                DiseaseOccurrenceReviewResponse.UNSURE
        );
        diseaseOccurrenceReviewDao.save(review);
    }

    private int createOccurrenceNeedingReview(int diseaseGroupId) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence(
                diseaseGroupDao.getById(diseaseGroupId),
                DateTime.now(),
                locationDao.getById(6),
                alertDao.getById(212855));
        occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
        diseaseOccurrenceDao.save(occurrence);
        return occurrence.getId();
    }

    @Test
    public void getDiseaseGroupsNeedingExtentReviewByExpert() {
        // Arrange
        setAuto(31);
        setAuto(112);
        setAuto(14);
        setAuto(52);
        flushAndClear();
        createAdminUnitDiseaseExtentClass(2, null, 31); // reviewed
        createAdminUnitDiseaseExtentClass(14, null, 112); // reviewed
        createAdminUnitDiseaseExtentClass(null, 13, 112); // only reviewed by other experts
        createAdminUnitDiseaseExtentClass(null, 13, 22);  // not reviewed, but not auto
        createAdminUnitDiseaseExtentClass(null, 21, 52);  // not reviewed
        AdminUnitDiseaseExtentClass toChange = createAdminUnitDiseaseExtentClass(null, 13, 14); // reviewed
        flushAndClear();
        createAdminUnitReview(1, 2, null, 31);
        createAdminUnitReview(1, 14, null, 112);
        createAdminUnitReview(2, null, 13, 112);
        createAdminUnitReview(1, null, 13, 14);
        flushAndClear();
        toChange = adminUnitDiseaseExtentClassDao.getById(toChange.getId());
        toChange.setClassChangedDate(DateTime.now().plusHours(1));
        adminUnitDiseaseExtentClassDao.save(toChange); // review is now too old
        flushAndClear();

        // Act
        List<DiseaseGroup> result = diseaseGroupDao.getDiseaseGroupsNeedingExtentReviewByExpert(1);

        // Assert
        assertThat(extract(result, on(DiseaseGroup.class).getId())).containsOnly(112, 14, 52);
    }

    private void setAuto(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        diseaseGroupDao.save(diseaseGroup);
    }

    private AdminUnitReview createAdminUnitReview(int expertId, Integer adminUnitGlobalGaulCode, Integer adminUnitTropicalGaulCode, int diseaseGroupId) {
        AdminUnitReview review = new AdminUnitReview(
                expertDao.getById(expertId),
                adminUnitGlobalGaulCode, adminUnitTropicalGaulCode,
                diseaseGroupDao.getById(diseaseGroupId),
                diseaseExtentClassDao.getByName(DiseaseExtentClass.UNCERTAIN));
        adminUnitReviewDao.save(review);
        return review;
    }

    private AdminUnitDiseaseExtentClass createAdminUnitDiseaseExtentClass(Integer adminUnitGlobalGaulCode, Integer adminUnitTropicalGaulCode, int diseaseGroupId) {
        AdminUnitDiseaseExtentClass extentClass = null;
        if (adminUnitGlobalGaulCode == null) {
            extentClass = new AdminUnitDiseaseExtentClass(
                    adminUnitTropicalDao.getByGaulCode(adminUnitTropicalGaulCode),
                    diseaseGroupDao.getById(diseaseGroupId),
                    diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE),
                    diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE),
                    3);
        } else {
            extentClass = new AdminUnitDiseaseExtentClass(
                    adminUnitGlobalDao.getByGaulCode(adminUnitGlobalGaulCode),
                    diseaseGroupDao.getById(diseaseGroupId),
                    diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE),
                    diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE),
                    3);
        }
        extentClass.setClassChangedDate(DateTime.now().minusDays(1));
        adminUnitDiseaseExtentClassDao.save(extentClass);
        return extentClass;
    }

    @Test(expected = ConstraintViolationException.class)
    public void duplicatingADiseaseGroupNameViolatesCaseInsensitiveUniqueConstraint() {
        DiseaseGroup diseaseGroup1 = diseaseGroupDao.getById(22);
        DiseaseGroup diseaseGroup2 = diseaseGroupDao.getById(87);
        String name = StringUtils.swapCase(diseaseGroup1.getName());
        diseaseGroup2.setName(name);
        diseaseGroupDao.save(diseaseGroup2);
        flushAndClear();
    }

    @Test(expected = ConstraintViolationException.class)
    public void duplicatingADiseaseGroupPublicNameViolatesCaseInsensitiveUniqueConstraint() {
        DiseaseGroup diseaseGroup1 = diseaseGroupDao.getById(22);
        DiseaseGroup diseaseGroup2 = diseaseGroupDao.getById(87);
        String publicName = StringUtils.swapCase(diseaseGroup1.getPublicName());
        diseaseGroup2.setPublicName(publicName);
        diseaseGroupDao.save(diseaseGroup2);
        flushAndClear();
    }

    @Test(expected = ConstraintViolationException.class)
    public void duplicatingADiseaseGroupShortNameViolatesCaseInsensitiveUniqueConstraint() {
        DiseaseGroup diseaseGroup1 = diseaseGroupDao.getById(22);
        DiseaseGroup diseaseGroup2 = diseaseGroupDao.getById(87);
        String shortName = StringUtils.swapCase(diseaseGroup1.getShortName());
        diseaseGroup2.setShortName(shortName);
        diseaseGroupDao.save(diseaseGroup2);
        flushAndClear();
    }

    @Test(expected = ConstraintViolationException.class)
    public void duplicatingADiseaseGroupAbbreviationViolatesCaseInsensitiveUniqueConstraint() {
        DiseaseGroup diseaseGroup1 = diseaseGroupDao.getById(22);
        DiseaseGroup diseaseGroup2 = diseaseGroupDao.getById(87);
        String abbreviation = StringUtils.swapCase(diseaseGroup1.getAbbreviation());
        diseaseGroup2.setAbbreviation(abbreviation);
        diseaseGroupDao.save(diseaseGroup2);
        flushAndClear();
    }

    private void setAutomaticModelRunsStartDate(int id) {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(id);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();
    }

    @Test
    public void getExistingDiseaseExtent() {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        DiseaseExtent diseaseExtent = diseaseGroup.getDiseaseExtentParameters();
        assertThat(diseaseExtent.getMaxMonthsAgoForHigherOccurrenceScore()).isEqualTo(24);
        assertThat(diseaseExtent.getMinValidationWeighting()).isEqualTo(0.6);
    }

    @Test
    public void updateExistingDiseaseExtent() {
        // Arrange
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        DiseaseExtent diseaseExtent = diseaseGroup.getDiseaseExtentParameters();

        // Act
        diseaseExtent.setMaxMonthsAgoForHigherOccurrenceScore(48);
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(87);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getMaxMonthsAgoForHigherOccurrenceScore()).isEqualTo(48);
    }

    @Test
    public void updateExistingDiseaseExtentWithLastOccurrences() {
        // Arrange
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        DiseaseExtent diseaseExtent = diseaseGroup.getDiseaseExtentParameters();
        List<DiseaseOccurrence> dengueOccurrences = diseaseOccurrenceDao.getByDiseaseGroupId(87);
        assertThat(dengueOccurrences).isNotEmpty();

        // Act
        diseaseExtent.setLastValidatorExtentUpdateInputOccurrences(dengueOccurrences);
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(87);
        for (DiseaseOccurrence occurrence : dengueOccurrences) {
            assertThat(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).contains(occurrence);
        }
        assertThat(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).hasSameSizeAs(dengueOccurrences);
    }

    @Test
    public void updateExistingDiseaseExtentWithLastOccurrencesTwice() {
        // Arrange
        List<DiseaseOccurrence> dengueOccurrences = diseaseOccurrenceDao.getByDiseaseGroupId(87);
        assertThat(dengueOccurrences).isNotEmpty();

        // Act
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        diseaseGroup.getDiseaseExtentParameters().setLastValidatorExtentUpdateInputOccurrences(dengueOccurrences);
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        diseaseGroup = diseaseGroupDao.getById(87);
        diseaseGroup.getDiseaseExtentParameters().setLastValidatorExtentUpdateInputOccurrences(dengueOccurrences.subList(2, 3));
        diseaseGroupDao.save(diseaseGroup);
        sessionFactory.getCurrentSession().merge(diseaseGroup);
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(87);
        for (DiseaseOccurrence occurrence : dengueOccurrences.subList(2, 3)) {
            assertThat(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).contains(occurrence);
        }
        assertThat(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).hasSameSizeAs(dengueOccurrences.subList(2, 3));
    }


    @Test
    public void addDiseaseExtentToDiseaseGroupSavesWithSameDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        DiseaseExtent diseaseExtent = new DiseaseExtent(diseaseGroup);
        diseaseGroup.setDiseaseExtentParameters(diseaseExtent);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isNotNull();
        assertThat(diseaseGroup.getDiseaseExtentParameters().getDiseaseGroupId()).isEqualTo(diseaseGroupId);
    }

    @Test
    public void getDiseaseGroupNamesForHealthMapReport() {
        // Act
        List<String> actual = diseaseGroupDao.getDiseaseGroupNamesForHealthMapReport();

        // Assert
        assertThat(actual).hasSize(34);
        assertThat(actual.get(0)).isEqualTo("Ascariasis");
        assertThat(actual.get(1)).isEqualTo("Chikungunya");
        assertThat(actual.get(2)).isEqualTo("Cholera");
    }

    @Test
    public void saveNewDiseaseGroupSavesDiseaseExtentWithSameId() {
        // Arrange
        DiseaseGroup diseaseGroup = initialiseDiseaseGroup();
        DiseaseExtent parameters = new DiseaseExtent(diseaseGroup);
        diseaseGroup.setDiseaseExtentParameters(parameters);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        // Assert
        assertThat(diseaseGroup.getId()).isNotNull();
        assertThat(parameters.getDiseaseGroupId()).isEqualTo(diseaseGroup.getId());
    }

    private DiseaseGroup initialiseDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup("Name");
        diseaseGroup.setGroupType(DiseaseGroupType.SINGLE);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        return diseaseGroup;
    }
}
