package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents an expert's response to the presence or absence of a disease group across an admin unit.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries(
        @NamedQuery(
                name = "getAdminUnitReviewsByExpertId",
                query = "from AdminUnitReview where expert.id=:expertId"
        )
)
@Entity
@Table(name = "admin_unit_review")
public class AdminUnitReview {
    // The id of the review.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The expert.
    @ManyToOne
    @JoinColumn(name = "expert_id", nullable = false)
    private Expert expert;

    // The global administrative unit.
    @ManyToOne
    @JoinColumn(name = "global_gaul_code")
    private AdminUnitGlobal adminUnitGlobal;

    // The tropical administrative unit.
    @ManyToOne
    @JoinColumn(name = "tropical_gaul_code")
    private AdminUnitTropical adminUnitTropical;

    // The disease group (to clarify, this is not referring to the validator disease group).
    @ManyToOne
    @JoinColumn(name = "disease_group_id", nullable = false)
    private DiseaseGroup diseaseGroup;

    // The expert's response.
    @Column
    @Enumerated(EnumType.STRING)
    private DiseaseExtentClass response;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public AdminUnitGlobal getAdminUnitGlobal() {
        return adminUnitGlobal;
    }

    public void setAdminUnitGlobal(AdminUnitGlobal adminUnitGlobal) {
        this.adminUnitGlobal = adminUnitGlobal;
    }

    public AdminUnitTropical getAdminUnitTropical() {
        return adminUnitTropical;
    }

    public void setAdminUnitTropical(AdminUnitTropical adminUnitTropical) {
        this.adminUnitTropical = adminUnitTropical;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public DiseaseExtentClass getResponse() {
        return response;
    }

    public void setResponse(DiseaseExtentClass response) {
        this.response = response;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminUnitReview)) return false;

        AdminUnitReview review = (AdminUnitReview) o;

        if (!createdDate.equals(review.createdDate)) return false;
        if (!diseaseGroup.equals(review.diseaseGroup)) return false;
        if (!expert.equals(review.expert)) return false;
        if (adminUnitGlobal != null ? !adminUnitGlobal.equals(review.adminUnitGlobal) : review.adminUnitGlobal != null)
            return false;
        if (!id.equals(review.id)) return false;
        if (response != review.response) return false;
        if (adminUnitTropical != null ? !adminUnitTropical.equals(review.adminUnitTropical) : review.adminUnitTropical != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + expert.hashCode();
        result = 31 * result + (adminUnitGlobal != null ? adminUnitGlobal.hashCode() : 0);
        result = 31 * result + (adminUnitTropical != null ? adminUnitTropical.hashCode() : 0);
        result = 31 * result + diseaseGroup.hashCode();
        result = 31 * result + response.hashCode();
        result = 31 * result + createdDate.hashCode();
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}