package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQLConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the ModelRunService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunServiceTest extends AbstractCommonSpringUnitTests {
    @Autowired
    private ModelRunService modelRunService;

    @Test
    public void getDiseaseOccurrencesForModelRunRequest() {
        // Arrange
        int diseaseGroupId = 87;
        List<DiseaseOccurrence> occurrences = Arrays.asList(new DiseaseOccurrence());
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId)).thenReturn(occurrences);

        // Act
        List<DiseaseOccurrence> testOccurrences = modelRunService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);

        // Assert
        assertThat(testOccurrences).isSameAs(occurrences);
    }

    @Test
    public void getModelRunByName() {
        // Arrange
        String name = "test";
        ModelRun expectedRun = new ModelRun();
        when(modelRunDao.getByName(name)).thenReturn(expectedRun);

        // Act
        ModelRun actualRun = modelRunService.getModelRunByName(name);

        // Assert
        assertThat(actualRun).isSameAs(expectedRun);
    }

    @Test
    public void updateMeanPredictionRasterForModelRun() {
        // Arrange
        int modelRunId = 1;
        byte[] raster = new byte[1];

        // Act
        modelRunService.updateMeanPredictionRasterForModelRun(modelRunId, raster);

        // Assert
        verify(nativeSQL).updateRasterForModelRun(eq(modelRunId), eq(raster),
                eq(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME));
    }

    @Test
    public void updatePredictionUncertaintyRasterForModelRun() {
        // Arrange
        int modelRunId = 1;
        byte[] raster = new byte[1];

        // Act
        modelRunService.updatePredictionUncertaintyRasterForModelRun(modelRunId, raster);

        // Assert
        verify(nativeSQL).updateRasterForModelRun(eq(modelRunId), eq(raster),
                eq(NativeSQLConstants.PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME));
    }

    @Test
    public void saveModelRun() {
        // Arrange
        ModelRun run = new ModelRun();

        // Act
        modelRunService.saveModelRun(run);

        // Assert
        verify(modelRunDao).save(eq(run));
    }
}