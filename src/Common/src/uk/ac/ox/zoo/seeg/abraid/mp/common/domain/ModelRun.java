package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

/**
 * Represents a run of the SEEG model.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getModelRunByName",
                query = "from ModelRun where name=:name"
        ),
        @NamedQuery(
                name = "getLastRequestedModelRun",
                query = "from ModelRun " +
                        "where diseaseGroupId=:diseaseGroupId " +
                        "and requestDate = " +
                        "   (select max(requestDate) from ModelRun" +
                        "    where diseaseGroupId = :diseaseGroupId)"
        ),
        @NamedQuery(
                name = "getLastCompletedModelRun",
                query = "from ModelRun " +
                        "where diseaseGroupId=:diseaseGroupId " +
                        "and status = 'COMPLETED' " +
                        "and responseDate =" +
                        "   (select max(responseDate) from ModelRun" +
                        "    where diseaseGroupId = :diseaseGroupId" +
                        "    and status = 'COMPLETED')"
        ),
        @NamedQuery(
                name = "hasBatchingEverCompleted",
                query = "select count(*) from ModelRun " +
                        "where diseaseGroupId = :diseaseGroupId " +
                        "and batchingCompletedDate is not null"
        )
})
@Entity
@Table(name = "model_run")
public class ModelRun {
    // The model run ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The model run name, as returned by the ModelWrapper.
    @Column
    private String name;

    // The status of the model run.
    @Column
    @Enumerated(EnumType.STRING)
    private ModelRunStatus status;

    // The ID of the disease group for the model run.
    @Column(name = "disease_group_id")
    private int diseaseGroupId;

    // The date that the model run was requested.
    @Column(name = "request_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime requestDate;

    // The date that the outputs for this model run were received.
    @Column(name = "response_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime responseDate;

    // The output text from the model run (stdout).
    @Column(name = "output_text")
    private String outputText;

    // The error text from the model run (stderr).
    @Column(name = "error_text")
    private String errorText;

    // List of submodel statistics associated with the model run.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modelRun")
    @Fetch(FetchMode.SELECT)
    private List<SubmodelStatistic> submodelStatistics;

    // List of covariate influences associated with the model run.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modelRun")
    @Fetch(FetchMode.SELECT)
    private List<CovariateInfluence> covariateInfluences;

    // The end date of this batch of disease occurrences (if relevant).
    @Column(name = "batch_end_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime batchEndDate;

    // The date that batching for this model run completed (if relevant).
    @Column(name = "batching_completed_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime batchingCompletedDate;

    public ModelRun() {
    }

    public ModelRun(int id) {
        this.id = id;
    }

    public ModelRun(String name, int diseaseGroupId, DateTime requestDate) {
        this.name = name;
        this.status = ModelRunStatus.IN_PROGRESS;
        this.requestDate = requestDate;
        this.diseaseGroupId = diseaseGroupId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelRunStatus getStatus() {
        return status;
    }

    public void setStatus(ModelRunStatus status) {
        this.status = status;
    }

    public int getDiseaseGroupId() {
        return diseaseGroupId;
    }

    public void setDiseaseGroupId(int diseaseGroupId) {
        this.diseaseGroupId = diseaseGroupId;
    }

    public DateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(DateTime requestDate) {
        this.requestDate = requestDate;
    }

    public DateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(DateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public List<SubmodelStatistic> getSubmodelStatistics() {
        return submodelStatistics;
    }

    public void setSubmodelStatistics(List<SubmodelStatistic> submodelStatistics) {
        this.submodelStatistics = submodelStatistics;
    }

    public List<CovariateInfluence> getCovariateInfluences() {
        return covariateInfluences;
    }

    public void setCovariateInfluences(List<CovariateInfluence> covariateInfluences) {
        this.covariateInfluences = covariateInfluences;
    }

    public DateTime getBatchEndDate() {
        return batchEndDate;
    }

    public void setBatchEndDate(DateTime batchEndDate) {
        this.batchEndDate = batchEndDate;
    }

    public DateTime getBatchingCompletedDate() {
        return batchingCompletedDate;
    }

    public void setBatchingCompletedDate(DateTime batchingCompletedDate) {
        this.batchingCompletedDate = batchingCompletedDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelRun modelRun = (ModelRun) o;

        if (diseaseGroupId != modelRun.diseaseGroupId) return false;
        if (batchEndDate != null ? !batchEndDate.equals(modelRun.batchEndDate) : modelRun.batchEndDate != null)
            return false;
        if (batchingCompletedDate != null ? !batchingCompletedDate.equals(modelRun.batchingCompletedDate) : modelRun.batchingCompletedDate != null)
            return false;
        if (covariateInfluences != null ? !covariateInfluences.equals(modelRun.covariateInfluences) : modelRun.covariateInfluences != null)
            return false;
        if (errorText != null ? !errorText.equals(modelRun.errorText) : modelRun.errorText != null) return false;
        if (id != null ? !id.equals(modelRun.id) : modelRun.id != null) return false;
        if (name != null ? !name.equals(modelRun.name) : modelRun.name != null) return false;
        if (outputText != null ? !outputText.equals(modelRun.outputText) : modelRun.outputText != null) return false;
        if (requestDate != null ? !requestDate.equals(modelRun.requestDate) : modelRun.requestDate != null)
            return false;
        if (responseDate != null ? !responseDate.equals(modelRun.responseDate) : modelRun.responseDate != null)
            return false;
        if (status != modelRun.status) return false;
        if (submodelStatistics != null ? !submodelStatistics.equals(modelRun.submodelStatistics) : modelRun.submodelStatistics != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + diseaseGroupId;
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (responseDate != null ? responseDate.hashCode() : 0);
        result = 31 * result + (outputText != null ? outputText.hashCode() : 0);
        result = 31 * result + (errorText != null ? errorText.hashCode() : 0);
        result = 31 * result + (submodelStatistics != null ? submodelStatistics.hashCode() : 0);
        result = 31 * result + (covariateInfluences != null ? covariateInfluences.hashCode() : 0);
        result = 31 * result + (batchEndDate != null ? batchEndDate.hashCode() : 0);
        result = 31 * result + (batchingCompletedDate != null ? batchingCompletedDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
