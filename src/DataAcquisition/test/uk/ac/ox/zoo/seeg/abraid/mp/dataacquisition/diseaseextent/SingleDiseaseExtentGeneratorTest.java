package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Tests the SingleDiseaseExtentGenerator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class SingleDiseaseExtentGeneratorTest {
    private SingleDiseaseExtentGenerator singleDiseaseExtentGenerator;
    private DiseaseService diseaseService = mock(DiseaseService.class);

    private DiseaseExtentClass presenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.PRESENCE);
    private DiseaseExtentClass possiblePresenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE);
    private DiseaseExtentClass uncertainDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.UNCERTAIN);

    @Before
    public void setUp() {
        singleDiseaseExtentGenerator = new SingleDiseaseExtentGenerator(diseaseService);
        mockGetDiseaseExtentClass(presenceDiseaseExtentClass);
        mockGetDiseaseExtentClass(possiblePresenceDiseaseExtentClass);
        mockGetDiseaseExtentClass(uncertainDiseaseExtentClass);
    }

    @Test
    public void generateDiseaseExtentDoesNothingIfExtentAlreadyExists() {
        // Arrange
        int diseaseGroupId = 87;
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass();
        mockGetExistingDiseaseExtent(diseaseGroupId, Arrays.asList(adminUnitDiseaseExtentClass));
        DiseaseExtentParameters diseaseExtentParameters = new DiseaseExtentParameters(null, 1, 0.2, 5, 1);

        // Act
        singleDiseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, diseaseExtentParameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(0);
        expectSaveAdminUnitDiseaseExtentClass(0);
    }

    @Test
    public void generateDiseaseExtentDoesNothingIfNoRelevantOccurrencesExist() {
        // Arrange
        int diseaseGroupId = 87;
        int maximumYearsAgo = 1;
        double minimumValidationWeighting = 0.2;
        int minimumOccurrencesForPresence = 5;
        int minimumOccurrencesForPossiblePresence = 1;
        List<Integer> feedIds = new ArrayList<>();

        DiseaseExtentParameters diseaseExtentParameters = new DiseaseExtentParameters(feedIds, maximumYearsAgo,
                minimumValidationWeighting, minimumOccurrencesForPresence, minimumOccurrencesForPossiblePresence);
        mockGetExistingDiseaseExtent(diseaseGroupId, new ArrayList<AdminUnitDiseaseExtentClass>());
        mockGetDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, minimumValidationWeighting,
                getFixedYearsAgo(maximumYearsAgo), feedIds, new ArrayList<DiseaseOccurrenceForDiseaseExtent>());

        // Act
        singleDiseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, diseaseExtentParameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectSaveAdminUnitDiseaseExtentClass(0);
    }

    @Test
    public void generateDiseaseExtentForTypicalCase() {
        int diseaseGroupId = 87;
        int maximumYearsAgo = 1;
        double minimumValidationWeighting = 0.2;
        int minimumOccurrencesForPresence = 5;
        int minimumOccurrencesForPossiblePresence = 1;
        List<Integer> feedIds = new ArrayList<>();
        List<? extends AdminUnitGlobalOrTropical> adminUnits = getTypicalAdminUnits();
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        DiseaseExtentParameters diseaseExtentParameters = new DiseaseExtentParameters(feedIds, maximumYearsAgo,
                minimumValidationWeighting, minimumOccurrencesForPresence, minimumOccurrencesForPossiblePresence);
        mockGetExistingDiseaseExtent(diseaseGroupId, new ArrayList<AdminUnitDiseaseExtentClass>());
        mockGetDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, minimumValidationWeighting,
                getFixedYearsAgo(maximumYearsAgo), feedIds, getTypicalOccurrences());
        mockGetAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId, adminUnits);
        mockGetDiseaseGroupById(diseaseGroupId, diseaseGroup);

        // Act
        singleDiseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, diseaseExtentParameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectSaveAdminUnitDiseaseExtentClass(6);
        expectSaveAdminUnitDiseaseExtentClass(new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(adminUnits, 100),
                diseaseGroup, uncertainDiseaseExtentClass, 0));
        expectSaveAdminUnitDiseaseExtentClass(new AdminUnitDiseaseExtentClass(getAdminUnitTropical(adminUnits, 125),
                diseaseGroup, uncertainDiseaseExtentClass, 0));
        expectSaveAdminUnitDiseaseExtentClass(new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(adminUnits, 150),
                diseaseGroup, possiblePresenceDiseaseExtentClass, 1));
        expectSaveAdminUnitDiseaseExtentClass(new AdminUnitDiseaseExtentClass(getAdminUnitTropical(adminUnits, 200),
                diseaseGroup, possiblePresenceDiseaseExtentClass, 4));
        expectSaveAdminUnitDiseaseExtentClass(new AdminUnitDiseaseExtentClass(getAdminUnitTropical(adminUnits, 250),
                diseaseGroup, presenceDiseaseExtentClass, 5));
        expectSaveAdminUnitDiseaseExtentClass(new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(adminUnits, 300),
                diseaseGroup, presenceDiseaseExtentClass, 10));
    }

    private void mockGetExistingDiseaseExtent(int diseaseGroupId, List<AdminUnitDiseaseExtentClass> diseaseExtent) {
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId)).thenReturn(diseaseExtent);
    }

    private void mockGetDiseaseOccurrencesForDiseaseExtent(Integer diseaseGroupId, Double minimumValidationWeighting,
                                                           DateTime minimumOccurrenceDate, List<Integer> feedIds,
                                                           List<DiseaseOccurrenceForDiseaseExtent> occurrences) {
        when(diseaseService.getDiseaseOccurrencesForDiseaseExtent(eq(diseaseGroupId), eq(minimumValidationWeighting),
                eq(minimumOccurrenceDate), same(feedIds))).thenReturn(occurrences);
    }

    private void mockGetAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(int diseaseGroupId,
                                                                        List<? extends AdminUnitGlobalOrTropical> adminUnits) {
        when(diseaseService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId))
                .thenAnswer(convertToAnswer(adminUnits));
    }

    private void mockGetDiseaseGroupById(int diseaseGroupId, DiseaseGroup diseaseGroup) {
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
    }

    private void mockGetDiseaseExtentClass(DiseaseExtentClass diseaseExtentClass) {
        when(diseaseService.getDiseaseExtentClass(diseaseExtentClass.getName())).thenReturn(diseaseExtentClass);
    }

    private void expectGetDiseaseOccurrencesForDiseaseExtent(int times) {
        verify(diseaseService, times(times)).getDiseaseOccurrencesForDiseaseExtent(anyInt(), anyDouble(),
                any(DateTime.class), anyListOf(Integer.class));
    }

    private void expectSaveAdminUnitDiseaseExtentClass(int times) {
        verify(diseaseService, times(times)).saveAdminUnitDiseaseExtentClass(any(AdminUnitDiseaseExtentClass.class));
    }

    private void expectSaveAdminUnitDiseaseExtentClass(AdminUnitDiseaseExtentClass extentClass) {
        verify(diseaseService, times(1)).saveAdminUnitDiseaseExtentClass(eq(extentClass));
    }

    private List<DiseaseOccurrenceForDiseaseExtent> getTypicalOccurrences() {
        // 0 occurrences of global GAUL code 100
        // 0 occurrences of tropical GAUL code 125
        // 1 occurrence of global GAUL code 150
        // 4 occurrences of tropical GAUL code 200
        // 5 occurrences of tropical GAUL code 250
        // 10 occurrences of global GAUL code 300
        return randomise(concatenate(
                createOccurrences(150, null, 1),
                createOccurrences(null, 200, 4),
                createOccurrences(null, 250, 5),
                createOccurrences(300, null, 10)));
    }

    private List<? extends AdminUnitGlobalOrTropical> getTypicalAdminUnits() {
        return Arrays.asList(
                new AdminUnitGlobal(100),
                new AdminUnitGlobal(150),
                new AdminUnitGlobal(300),
                new AdminUnitTropical(125),
                new AdminUnitTropical(200),
                new AdminUnitTropical(250)
        );
    }

    private <A extends AdminUnitGlobalOrTropical> Answer<List<A>> convertToAnswer(final List<A> values) {
        // This is necessary to support types with bounded wildcards
        return new Answer<List<A>>() {
            public List<A> answer(InvocationOnMock invocation) throws Throwable {
                return values;
            }
        };
    }


    private DateTime getFixedYearsAgo(int yearsAgo) {
        int year = 2014;
        int month = 5;
        int day = 6;
        long fixedNow = new DateTime(year, month, day, 0, 0, 0).getMillis();
        DateTimeUtils.setCurrentMillisFixed(fixedNow);
        return new DateTime(year - yearsAgo, month, day, 0, 0, 0);
    }

    private List<DiseaseOccurrenceForDiseaseExtent> createOccurrences(Integer adminUnitGlobalGaulCode,
                                                                      Integer adminUnitTropicalGaulCode,
                                                                      int numberOfTimes) {
        // The occurrence date adn weighting isn't used in the current disease extent calculations, so just supply the
        // same one
        DateTime occurrenceDate = new DateTime("2014-01-01");
        double systemWeighting = 0.7;
        List<DiseaseOccurrenceForDiseaseExtent> occurrences = new ArrayList<>();
        for (int i = 0; i < numberOfTimes; i++) {
            occurrences.add(new DiseaseOccurrenceForDiseaseExtent(occurrenceDate, systemWeighting,
                    adminUnitGlobalGaulCode, adminUnitTropicalGaulCode));
        }
        return occurrences;
    }

    private AdminUnitGlobal getAdminUnitGlobal(List<? extends AdminUnitGlobalOrTropical> adminUnits, int gaulCode) {
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            if (adminUnit instanceof AdminUnitGlobal && adminUnit.getGaulCode() == gaulCode) {
                return (AdminUnitGlobal) adminUnit;
            }
        }
        return null;
    }

    private AdminUnitTropical getAdminUnitTropical(List<? extends AdminUnitGlobalOrTropical> adminUnits, int gaulCode) {
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            if (adminUnit instanceof AdminUnitTropical && adminUnit.getGaulCode() == gaulCode) {
                return (AdminUnitTropical) adminUnit;
            }
        }
        return null;
    }

    private <T> List<T> concatenate(List<T>... inputLists) {
        List<T> outputList = new ArrayList<T>();
        for (List<T> inputList : inputLists) {
            outputList.addAll(inputList);
        }
        return outputList;
    }

    private <T> List<T> randomise(List<T> list) {
        Collections.shuffle(list, new Random(System.nanoTime()));
        return list;
    }
}
