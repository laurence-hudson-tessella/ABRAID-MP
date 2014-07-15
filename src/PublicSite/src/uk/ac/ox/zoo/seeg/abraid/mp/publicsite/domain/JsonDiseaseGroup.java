package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * Represents a disease group for use in the Public Site JavaScript.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseGroup {
    private Integer id;
    private String name;
    private String publicName;
    private String shortName;
    private String abbreviation;
    private String groupType;
    private Boolean isGlobal;
    private JsonParentDiseaseGroup parentDiseaseGroup;
    private JsonValidatorDiseaseGroup validatorDiseaseGroup;
    private Double weighting;
    private boolean automaticModelRuns;
    private Integer minNewOccurrencesTrigger;
    private Integer minDataVolume;
    private Integer minDistinctCountries;
    private Integer highFrequencyThreshold;
    private Integer minHighFrequencyCountries;
    private Boolean occursInAfrica;

    public JsonDiseaseGroup() {
    }

    public JsonDiseaseGroup(DiseaseGroup diseaseGroup) {
        setId(diseaseGroup.getId());
        setName(diseaseGroup.getName());
        setPublicName(diseaseGroup.getPublicName());
        setShortName(diseaseGroup.getShortName());
        setAbbreviation(diseaseGroup.getAbbreviation());
        setGroupType(diseaseGroup.getGroupType().name());
        setIsGlobal(diseaseGroup.isGlobal());
        if (diseaseGroup.getParentGroup() != null) {
            setParentDiseaseGroup(new JsonParentDiseaseGroup(diseaseGroup.getParentGroup()));
        }
        if (diseaseGroup.getValidatorDiseaseGroup() != null) {
            setValidatorDiseaseGroup(new JsonValidatorDiseaseGroup(diseaseGroup.getValidatorDiseaseGroup()));
        }
        setWeighting(diseaseGroup.getWeighting());
        setAutomaticModelRuns(diseaseGroup.isAutomaticModelRunsEnabled());
        setMinNewOccurrencesTrigger(diseaseGroup.getMinNewOccurrencesTrigger());
        setMinDataVolume(diseaseGroup.getMinDataVolume());
        setMinDistinctCountries(diseaseGroup.getMinDistinctCountries());
        setHighFrequencyThreshold(diseaseGroup.getHighFrequencyThreshold());
        setMinHighFrequencyCountries(diseaseGroup.getMinHighFrequencyCountries());
        setOccursInAfrica(diseaseGroup.occursInAfrica());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Boolean getIsGlobal() {
        return isGlobal;
    }

    public void setIsGlobal(Boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public JsonValidatorDiseaseGroup getValidatorDiseaseGroup() {
        return validatorDiseaseGroup;
    }

    public void setValidatorDiseaseGroup(JsonValidatorDiseaseGroup validatorDiseaseGroup) {
        this.validatorDiseaseGroup = validatorDiseaseGroup;
    }

    public JsonParentDiseaseGroup getParentDiseaseGroup() {
        return parentDiseaseGroup;
    }

    public void setParentDiseaseGroup(JsonParentDiseaseGroup parentDiseaseGroup) {
        this.parentDiseaseGroup = parentDiseaseGroup;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public Integer getMinNewOccurrencesTrigger() {
        return minNewOccurrencesTrigger;
    }

    public void setMinNewOccurrencesTrigger(Integer minNewOccurrencesTrigger) {
        this.minNewOccurrencesTrigger = minNewOccurrencesTrigger;
    }

    public boolean isAutomaticModelRuns() {
        return automaticModelRuns;
    }

    public void setAutomaticModelRuns(boolean automaticModelRuns) {
        this.automaticModelRuns = automaticModelRuns;
    }

    public Integer getMinDataVolume() {
        return minDataVolume;
    }

    public void setMinDataVolume(Integer minDataVolume) {
        this.minDataVolume = minDataVolume;
    }

    public Integer getMinDistinctCountries() {
        return minDistinctCountries;
    }

    public void setMinDistinctCountries(Integer minDistinctCountries) {
        this.minDistinctCountries = minDistinctCountries;
    }

    public Integer getHighFrequencyThreshold() {
        return highFrequencyThreshold;
    }

    public void setHighFrequencyThreshold(Integer highFrequencyThreshold) {
        this.highFrequencyThreshold = highFrequencyThreshold;
    }

    public Integer getMinHighFrequencyCountries() {
        return minHighFrequencyCountries;
    }

    public void setMinHighFrequencyCountries(Integer minHighFrequencyCountries) {
        this.minHighFrequencyCountries = minHighFrequencyCountries;
    }

    public Boolean getOccursInAfrica() {
        return occursInAfrica;
    }

    public void setOccursInAfrica(Boolean occursInAfrica) {
        this.occursInAfrica = occursInAfrica;
    }
}
