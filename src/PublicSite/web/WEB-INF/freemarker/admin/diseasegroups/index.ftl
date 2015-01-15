<#--
    The system administration page for disease groups.
    Copyright (c) 2014 University of Oxford
-->
<#import "../../layout/common.ftl" as c/>
<#import "../../shared/layout/form.ftl" as f/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var diseaseGroups = ${diseaseGroups};
    var validatorDiseaseGroups = ${validatorDiseaseGroups};
</script>
</#assign>

<#assign css>
<style>
    #disease-group-settings .input-group, #model-run-parameters .input-group, #disease-extent-parameters .input-group {
        padding-left: 15px;
        padding-right: 15px;
    }
    #disease-group-settings label, #model-run-parameters label, #disease-extent-parameters label {
        text-align: left;
    }

    @media (min-width: 730px) {
        #disease-groups-list .right-buttons {
            float: right;
            max-width: 350px;
        }
        #disease-groups-list .left-control {
            margin-right: 350px;
        }
    }
    @media (max-width: 730px) {
        #disease-groups-list .right-buttons {
            width: 100%;
            text-align: center;
        }
        #disease-groups-list .right-buttons > span button {
            margin-bottom: 10px;
        }
    }
</style>
</#assign>

<@c.page title="ABRAID-MP Administration: Diseases" mainjs="/js/kickstart/admin/diseaseGroup" bootstrapData=bootstrapData endOfHead=css>
<div class="container">
    <div id="disease-groups-list">
        <div class="right-buttons">
            <span data-bind="with: diseaseGroupsListViewModel">
                <button type="button" class="btn btn-primary" data-bind="click: add">Add new disease</button>
            </span>
            <span data-bind="with: syncDiseasesViewModel">
                <form id="sync-diseases-form" action="" data-bind="formSubmit: submit" style="display: inline-block; margin: 0">
                    <button type="submit" class="btn btn-primary disabled" data-bind="formButton: { submitting: 'Syncing ...', standard: 'Sync all diseases to model' }" disabled>Sync all diseases to model</button>
                </form>
            </span>
        </div>
        <div class="left-control" data-bind="with: diseaseGroupsListViewModel">
            <label for="disease-group-picker" class="side-by-side">Selected Disease</label>
            <span class="input-group">
                <span class="input-group-addon">
                    <i class="fa fa-medkit"></i>
                </span>
                <select id="disease-group-picker" class="form-control" data-bind="options: diseaseGroups, value: selectedDiseaseGroup, optionsText: 'name', valueAllowUnset: true" ></select>
            </span>
        </div>
        <div style="clear: both;"></div>
        <!-- ko with: syncDiseasesViewModel -->
            <div class="form-group" data-bind="foreach: notices">
                <div data-bind="alert: $data"></div>
            </div>
        <!-- /ko -->
    </div>
    <div class="panel panel-default">
        <div class="panel-body" id="disease-group-administration-panel">
            <@f.form formId="disease-group-administration">
                <div data-bind="with: diseaseGroupSettingsViewModel">
                    <#include "settingspanel.ftl"/>
                </div>
                <div data-bind="with: modelRunParametersViewModel">
                    <#include "modelrunparameterspanel.ftl"/>
                </div>
                <div data-bind="with: diseaseExtentParametersViewModel">
                    <#include "diseaseextentparameterspanel.ftl"/>
                </div>
            </@f.form>
        </div>
    </div>
    <#include "modelrunssetuppanel.ftl"/>
</div>
</@c.page>