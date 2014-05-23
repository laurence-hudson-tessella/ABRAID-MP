package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents a run of the SEEG model.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries(
        @NamedQuery(
                name = "getModelRunByName",
                query = "from ModelRun where name=:name"
        )
)
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

    public ModelRun() {
    }

    public ModelRun(int id) {
        this.id = id;
    }

    public ModelRun(String name, int diseaseGroupId, DateTime requestDate) {
        this.name = name;
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

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelRun modelRun = (ModelRun) o;

        if (diseaseGroupId != modelRun.diseaseGroupId) return false;
        if (id != null ? !id.equals(modelRun.id) : modelRun.id != null) return false;
        if (name != null ? !name.equals(modelRun.name) : modelRun.name != null) return false;
        if (requestDate != null ? !requestDate.equals(modelRun.requestDate) : modelRun.requestDate != null)
            return false;
        if (responseDate != null ? !responseDate.equals(modelRun.responseDate) : modelRun.responseDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + diseaseGroupId;
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (responseDate != null ? responseDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
