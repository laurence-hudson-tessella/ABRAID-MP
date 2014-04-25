package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitReviewDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitReviewDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Autowired
    private AdminUnitReviewDao adminUnitReviewDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private ExpertDao expertDao;

    @Test
    public void saveAndReloadGlobalAdminUnitReview() {
        // Arrange
        Expert expert = expertDao.getById(2);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(1);
        AdminUnitGlobal adminUnitGlobal = adminUnitGlobalDao.getByGaulCode(2);
        DiseaseExtentClass response = DiseaseExtentClass.PRESENCE;

        AdminUnitReview review = createAdminUnitReview(expert, adminUnitGlobal, diseaseGroup, response);

        // Act
        adminUnitReviewDao.save(review);

        // Assert
        assertThat(review.getId()).isNotNull();
        assertThat(review.getCreatedDate()).isNotNull();

        Integer id = review.getId();
        flushAndClear();
        review = adminUnitReviewDao.getById(id);
        assertThat(review).isNotNull();
        assertThat(review.getExpert().getEmail()).isEqualTo(expert.getEmail());
        assertThat(review.getExpert().getValidatorDiseaseGroups()).containsAll(expert.getValidatorDiseaseGroups());
        assertThat(review.getDiseaseGroup()).isEqualTo(diseaseGroup);
        assertThat(review.getAdminUnitGlobal()).isEqualTo(adminUnitGlobal);
        assertThat(review.getAdminUnitTropical()).isNull();
        assertThat(review.getResponse()).isEqualTo(response);
    }

    private AdminUnitReview createAdminUnitReview(Expert expert, AdminUnitGlobal adminUnitGlobal,
                                                  DiseaseGroup diseaseGroup, DiseaseExtentClass response) {
        AdminUnitReview review = new AdminUnitReview();
        review.setExpert(expert);
        review.setAdminUnitGlobal(adminUnitGlobal);
        review.setDiseaseGroup(diseaseGroup);
        review.setResponse(response);
        return review;
    }
}