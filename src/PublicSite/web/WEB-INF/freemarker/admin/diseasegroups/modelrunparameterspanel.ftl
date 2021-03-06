<#import "../../shared/layout/form.ftl" as f/>

<#macro formGroup id title bind>
<div class="form-group">
    <label for="${id}" class="col-sm-8 control-label">${title}</label>
    <div class="input-group col-sm-4">
        <input type="text" class="form-control" id="${id}" data-bind="${bind}">
    </div>
</div>
</#macro>

<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#model-run-parameters">
                Model Run Parameters
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse" id="model-run-parameters">
        <div class="panel-body">
            <div class="col-sm-6">
                <div class="form-horizontal">
                    Triggering a Model Run:
                    <br>
                    <@formGroup id="max-days-between-runs" title="Max. Number of Days Between Runs" bind="formValue: maxDaysBetweenModelRuns"></@formGroup>
                    <@formGroup id="min-new-locations" title="Min. Number of New Locations" bind="formValue: minNewLocations"></@formGroup>
                    <@formGroup id="max-environmental-suitability-for-triggering" title="Max. Environmental Suitability" bind="formValue: maxEnvironmentalSuitabilityForTriggering"></@formGroup>
                    <@formGroup id="min-distance-from-extent-for-triggering" title="Min. Distance from Disease Extent" bind="formValue: minDistanceFromDiseaseExtentForTriggering"></@formGroup>
                    Machine Learning:
                    <br>
                    <div class="form-group">
                        <label for="use-machine-learning" class="col-sm-8 control-label">Use Machine Learning</label>
                        <div class="col-sm-4">
                            <input type="checkbox" id="use-machine-learning" data-bind="formChecked: useMachineLearning">
                        </div>
                    </div>
                    <@formGroup id="max-environmental-suitability-without-ml" title="Max. Environmental Suitability of Points for the Validator If Not Using Machine Learning" bind="syncValue: maxEnvironmentalSuitabilityWithoutML, bootstrapDisable: find('isSubmitting') || useMachineLearning()"></@formGroup>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-horizontal">
                    SDM:<br>
                    <@formGroup id="model-mode" title="Model Mode" bind="formValue: modelMode"></@formGroup>
                    <div class="form-group">
                        <label for="agent-type" class="col-sm-6 control-label">Agent Type</label>
                        <div class="input-group col-sm-6">
                            <select class="form-control" id="agent-type" data-bind="options: agentTypes, value: agentType, optionsCaption:'Select one...', bootstrapDisable: find('isSubmitting')"></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="filter-bias-data" class="col-sm-8 control-label">Filter Background Data By Agent Type</label>
                        <div class="col-sm-4">
                            <input type="checkbox" id="filter-bias-data" data-bind="formChecked: filterBiasDataByAgentType">
                        </div>
                    </div>
                    Minimum Data Volume and Minimum Data Spread:
                    <br>
                    <@formGroup id="min-data-volume" title="Min. Data Volume" bind="formValue: minDataVolume"></@formGroup>
                    <@formGroup id="min-distinct-countries" title="Min. Number of Distinct Countries" bind="formValue: minDistinctCountries"></@formGroup>
                    <div class="form-group">
                        <label for="occurs-in-africa" class="col-sm-8 control-label">Occurs in Africa</label>
                        <div class="col-sm-4">
                            <input type="checkbox" id="occurs-in-africa" data-bind="formChecked: occursInAfrica">
                        </div>
                    </div>
                    <@formGroup id="min-high-frequency-countries" title="Min. Number of High Frequency Countries" bind="syncValue: minHighFrequencyCountries, bootstrapDisable: find('isSubmitting') || !occursInAfrica()"></@formGroup>
                    <@formGroup id="high-frequency-threshold" title="Min. Number of Occurrences to be deemed a High Frequency Country" bind="syncValue: highFrequencyThreshold, bootstrapDisable: find('isSubmitting') || !occursInAfrica()"></@formGroup>
                    <p class="form-group" style="text-align: center">
                        <a class="btn btn-primary" data-bind="attr: { href: diseaseOccurrenceSpreadUrl() }, bootstrapDisable: !diseaseOccurrenceSpreadButtonEnabled()" target="_blank">Show Disease Occurrence Spread</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
