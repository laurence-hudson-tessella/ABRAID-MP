package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the ProcessException class.
 * Copyright (c) 2014 University of Oxford
 */
public class ProcessExceptionTest {
    @Test
    public void processExceptionAssignsCauseCorrectly() throws Exception {
        // Arrange
        Throwable expectation = new Throwable();

        // Act
        ProcessException result = new ProcessException(expectation);

        // Assert
        assertThat(result.getCause()).isEqualTo(expectation);
    }
}
