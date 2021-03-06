package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils.captorForClass;

/**
 * Tests the Atlas controller.
 * Copyright (c) 2014 University of Oxford
 */
public class AtlasControllerTest {

    @Test
    public void showPageReturnsAtlasPage() {
        // Arrange
        AtlasController target = new AtlasController(null, null, null, null, null);

        // Act
        String result = target.showPage();

        // Assert
        assertThat(result).isEqualTo("atlas/index");
    }

    @Test
    public void showAtlasReturnsAtlasContent() throws JsonProcessingException {
        // Arrange
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        ExpertService expertService = mock(ExpertService.class);
        mockSeegExpert(currentUserService, expertService, false);
        AtlasController target = new AtlasController(mock(ModelRunService.class), mock(DiseaseService.class),
                currentUserService, expertService, mock(AbraidJsonObjectMapper.class));

        // Act
        String result = target.showAtlas(mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("atlas/content");
    }

    @Test
    public void showAtlasAddsAllDiseaseGroupsWithCompletedModelRunsToMapIfUserIsSeeg() throws Exception {
        String expectation = "[" +
            "{\"disease\":\"Disease Group 1\",\"runs\":[" +
                "{\"date\":\"2014-10-13\",\"id\":\"Model Run 1\",\"rangeStart\":\"1999-10-13\",\"rangeEnd\":\"2001-10-13\",\"automatic\":true}," +
                "{\"date\":\"2036-12-18\",\"id\":\"Model Run 3\",\"rangeStart\":\"1997-12-18\",\"rangeEnd\":\"2003-12-18\",\"automatic\":true}" +
            "]}," +
                "{\"disease\":\"Disease Group 2\",\"runs\":[" +
                "{\"date\":\"1995-10-09\",\"id\":\"Model Run 2\",\"rangeStart\":\"1998-10-09\",\"rangeEnd\":\"2002-10-09\",\"automatic\":false}" +
            "]}" +
        "]";
        showAtlasTemplatesTheCorrectData(true, expectation);
    }

    @Test
    public void showAtlasAddsOnlyAutomaticModelRunsEnabledDiseaseGroupsWithCompletedModelRunsIfUserIsNotSeeg() throws Exception {
        String expectation = "[" +
            "{\"disease\":\"Disease Group 1\",\"runs\":[" +
                "{\"date\":\"2014-10-13\",\"id\":\"Model Run 1\",\"rangeStart\":\"1999-10-13\",\"rangeEnd\":\"2001-10-13\",\"automatic\":true}," +
                "{\"date\":\"2036-12-18\",\"id\":\"Model Run 3\",\"rangeStart\":\"1997-12-18\",\"rangeEnd\":\"2003-12-18\",\"automatic\":true}" +
            "]}" +
        "]";
        showAtlasTemplatesTheCorrectData(false, expectation);
    }

    private void showAtlasTemplatesTheCorrectData(boolean isSeegMember, String expectation) throws Exception {
        // Arrange
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        ExpertService expertService = mock(ExpertService.class);
        mockSeegExpert(currentUserService, expertService, isSeegMember);

        DiseaseService diseaseService = mock(DiseaseService.class);
        ModelRunService modelRunService = mock(ModelRunService.class);
        stubLayerRelatedServices(modelRunService, diseaseService);
        when(diseaseService.getDiseaseGroupIdsForAutomaticModelRuns()).thenReturn(Arrays.asList(1));

        AtlasController target = new AtlasController(
                modelRunService, diseaseService, currentUserService, expertService, new AbraidJsonObjectMapper());

        // Act
        Model model = mock(Model.class);
        target.showAtlas(model);

        // Assert
        ArgumentCaptor<String> argumentCaptor = captorForClass(String.class);
        verify(model).addAttribute(eq("layers"), argumentCaptor.capture());
        String value = argumentCaptor.getValue();
        assertThat(value).contains(expectation);
        verify(model).addAttribute("seegMember", isSeegMember);
    }

    private void mockSeegExpert(CurrentUserService currentUserService, ExpertService expertService, boolean isSeegMember) {
        Expert expert = mock(Expert.class);
        when(expert.isSeegMember()).thenReturn(isSeegMember);

        when(currentUserService.getCurrentUserId()).thenReturn(1);
        when(expertService.getExpertById(1)).thenReturn(expert);
    }

    private void stubLayerRelatedServices(ModelRunService modelRunService, DiseaseService diseaseService) {
        ModelRun modelRun1 = mock(ModelRun.class);
        ModelRun modelRun2 = mock(ModelRun.class);
        ModelRun modelRun3 = mock(ModelRun.class);
        DiseaseGroup diseaseGroup1 = mock(DiseaseGroup.class);
        DiseaseGroup diseaseGroup2 = mock(DiseaseGroup.class);

        when(modelRun1.getDiseaseGroup()).thenReturn(diseaseGroup1);
        when(modelRun2.getDiseaseGroup()).thenReturn(diseaseGroup2);
        when(modelRun3.getDiseaseGroup()).thenReturn(diseaseGroup1);
        when(modelRun1.getRequestDate()).thenReturn(new DateTime(2014, 10, 13, 12, 0));
        when(modelRun2.getRequestDate()).thenReturn(new DateTime(1995, 10, 9, 12, 0));
        when(modelRun3.getRequestDate()).thenReturn(new DateTime(2036, 12, 18, 12, 0));
        when(modelRun1.getOccurrenceDataRangeStartDate()).thenReturn(new DateTime(1999, 10, 13, 12, 0));
        when(modelRun2.getOccurrenceDataRangeStartDate()).thenReturn(new DateTime(1998, 10, 9, 12, 0));
        when(modelRun3.getOccurrenceDataRangeStartDate()).thenReturn(new DateTime(1997, 12, 18, 12, 0));
        when(modelRun1.getOccurrenceDataRangeEndDate()).thenReturn(new DateTime(2001, 10, 13, 12, 0));
        when(modelRun2.getOccurrenceDataRangeEndDate()).thenReturn(new DateTime(2002, 10, 9, 12, 0));
        when(modelRun3.getOccurrenceDataRangeEndDate()).thenReturn(new DateTime(2003, 12, 18, 12, 0));
        when(modelRun1.getName()).thenReturn("Model Run 1");
        when(modelRun2.getName()).thenReturn("Model Run 2");
        when(modelRun3.getName()).thenReturn("Model Run 3");
        CovariateInfluence covariateInfluence = mock(CovariateInfluence.class);
        when(covariateInfluence.getMeanInfluence()).thenReturn(20.2);
        CovariateFile covariateFile = mock(CovariateFile.class);
        when(covariateInfluence.getCovariateFile()).thenReturn(covariateFile);
        when(covariateFile.getName()).thenReturn("Name");
        when(modelRun1.getCovariateInfluences()).thenReturn(Arrays.asList(covariateInfluence));
        when(modelRun2.getCovariateInfluences()).thenReturn(new ArrayList<CovariateInfluence>());
        when(modelRun3.getCovariateInfluences()).thenReturn(new ArrayList<CovariateInfluence>());
        when(modelRun1.getSubmodelStatistics()).thenReturn(Arrays.asList(new SubmodelStatistic(0.4, 0.5, 0.6, 0.7, 0.8)));
        when(modelRun2.getSubmodelStatistics()).thenReturn(new ArrayList<SubmodelStatistic>());
        when(modelRun3.getSubmodelStatistics()).thenReturn(new ArrayList<SubmodelStatistic>());

        when(diseaseGroup1.getShortNameForDisplay()).thenReturn("Disease Group 1");
        when(diseaseGroup1.getId()).thenReturn(1);
        when(diseaseGroup2.getShortNameForDisplay()).thenReturn("Disease Group 2");
        when(diseaseGroup2.getId()).thenReturn(2);

        when(modelRunService.getCompletedModelRunsForDisplay()).thenReturn(Arrays.asList(modelRun1, modelRun2, modelRun3));

    }
}
