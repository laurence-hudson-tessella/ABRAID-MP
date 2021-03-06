package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils;

import static org.mockito.Mockito.*;

/**
 * Tests the HandlersAsyncWrapper class.
 * Copyright (c) 2014 University of Oxford
 */
public class HandlersAsyncWrapperTest {
    @Test
    public void handlersAreCalledSuccessfully() throws Exception {
        // Arrange
        BatchingHandler batchingHandler = mock(BatchingHandler.class);
        HandlersAsyncWrapper wrapper = new HandlersAsyncWrapper(batchingHandler);

        ModelRun modelRun = new ModelRun();

        // Act
        wrapper.handle(modelRun).get();

        // Assert
        verify(batchingHandler).handle(same(modelRun));
    }

    @Test
    public void exceptionThrownByAHandlerIsCaught() throws Exception {
        // Arrange
        BatchingHandler batchingHandler = mock(BatchingHandler.class);
        HandlersAsyncWrapper wrapper = new HandlersAsyncWrapper(batchingHandler);
        Logger mockLogger = GeneralTestUtils.createMockLogger(wrapper);

        ModelRun modelRun = new ModelRun();

        RuntimeException exception = new RuntimeException("Test message");
        doThrow(exception).when(batchingHandler).handle(modelRun);

        // Act
        wrapper.handle(modelRun).get();

        // Assert
        verify(mockLogger).error(exception);
    }
}
