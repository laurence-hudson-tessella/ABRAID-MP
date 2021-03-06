package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents an occurrence of a disease group, in a location, as reported by an alert.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getDiseaseOccurrencesByIds",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.id in :diseaseOccurrenceIds"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesByDiseaseGroupId",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id=:diseaseGroupId"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesByDiseaseGroupIdAndStatuses",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "inner join fetch d.location.country " +
                        "where d.diseaseGroup.id=:diseaseGroupId and d.status in :statuses"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesForExistenceCheck",
                query = "from DiseaseOccurrence where diseaseGroup=:diseaseGroup and location=:location " +
                        "and alert=:alert and occurrenceDate=:occurrenceDate and biasDisease is null"
                        // Using biasDisease instead of status BIAS, as bias points can be BIAS or FAILED_QC, neither
                        // should be considered for duplicates as they may be purged
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesYetToBeReviewedByExpert",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.validatorDiseaseGroup.id=:validatorDiseaseGroupId " +
                        "and (:userIsSeeg = true or d.diseaseGroup.lastModelRunPrepDate is not null) " +
                        "and d.status = 'IN_REVIEW' " +
                        "and d.id not in " +
                        "(select diseaseOccurrence.id from DiseaseOccurrenceReview where expert.id=:expertId)"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesInValidation",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id=:diseaseGroupId and d.status = 'IN_REVIEW'"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesYetToHaveFinalWeightingAssigned",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id=:diseaseGroupId " +
                        "and d.status in :statuses " +
                        "and d.finalWeighting is null "
        ),
        @NamedQuery(
                name = "getDistinctLocationsCountForTriggeringModelRunWithoutLastModelRunClause",
                query = DiseaseOccurrence.NEW_LOCATION_COUNT_QUERY
        ),
        @NamedQuery(
                name = "getDistinctLocationsCountForTriggeringModelRunWithLastModelRunClause",
                query = DiseaseOccurrence.NEW_LOCATION_COUNT_QUERY +
                        DiseaseOccurrence.NEW_LOCATION_COUNT_QUERY_NOT_IN_LAST_RUN_CLAUSE
        ),
        @NamedQuery(
                name = "getDiseaseOccurrenceStatistics",
                query = "select new uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatistics" +
                            "(count(*), coalesce(sum(case location.isModelEligible when true then 1 else 0 end), 0)," +
                            " min(occurrenceDate), max(occurrenceDate)) " +
                        "from DiseaseOccurrence " +
                        "where diseaseGroup.id=:diseaseGroupId " +
                        "and status in ('READY', 'IN_REVIEW', 'AWAITING_BATCHING')"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesForBatching",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id=:diseaseGroupId " +
                        "and d.status = 'AWAITING_BATCHING' " +
                        "and d.occurrenceDate between :batchStartDate and :batchEndDate " +
                        "and " + DiseaseOccurrence.NOT_GOLD_STANDARD
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesForBatchingInitialisation",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id=:diseaseGroupId " +
                        "and d.status = 'READY' " +
                        "and " + DiseaseOccurrence.NOT_GOLD_STANDARD
        ),
        @NamedQuery(
                name = "getNumberOfDiseaseOccurrencesEligibleForModelRun",
                query = "select count(*) " +
                        "from DiseaseOccurrence " +
                        "where diseaseGroup.id=:diseaseGroupId " +
                        "and status in ('READY', 'IN_REVIEW', 'AWAITING_BATCHING') " +
                        "and location.isModelEligible is TRUE " +
                        "and occurrenceDate between :startDate and :endDate"
        ),
        @NamedQuery(
                name = "getCountOfUnfilteredBespokeBiasOccurrences",
                query = "select count(*) " +
                        "from DiseaseOccurrence d " +
                        "where d.biasDisease.id=:diseaseGroupId"
        ),
        @NamedQuery(
                name = "getEstimateCountOfFilteredBespokeBiasOccurrences",
                // This only an estimate as we can not apply the occurrence date filter used in
                // 'getBespokeBiasOccurrences' outside of the context of a model run as the date range is unknown
                query = "select count(*) " +
                        "from DiseaseOccurrence d " +
                        "where d.biasDisease.id=:diseaseGroupId " +
                        "and d.status = 'BIAS' " +
                        DiseaseOccurrence.BIAS_OCCURRENCE_LOCATION_FILTER_CLAUSES
        ),
        @NamedQuery(
                name = "getEstimateCountOfFilteredDefaultBiasOccurrences",
                // This only an estimate as we can not apply the occurrence date filter used in
                // 'getDefaultBiasOccurrencesForModelRun' outside of a model run as the date range is unknown
                query = "select count(*) " +
                        "from DiseaseOccurrence d " +
                        "where d.diseaseGroup.id<>:diseaseGroupId " +
                        "and d.status NOT IN ('DISCARDED_FAILED_QC', 'BIAS') " +
                        DiseaseOccurrence.BIAS_OCCURRENCE_AGENT_TYPE_FILTER_CLAUSE +
                        DiseaseOccurrence.BIAS_OCCURRENCE_LOCATION_FILTER_CLAUSES
        ),
        @NamedQuery(
                name = "getBespokeBiasOccurrencesForModelRun",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.biasDisease.id=:diseaseGroupId " +
                        "and d.status = 'BIAS' " +
                        DiseaseOccurrence.BIAS_OCCURRENCE_LOCATION_FILTER_CLAUSES +
                        DiseaseOccurrence.OCCURRENCE_DATE_FILTER_CLAUSE
        ),
        @NamedQuery(
                name = "getDefaultBiasOccurrencesForModelRun",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id<>:diseaseGroupId " +
                        "and d.status NOT IN ('DISCARDED_FAILED_QC', 'BIAS') " +
                        // As this data set is for sample bias, we don't need to apply our
                        // normal filters (READY/final weighting), but we don't want bias points if there aren't
                        // bias points uploaded for this disease
                        DiseaseOccurrence.BIAS_OCCURRENCE_AGENT_TYPE_FILTER_CLAUSE +
                        DiseaseOccurrence.BIAS_OCCURRENCE_LOCATION_FILTER_CLAUSES +
                        DiseaseOccurrence.OCCURRENCE_DATE_FILTER_CLAUSE
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesForTrainingPredictor",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id=:diseaseGroupId " +
                        "and d.status = 'READY' " +
                        "and d.location.isModelEligible is TRUE " +
                        "and d.distanceFromDiseaseExtent is not null " +
                        "and d.environmentalSuitability is not null " +
                        "and d.expertWeighting is not null " +
                        "and d.occurrenceDate > :cutOffDate"
        ),
        @NamedQuery(
                name = "deleteDiseaseOccurrencesByBiasDiseaseId",
                query = "delete from DiseaseOccurrence where biasDisease.id=:diseaseGroupId"
        )
})
@Entity
@Table(name = "disease_occurrence")
public class DiseaseOccurrence {
    /**
     * An HQL fragment used as a basis for disease occurrence queries. It ensures that Hibernate populate the objects
     * and their parents using one select statement.
     */
    public static final String DISEASE_OCCURRENCE_BASE_QUERY =
            "from DiseaseOccurrence as d " +
            "inner join fetch d.location " +
            "inner join fetch d.alert " +
            "inner join fetch d.alert.feed " +
            "inner join fetch d.alert.feed.provenance " +
            "inner join fetch d.diseaseGroup ";

    /**
     * An HQL fragment used to exclude gold standard occurrences from queries.
     */
    public static final String NOT_GOLD_STANDARD =
            "d.alert.feed.provenance.name <> '" + ProvenanceNames.MANUAL_GOLD_STANDARD + "'";

    /**
     * The final weighting assigned to a "gold standard" disease occurrence.
     */
    public static final double GOLD_STANDARD_FINAL_WEIGHTING = 1.0;

    /**
     * An HQL fragment used to count the new location for model triggering.
     */
    public static final String NEW_LOCATION_COUNT_QUERY =
            "select count(distinct d.location.id) " +
            "from DiseaseOccurrence as d " +
            "where d.diseaseGroup.id=:diseaseGroupId " +
            // Model eligible locations
            "and d.location.isModelEligible is TRUE " +
            // Linked to occurrences that were not available for use in the last model run
            "and d.status='READY' " +
            "and (" +
                "(d.expertWeighting is NULL and createdDate > :cutoffForAutomaticallyValidated) " +
                "or " +
                "(d.expertWeighting is not NULL and createdDate > :cutoffForManuallyValidated) " +
            ") " +
            // Which are in areas of interest (new places)
            "and (" +
                "environmentalSuitability <= :maxEnvironmentalSuitability " +
                "or " +
                "distanceFromDiseaseExtent >= :minDistanceFromDiseaseExtent" +
            ")";

    /**
     * An HQL fragment used to count the new location for model triggering.
     * "That were not used in the last model run"
     */
    public static final String NEW_LOCATION_COUNT_QUERY_NOT_IN_LAST_RUN_CLAUSE =
            " and d.location.id not in :locationsFromLastModelRun";

    /** A HQL fragment to choose between global and tropical gaul codes. */
    public static final String PICK_EXTENT_GAUL_CODE =
            "CASE :isGlobal WHEN true " +
            "  THEN d.location.adminUnitGlobalGaulCode " +
            "  ELSE d.location.adminUnitTropicalGaulCode " +
            "END";

    /** A HQL fragment to filter occurrences by location when selecting a bias data set.
     * Filters by model eligibiliy (size) and disease extent (within only)
     */
    public static final String BIAS_OCCURRENCE_LOCATION_FILTER_CLAUSES =
            "and d.location.isModelEligible is TRUE " +
            "and (" + DiseaseOccurrence.PICK_EXTENT_GAUL_CODE + ") " +
            "in (" + AdminUnitDiseaseExtentClass.EXTENT_GAUL_CODES_BY_DISEASE_GROUP_ID + ") ";

    /** A HQL fragment to filter occurrences by disease group agent type when selecting a bias data set.
     */
    public static final String BIAS_OCCURRENCE_AGENT_TYPE_FILTER_CLAUSE =
            "and (:shouldFilterBiasDataByAgentType is FALSE or d.diseaseGroup.agentType=:agentType) ";

    /** A HQL fragment to filter occurrences within an occurrence date range. */
    public static final String OCCURRENCE_DATE_FILTER_CLAUSE =
            "and d.occurrenceDate between :startDate and :endDate ";

    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The disease group that occurred.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "disease_group_id", nullable = false)
    private DiseaseGroup diseaseGroup;

    // The location of this occurrence.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // The alert containing this occurrence.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    // The status of this occurrence.
    @Column
    @Enumerated(EnumType.STRING)
    private DiseaseOccurrenceStatus status;

    // The suitability of the environment for this disease group to exist in this location.
    @Column(name = "env_suitability")
    private Double environmentalSuitability;

    // The distance between this location and the edge of the disease extent. The value is positive if the location
    // is outside of the extent, or negative if inside.
    @Column(name = "distance_from_extent")
    private Double distanceFromDiseaseExtent;

    // The weighting as calculated from experts' responses during data validation process.
    @Column(name = "expert_weighting")
    private Double expertWeighting;

    // The weighting as predicted via the system (which may include machine learning).
    @Column(name = "machine_weighting")
    private Double machineWeighting;

    // The validation weighting used in the data weighting formula.
    // Takes the value of the expertWeighting if it exists, otherwise the machineWeighting value.
    @Column(name = "validation_weighting")
    private Double validationWeighting;

    // The final weighting to be used in the model run,
    // combining location resolution weighting, feed weighting, disease group type weighting and validation weighting.
    @Column(name = "final_weighting")
    private Double finalWeighting;

    // The final weighting to be used in later model runs, following a refactor where location's spatial resolution
    // weighting is handled separately. This value combines feed weighting, disease group type weighting and validation
    // weighting.
    @Column(name = "final_weighting_excl_spatial")
    private Double finalWeightingExcludingSpatial;

    // The date of the disease occurrence.
    @Column(name = "occurrence_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime occurrenceDate;

    // The disease group for which this occurrence is part of a bias set (or null).
    @ManyToOne
    @JoinColumn(name = "bias_disease_group_id", nullable = true)
    private DiseaseGroup biasDisease;

    public DiseaseOccurrence() {
    }

    public DiseaseOccurrence(int id) {
        this.id = id;
    }

    public DiseaseOccurrence(DiseaseGroup diseaseGroup, DateTime occurrenceDate, Location location, Alert alert) {
        this.diseaseGroup = diseaseGroup;
        this.occurrenceDate = occurrenceDate;
        this.location = location;
        this.alert = alert;
    }

    public DiseaseOccurrence(Integer id, DiseaseGroup diseaseGroup, Location location, Alert alert,
                             DiseaseOccurrenceStatus status, Double finalWeighting, DateTime occurrenceDate) {
        this.id = id;
        this.diseaseGroup = diseaseGroup;
        this.location = location;
        this.alert = alert;
        this.status = status;
        this.finalWeighting = finalWeighting;
        this.occurrenceDate = occurrenceDate;
    }

    public DiseaseOccurrence(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public ValidatorDiseaseGroup getValidatorDiseaseGroup() {
        return diseaseGroup.getValidatorDiseaseGroup();
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public DiseaseOccurrenceStatus getStatus() {
        return status;
    }

    public void setStatus(DiseaseOccurrenceStatus status) {
        this.status = status;
    }

    public Double getEnvironmentalSuitability() {
        return environmentalSuitability;
    }

    public void setEnvironmentalSuitability(Double environmentalSuitability) {
        this.environmentalSuitability = environmentalSuitability;
    }

    public Double getDistanceFromDiseaseExtent() {
        return distanceFromDiseaseExtent;
    }

    public void setDistanceFromDiseaseExtent(Double distanceFromDiseaseExtent) {
        this.distanceFromDiseaseExtent = distanceFromDiseaseExtent;
    }

    public Double getExpertWeighting() {
        return expertWeighting;
    }

    public void setExpertWeighting(Double expertWeighting) {
        this.expertWeighting = expertWeighting;
    }

    public Double getMachineWeighting() {
        return machineWeighting;
    }

    public void setMachineWeighting(Double machineWeighting) {
        this.machineWeighting = machineWeighting;
    }

    public Double getValidationWeighting() {
        return validationWeighting;
    }

    public void setValidationWeighting(Double validationWeighting) {
        this.validationWeighting = validationWeighting;
    }

    public Double getFinalWeighting() {
        return finalWeighting;
    }

    public void setFinalWeighting(Double finalWeighting) {
        this.finalWeighting = finalWeighting;
    }

    public Double getFinalWeightingExcludingSpatial() {
        return finalWeightingExcludingSpatial;
    }

    public void setFinalWeightingExcludingSpatial(Double finalWeightingExcludingSpatial) {
        this.finalWeightingExcludingSpatial = finalWeightingExcludingSpatial;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(DateTime occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
    }

    public DiseaseGroup getBiasDisease() {
        return biasDisease;
    }

    public void setBiasDisease(DiseaseGroup biasDisease) {
        this.biasDisease = biasDisease;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiseaseOccurrence that = (DiseaseOccurrence) o;

        if (alert != null ? !alert.equals(that.alert) : that.alert != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (distanceFromDiseaseExtent != null ? !distanceFromDiseaseExtent.equals(that.distanceFromDiseaseExtent) : that.distanceFromDiseaseExtent != null)
            return false;
        if (environmentalSuitability != null ? !environmentalSuitability.equals(that.environmentalSuitability) : that.environmentalSuitability != null)
            return false;
        if (expertWeighting != null ? !expertWeighting.equals(that.expertWeighting) : that.expertWeighting != null)
            return false;
        if (finalWeighting != null ? !finalWeighting.equals(that.finalWeighting) : that.finalWeighting != null)
            return false;
        if (finalWeightingExcludingSpatial != null ? !finalWeightingExcludingSpatial.equals(that.finalWeightingExcludingSpatial) : that.finalWeightingExcludingSpatial != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (machineWeighting != null ? !machineWeighting.equals(that.machineWeighting) : that.machineWeighting != null)
            return false;
        if (occurrenceDate != null ? !occurrenceDate.equals(that.occurrenceDate) : that.occurrenceDate != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (validationWeighting != null ? !validationWeighting.equals(that.validationWeighting) : that.validationWeighting != null)
            return false;
        if (biasDisease != null ? !biasDisease.equals(that.biasDisease) : that.biasDisease != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (alert != null ? alert.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (environmentalSuitability != null ? environmentalSuitability.hashCode() : 0);
        result = 31 * result + (distanceFromDiseaseExtent != null ? distanceFromDiseaseExtent.hashCode() : 0);
        result = 31 * result + (expertWeighting != null ? expertWeighting.hashCode() : 0);
        result = 31 * result + (machineWeighting != null ? machineWeighting.hashCode() : 0);
        result = 31 * result + (validationWeighting != null ? validationWeighting.hashCode() : 0);
        result = 31 * result + (finalWeighting != null ? finalWeighting.hashCode() : 0);
        result = 31 * result + (finalWeightingExcludingSpatial != null ? finalWeightingExcludingSpatial.hashCode() : 0);
        result = 31 * result + (occurrenceDate != null ? occurrenceDate.hashCode() : 0);
        result = 31 * result + (biasDisease != null ? biasDisease.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
