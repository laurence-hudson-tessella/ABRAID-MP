package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQLConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * Service interface for model run inputs and outputs.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class ModelRunServiceImpl implements ModelRunService {
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private ModelRunDao modelRunDao;
    private NativeSQL nativeSQL;

    public ModelRunServiceImpl(DiseaseOccurrenceDao diseaseOccurrenceDao, ModelRunDao modelRunDao,
                               NativeSQL nativeSQL) {
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.modelRunDao = modelRunDao;
        this.nativeSQL = nativeSQL;
    }

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @return Disease occurrences for a request to run the model.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
    }

    /**
     * Gets a model run by name.
     * @param name The model run name.
     * @return The model run with the specified name, or null if no model run.
     */
    public ModelRun getModelRunByName(String name) {
        return modelRunDao.getByName(name);
    }

    /**
     * Saves a model run.
     * @param modelRun The model run to save.
     */
    public void saveModelRun(ModelRun modelRun) {
        modelRunDao.save(modelRun);
    }

    /**
     * Gets the mean prediction raster of the specified model run, as a GeoTIFF.
     * @param modelRunId The model run's ID.
     * @return gdalRaster The mean prediction raster, in GeoTIFF format.
     */
    @Override
    public byte[] getMeanPredictionRasterForModelRun(int modelRunId) {
        return nativeSQL.getRasterForModelRun(modelRunId, NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME);
    }

    /**
     * Updates the specified model run to include the specified mean prediction raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The mean prediction raster, in any GDAL format supported by the PostGIS database.
     */
    @Override
    public void updateMeanPredictionRasterForModelRun(int modelRunId, byte[] gdalRaster) {
        nativeSQL.updateRasterForModelRun(modelRunId, gdalRaster,
                NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME);
    }

    /**
     * Updates the specified model run to include the specified prediction uncertainty raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The prediction uncertainty raster, in any GDAL format supported by the PostGIS database.
     */
    @Override
    public void updatePredictionUncertaintyRasterForModelRun(int modelRunId, byte[] gdalRaster) {
        nativeSQL.updateRasterForModelRun(modelRunId, gdalRaster,
                NativeSQLConstants.PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME);
    }

    /**
     * Gets the latest requested model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest requested model run, or null if there are no model runs.
     */
    @Override
    public ModelRun getLastRequestedModelRun(int diseaseGroupId) {
        return modelRunDao.getLastRequestedModelRun(diseaseGroupId);
    }

    /**
     * Gets the latest completed model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getLastCompletedModelRun(int diseaseGroupId) {
        return modelRunDao.getLastCompletedModelRun(diseaseGroupId);
    }

    @Override
    public boolean hasBatchingEverCompleted(int diseaseGroupId) {
        return modelRunDao.hasBatchingEverCompleted(diseaseGroupId);
    }
}
