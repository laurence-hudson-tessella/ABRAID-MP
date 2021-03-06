package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;

import java.util.List;

/**
 * The DiseaseOccurrenceReview entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseOccurrenceReviewDaoImpl extends AbstractDao<DiseaseOccurrenceReview, Integer>
        implements DiseaseOccurrenceReviewDao {
    public DiseaseOccurrenceReviewDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets the reviews submitted by reliable experts (whose weighting is greater than the threshold) for the disease
     * occurrences which are in review. "I don't know" response are excluded.
     * @param diseaseGroupId The ID of the disease group.
     * @param expertWeightingThreshold Reviews by experts with a weighting greater than this value will be considered.
     * @return A list of disease occurrence reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getDiseaseOccurrenceReviewsForOccurrencesInValidationForUpdatingWeightings(
            Integer diseaseGroupId, Double expertWeightingThreshold) {
        return listNamedQuery("getDiseaseOccurrenceReviewsForOccurrencesInValidationForUpdatingWeightings",
                "diseaseGroupId", diseaseGroupId, "expertWeightingThreshold", expertWeightingThreshold);
    }

    /**
     * Gets the total number of reviews submitted by the specified expert.
     * @param expertId The expert's Id.
     * @return The count of the expert's reviews.
     */
    @Override
    public Long getCountByExpertId(Integer expertId) {
        Query query = getParameterisedNamedQuery("getDiseaseOccurrenceReviewCountByExpertId", "expertId", expertId);
        return (Long) query.uniqueResult();
    }

    /**
     * Gets the date of the last disease occurrence review submitted by a specific expert.
     * @param expertId The expert's Id.
     * @return The date of the last disease occurrence review.
     */
    @Override
    public DateTime getLastReviewDateByExpertId(Integer expertId) {
        Query query = getParameterisedNamedQuery("getLastDiseaseOccurrenceReviewDateByExpertId", "expertId", expertId);
        return (DateTime) query.uniqueResult();
    }

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert,
     * already exists in the database.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @param expertId The id of the specified expert.
     * @return True if the review already exists, otherwise false.
     */
    @Override
    public boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId) {
        Query query = getParameterisedNamedQuery("getDiseaseOccurrenceReviewByExpertIdAndDiseaseOccurrenceId",
                "expertId", expertId, "diseaseOccurrenceId", diseaseOccurrenceId);
        return query.uniqueResult() != null;
    }
}
