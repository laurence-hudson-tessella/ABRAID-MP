package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvCovariateInfluence;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents the influence of a covariate file on a model run.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "covariate_influence")
@NamedQueries({
        @NamedQuery(
                name = "getCovariateInfluencesForModelRun",
                query = "from CovariateInfluence where modelRun=:modelRun"
        )
})
public class CovariateInfluence extends AbstractCovariateInfluence {
    public CovariateInfluence() {
        super();
    }

    public CovariateInfluence(CovariateFile covariate, CsvCovariateInfluence dto, ModelRun parentRun) {
        super(covariate, dto, parentRun);
    }

    public CovariateInfluence(CovariateFile covariate, Double meanInfluence) {
        super(covariate, meanInfluence);
    }
}
