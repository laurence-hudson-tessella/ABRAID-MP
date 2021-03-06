package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseOccurrenceDataPoint;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseOccurrenceDataSet;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Web Service for calling out to Machine Learning predictor.
 * Copyright (c) 2014 University of Oxford
 */
public class MachineLearningWebService {
    /** Server response indicating that a trusted prediction was not returned and point should be validated manually. */
    private static final String EXPECTED_PREDICTION_FAILURE_RESPONSE = "No prediction";
    /** URL component for training method name. */
    private static final String TRAIN_METHOD = "/train";
    /** URL component for prediction method name. */
    private static final String PREDICT_METHOD = "/predict";

    private WebServiceClient webServiceClient;
    private AbraidJsonObjectMapper objectMapper;
    private String rootUrl;

    public MachineLearningWebService(WebServiceClient webServiceClient, AbraidJsonObjectMapper objectMapper,
                                     String rootUrl) {
        this.webServiceClient = webServiceClient;
        this.objectMapper = objectMapper;
        this.rootUrl = rootUrl;
    }

    /**
     * Send the data points of one disease group, with which to train the predictor, as Json.
     * @param diseaseGroupId The ID of the disease group the occurrences belong to.
     * @param occurrences The training data points.
     * @throws JsonProcessingException if the JSON is invalid
     * @throws WebServiceClientException if the web service client cannot execute the request
     */
    public void sendTrainingData(int diseaseGroupId, List<DiseaseOccurrence> occurrences)
            throws JsonProcessingException, WebServiceClientException {
        String url = buildUrl(diseaseGroupId, TRAIN_METHOD);
        JsonDiseaseOccurrenceDataSet data = convertToDTO(occurrences);
        String bodyAsJson = writeRequestBodyAsJson(data);
        webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
    }

    /**
     * Find the predicted weighting of the given disease occurrence.
     * @param occurrence The disease occurrence.
     * @return The predicted weighting.
     * @throws JsonProcessingException If the JSON is invalid
     * @throws WebServiceClientException If the web service client fails to execute request
     * @throws NumberFormatException If the response string cannot be parsed as double
     */
    public Double getPrediction(DiseaseOccurrence occurrence)
            throws JsonProcessingException, WebServiceClientException, NumberFormatException {

        Integer diseaseGroupId = getDiseaseGroupId(occurrence);
        if (diseaseGroupId != null) {
            String url = buildUrl(diseaseGroupId, PREDICT_METHOD);
            JsonDiseaseOccurrenceDataPoint data = new JsonDiseaseOccurrenceDataPoint(occurrence);
            String bodyAsJson = writeRequestBodyAsJson(data);
            String response = webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
            if (response.equals(EXPECTED_PREDICTION_FAILURE_RESPONSE)) {
                return null;
            } else {
                return parseDouble(response);
            }
        } else {
            throw new ModelRunWorkflowException("No disease group");
        }
    }

    private String buildUrl(int diseaseGroupId, String action) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(rootUrl)
                .path("/" + Integer.toString(diseaseGroupId))
                .path(action);
        return builder.build().toString();
    }

    private JsonDiseaseOccurrenceDataSet convertToDTO(List<DiseaseOccurrence> occurrences) {
        List<JsonDiseaseOccurrenceDataPoint> data = new ArrayList<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            data.add(new JsonDiseaseOccurrenceDataPoint(occurrence));
        }
        return new JsonDiseaseOccurrenceDataSet(data);
    }

    private Integer getDiseaseGroupId(DiseaseOccurrence occurrence) {
        return (occurrence.getDiseaseGroup() != null) ? occurrence.getDiseaseGroup().getId() : null;
    }

    private String writeRequestBodyAsJson(Object data) throws JsonProcessingException {
        ObjectWriter writer = objectMapper.writer();
        return writer.writeValueAsString(data);
    }
}
