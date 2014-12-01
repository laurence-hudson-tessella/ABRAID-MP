package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the WeightingsCalculator class.
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculatorTest {

    private static final int DISEASE_GROUP_ID = 87;
    private static final double EXPERT_WEIGHTING_THRESHOLD = 0.6;

    List<DiseaseOccurrence> occurrences = Arrays.asList(
            new DiseaseOccurrence(0), new DiseaseOccurrence(1), new DiseaseOccurrence(2), new DiseaseOccurrence(3)
    );

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsGetsExpectedListOfReviews() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        WeightingsCalculator target = new WeightingsCalculator(diseaseService, mock(ExpertService.class));

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(DISEASE_GROUP_ID);

        // Assert
        verify(diseaseService).getDiseaseOccurrenceReviewsForUpdatingWeightings(DISEASE_GROUP_ID, EXPERT_WEIGHTING_THRESHOLD);
    }

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsLogsNoReviews() {
        // Arrange
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getDiseaseOccurrenceReviewsForUpdatingWeightings(DISEASE_GROUP_ID, EXPERT_WEIGHTING_THRESHOLD))
            .thenReturn(new ArrayList<DiseaseOccurrenceReview>());

        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class));
        Logger logger = GeneralTestUtils.createMockLogger(target);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(DISEASE_GROUP_ID);

        // Assert
        verify(logger).info(eq("No new occurrence reviews have been submitted by experts with a weighting >= 0.60 - " +
                "expert weightings of disease occurrences will not be updated"));
    }

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsCalculatesWeightingAsAverageResponseForAllOccurrencesInReviewsList() {
        // Arrange
        // NB. An expert's weighting is used when selecting reviews (getDiseaseOccurrenceReviewForUpdatingWeightings
        // returns only the reviews submitted by experts with a weighting >= EXPERT_WEIGHTING_THRESHOLD), but it does
        // not actually feature in occurrence's expertWeighting calculation.
        List<Expert> experts = Arrays.asList(
                new Expert(1.0), new Expert(0.9), new Expert(0.8), new Expert(0.7), new Expert(0.6)
        );

        List<DiseaseOccurrenceReview> reviews = Arrays.asList(
            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(3), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),

            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(1), DiseaseOccurrenceReviewResponse.NO),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(1), DiseaseOccurrenceReviewResponse.NO),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(1), DiseaseOccurrenceReviewResponse.NO),
            new DiseaseOccurrenceReview(experts.get(4), occurrences.get(1), DiseaseOccurrenceReviewResponse.NO),

            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(2), DiseaseOccurrenceReviewResponse.UNSURE),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(2), DiseaseOccurrenceReviewResponse.UNSURE),
            new DiseaseOccurrenceReview(experts.get(3), occurrences.get(2), DiseaseOccurrenceReviewResponse.UNSURE),
            new DiseaseOccurrenceReview(experts.get(4), occurrences.get(2), DiseaseOccurrenceReviewResponse.UNSURE),

            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(3), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(3), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(3), occurrences.get(3), DiseaseOccurrenceReviewResponse.UNSURE),
            new DiseaseOccurrenceReview(experts.get(4), occurrences.get(3), DiseaseOccurrenceReviewResponse.NO)
        );

        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseOccurrenceReviewsForUpdatingWeightings(DISEASE_GROUP_ID, EXPERT_WEIGHTING_THRESHOLD)).thenReturn(reviews);
        WeightingsCalculator target = new WeightingsCalculator(diseaseService, mock(ExpertService.class));

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(DISEASE_GROUP_ID);

        // Assert - Expert weighting is average of review responses.
        for (DiseaseOccurrence occurrence : occurrences) {
            verify(diseaseService).saveDiseaseOccurrence(occurrence);
        }
        assertThat(occurrences.get(0).getExpertWeighting()).isEqualTo(1.0);   // Average of: {1, 1, 1, 1}
        assertThat(occurrences.get(1).getExpertWeighting()).isEqualTo(0.0);   // Average of: {0, 0, 0, 0}
        assertThat(occurrences.get(2).getExpertWeighting()).isEqualTo(0.5);   // Average of: {0.5, 0.5, 0.5, 0.5}
        assertThat(occurrences.get(3).getExpertWeighting()).isEqualTo(0.625); // Average of: {1, 1, 0.5, 0}
    }

    @Test
    public void updateDiseaseOccurrenceValidationWeightingAndFinalWeightingsGetsExpectedListOfOccurrences() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        WeightingsCalculator target = new WeightingsCalculator(diseaseService, mock(ExpertService.class));

        // Act
        target.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(DISEASE_GROUP_ID);

        // Assert
        verify(diseaseService).getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(DISEASE_GROUP_ID);
    }

    @Test
    public void updateDiseaseOccurrenceValidationWeightingAndFinalWeightingsLogsNoOccurrences() {
        // Arrange
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(DISEASE_GROUP_ID))
            .thenReturn(new ArrayList<DiseaseOccurrence>());

        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class));
        Logger logger = GeneralTestUtils.createMockLogger(target);

        // Act
        target.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(DISEASE_GROUP_ID);

        // Assert
        verify(logger).info(eq("No occurrences found that need their validation and final weightings set"));
    }

    @Test
    public void validationWeightingSetAsExpertWeightingOrMachineWeightingAppropriately() {
        // Arrange
        double expertWeighting = 0.2;
        double machineWeighting = 0.4;

        DiseaseOccurrence occurrenceWithExpertWeighting = new DiseaseOccurrence();
        occurrenceWithExpertWeighting.setLocation(locationWithResolutionWeighting(1.0));
        occurrenceWithExpertWeighting.setExpertWeighting(expertWeighting);

        DiseaseOccurrence occurrenceWithMachineWeighting = new DiseaseOccurrence();
        occurrenceWithMachineWeighting.setLocation(locationWithResolutionWeighting(1.0));
        occurrenceWithMachineWeighting.setMachineWeighting(machineWeighting);

        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(DISEASE_GROUP_ID))
            .thenReturn(Arrays.asList(occurrenceWithExpertWeighting, occurrenceWithMachineWeighting));

        WeightingsCalculator target =  new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class));

        // Act
        target.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(DISEASE_GROUP_ID);

        // Assert
        verify(mockDiseaseService).saveDiseaseOccurrence(occurrenceWithExpertWeighting);
        verify(mockDiseaseService).saveDiseaseOccurrence(occurrenceWithMachineWeighting);

        assertThat(occurrenceWithExpertWeighting.getValidationWeighting()).isEqualTo(expertWeighting);
        assertThat(occurrenceWithMachineWeighting.getValidationWeighting()).isEqualTo(machineWeighting);
    }

    @Test
    public void updateDiseaseOccurrenceValidationWeightingAndFinalWeightingsForNullValidationWeighting() {
        // Arrange
        double locationResolutionWeighting = 0.7;

        // Occurrence's expertWeighting and machineWeighting null by default.
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setLocation(locationWithResolutionWeighting(locationResolutionWeighting));

        WeightingsCalculator target = weightingsCalculatorWithMockedDiseaseService(occurrence);

        // Act
        target.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(DISEASE_GROUP_ID);

        // Assert
        assertThat(occurrence.getValidationWeighting()).isNull();
        assertThat(occurrence.getFinalWeighting()).isEqualTo(locationResolutionWeighting);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
    }

    @Test
    public void updateDiseaseOccurrenceValidationWeightingAndFinalWeightingsForValidationWeightingLessThanLowThreshold() {
        // Arrange
        double machineWeighting = 0.1;  // Less than validationWeighting threshold of 0.2
        DiseaseOccurrence occurrence = occurrenceWithExpertWeightingAndMachineWeighting(null, machineWeighting);
        occurrence.setLocation(locationWithResolutionWeighting(0.7));

        WeightingsCalculator target = weightingsCalculatorWithMockedDiseaseService(occurrence);

        // Act
        target.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(DISEASE_GROUP_ID);

        // Assert
        assertThat(occurrence.getValidationWeighting()).isEqualTo(machineWeighting);  // Since expertWeighting is null.
        assertThat(occurrence.getFinalWeighting()).isEqualTo(0.0);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(occurrence.getValidationWeighting());
    }

    @Test
    public void updateDiseaseOccurrenceValidationWeightingAndFinalWeightingsForZeroLocationResolutionWeighting() {
        // Arrange
        double expertWeighting = 0.7;  // Greater than validationWeighting threshold of 0.2
        DiseaseOccurrence occurrence = occurrenceWithExpertWeightingAndMachineWeighting(expertWeighting, null);
        occurrence.setLocation(locationWithResolutionWeighting(0.0));

        WeightingsCalculator target = weightingsCalculatorWithMockedDiseaseService(occurrence);

        // Act
        target.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(DISEASE_GROUP_ID);

        // Assert
        assertThat(occurrence.getValidationWeighting()).isEqualTo(expertWeighting);  // Since machineWeighting is null.
        assertThat(occurrence.getFinalWeighting()).isEqualTo(0.0);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(occurrence.getValidationWeighting());
    }

    @Test
    public void updateDiseaseOccurrenceValidationWeightingAndFinalWeightingsCalculatesAverage() {
        // Arrange
        double machineWeighting = 0.9;
        double locationResolutionWeighting = 0.7;
        DiseaseOccurrence occurrence = occurrenceWithExpertWeightingAndMachineWeighting(null, machineWeighting);
        occurrence.setLocation(locationWithResolutionWeighting(locationResolutionWeighting));

        WeightingsCalculator target = weightingsCalculatorWithMockedDiseaseService(occurrence);

        // Act
        target.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(DISEASE_GROUP_ID);

        // Assert
        double expectedFinalWeighting = (machineWeighting + locationResolutionWeighting) / 2;
        assertThat(occurrence.getValidationWeighting()).isEqualTo(machineWeighting);  // Since expertWeighting is null.
        assertThat(occurrence.getFinalWeighting()).isEqualTo(expectedFinalWeighting);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(occurrence.getValidationWeighting());
    }

    @Test
    public void updateExpertsWeightingsUsesAllReviews() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        WeightingsCalculator target = new WeightingsCalculator(diseaseService, mock(ExpertService.class));

        // Act
        target.updateExpertsWeightings();

        // Assert
        verify(diseaseService).getAllDiseaseOccurrenceReviews();
    }

    @Test
    public void updateExpertsWeightingsLogsNoReviews() {
        // Arrange
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(new ArrayList<DiseaseOccurrenceReview>());

        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class));
        Logger logger = GeneralTestUtils.createMockLogger(target);

        // Act
        target.updateExpertsWeightings();

        // Assert
        verify(logger).info(eq("No occurrence reviews have been submitted - weightings of experts will not be updated"));
    }

    @Test
    public void updateExpertsWeightingsForOneExpertBeingOnlyOneToReviewOccurrenceGivesExpectedResult(){
        // Arrange - Only one expert has reviewed an occurrence so "reviewsOfOccurrence" in calculateDifference is empty
        Expert expert = new Expert();
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(Arrays.asList(
            new DiseaseOccurrenceReview(expert, new DiseaseOccurrence(), DiseaseOccurrenceReviewResponse.YES)
        ));

        WeightingsCalculator target = new WeightingsCalculator(diseaseService, expertService);

        // Act
        target.updateExpertsWeightings();

        // Assert - No other reviews of occurrence, so the difference is 0.0 and the newWeighting = 1.0 - difference
        verify(expertService).saveExpert(expert);
        assertThat(expert.getWeighting()).isEqualTo(1.0);
    }

    @Test
    public void updateExpertsWeightingsSavesExpectedValuesWhenAllExpertsProvideSameResponse() {
        // Arrange
        List<Expert> experts = Arrays.asList(new Expert(0), new Expert(1), new Expert(2));

        List<DiseaseOccurrenceReview> reviews = Arrays.asList(
            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),

            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(1), DiseaseOccurrenceReviewResponse.UNSURE),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(1), DiseaseOccurrenceReviewResponse.UNSURE),

            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(2), DiseaseOccurrenceReviewResponse.NO),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(2), DiseaseOccurrenceReviewResponse.NO)
        );

        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(reviews);

        ExpertService mockExpertService = mock(ExpertService.class);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mockExpertService);

        // Act
        target.updateExpertsWeightings();

        // Assert
        for (Expert expert : experts) {
            verify(mockExpertService).saveExpert(expert);
            assertThat(expert.getWeighting()).isEqualTo(1.0);
        }
    }

    @Test
    public void updateExpertsWeightingsSavesExpectedValues() {
        // Arrange
        List<Expert> experts = Arrays.asList(new Expert(0), new Expert(1), new Expert(2), new Expert(3));

        List<DiseaseOccurrenceReview> reviews = Arrays.asList(
            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(3), occurrences.get(0), DiseaseOccurrenceReviewResponse.YES),

            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(1), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(1), DiseaseOccurrenceReviewResponse.NO),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(1), DiseaseOccurrenceReviewResponse.UNSURE),

            new DiseaseOccurrenceReview(experts.get(0), occurrences.get(2), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(2), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(3), occurrences.get(2), DiseaseOccurrenceReviewResponse.UNSURE),

            new DiseaseOccurrenceReview(experts.get(1), occurrences.get(3), DiseaseOccurrenceReviewResponse.YES),
            new DiseaseOccurrenceReview(experts.get(2), occurrences.get(3), DiseaseOccurrenceReviewResponse.NO),
            new DiseaseOccurrenceReview(experts.get(3), occurrences.get(3), DiseaseOccurrenceReviewResponse.NO)
        );

        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(reviews);

        ExpertService mockExpertService = mock(ExpertService.class);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mockExpertService);

        // Act
        target.updateExpertsWeightings();

        // Assert - Values as calculated in Weights spreadsheet
        for (Expert expert : experts) {
            verify(mockExpertService).saveExpert(expert);
        }
        assertThat(experts.get(0).getWeighting()).isEqualTo(0.667, offset(0.005));
        assertThat(experts.get(1).getWeighting()).isEqualTo(0.417, offset(0.005));
        assertThat(experts.get(2).getWeighting()).isEqualTo(0.813, offset(0.005));
        assertThat(experts.get(3).getWeighting()).isEqualTo(0.667, offset(0.005));
    }

    @Test
    public void averageReturnsExpectedValue() {
        // Act
        double result = WeightingsCalculator.average(0.1, 0.2, 0.3);

        // Assert
        assertThat(result).isEqualTo(0.2, offset(0.0001));
    }

    @Test
    public void averageReturnsExpectedValueDiscountingNullValue() {
        // Act
        double result = WeightingsCalculator.average(0.1, 0.2, null);

        // Assert
        assertThat(result).isEqualTo(0.15, offset(0.0001));
    }


    @Test
    public void averageReturnsExpectedValueDiscountingAllNulls() {
        // Act
        double result = WeightingsCalculator.average(null, null, null);

        // Assert
        assertThat(result).isEqualTo(0.0);
    }

    private DiseaseOccurrence occurrenceWithExpertWeightingAndMachineWeighting(Double expertWeighting, Double machineWeighting) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setExpertWeighting(expertWeighting);
        occurrence.setMachineWeighting(machineWeighting);
        return occurrence;
    }

    private Location locationWithResolutionWeighting(double locationResolutionWeighting) {
        Location location = new Location();
        location.setResolutionWeighting(locationResolutionWeighting);
        return location;
    }

    private WeightingsCalculator weightingsCalculatorWithMockedDiseaseService(DiseaseOccurrence occurrence) {
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(DISEASE_GROUP_ID))
            .thenReturn(Arrays.asList(occurrence));

        return new WeightingsCalculator(diseaseService, mock(ExpertService.class));
    }
}
