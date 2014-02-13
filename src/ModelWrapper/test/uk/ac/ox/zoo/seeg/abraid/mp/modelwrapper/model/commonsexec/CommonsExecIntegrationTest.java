package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.OS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.omg.PortableInterceptor.SUCCESSFUL;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunnerImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.WorkspaceProvisioner;

import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.googlecode.catchexception.CatchException.catchException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the commons exec based R script runner.
 * Created by zool1112 on 13/02/14.
 */
public class CommonsExecIntegrationTest {
    private int SUCCESSFUL = 0;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    /**
     * Verifies that subprocesses can be started
     */
    @Test
    public void shouldBeAbleToRunEmptyScript() throws Exception {
        // Given
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 1000);
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[] {""});
            }
        });

        // When
        ProcessHandler processHandler = runner.runModel(config);
        int exitCode = processHandler.waitForCompletion();

        // Then
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
    }

    /**
     * Verifies that stdout is correctly wired
     */
    @Test
    public void shouldBeAbleToRunHelloWorldScript() throws Exception {
        // Given
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 1000);
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[] {"cat('Hello, world!\n')"});
            }
        });

        // When
        ProcessHandler processHandler = runner.runModel(config);
        int exitCode = processHandler.waitForCompletion();
        String result = processHandler.getOutputStream().toString().split(System.lineSeparator())[2];

        // Then
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
        assertThat(result).isEqualTo("Hello, world!");
    }

    /**
     * Verifies that stderr is correctly wired
     */
    @Test
    public void shouldBeAbleToRunHelloErrorScript() throws Exception {
        // Given
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 1000);
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[] {"write('Hello, world!\n', stderr())"});
            }
        });

        // When
        ProcessHandler processHandler = runner.runModel(config);
        int exitCode = processHandler.waitForCompletion();
        String result = processHandler.getErrorStream().toString().split(System.lineSeparator())[0];

        // Then
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
        assertThat(result).isEqualTo("Hello, world!");
    }

    /**
     * Verifies that stdin is correctly wired
     */
    @Test
    public void shouldBeAbleToRunHelloNameScript() throws Exception {
        // Given
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 1000);
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[] {
                        "name <- readLines(file(\"stdin\"),1)",
                        "cat('Hello, ', name, '!\n', sep='')"});
            }
        });
        String expectedName = "Bob";
        PipedOutputStream writer = new PipedOutputStream();

        // When
        ProcessHandler processHandler = runner.runModel(config);
        processHandler.getInputStream().connect(writer);
        writer.write(expectedName.getBytes());
        writer.flush();
        writer.close();
        int exitCode = processHandler.waitForCompletion();
        String result = processHandler.getOutputStream().toString().split(System.lineSeparator())[3];

        // Then
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
        assertThat(result).isEqualTo("Hello, "+expectedName+"!");
    }

    /**
     * Find the R executable. This is not very robust and should be reconsidered at some point.
     * @return The R executable
     */
    private static File findR() {
        if (OS.isFamilyWindows()) {
            return new File("C:\\Program Files\\R\\R-3.0.2\\bin\\x64\\R.exe");
        } else {
            return new File("/usr/bin/R");
        }
    }

    private static File createScript(TemporaryFolder baseDir, String[] lines) throws IOException {
        File directory = baseDir.newFolder();
        File script = Files.createFile(Paths.get(directory.getPath(), "script.R")).toFile();
        PrintWriter writer = new PrintWriter(script);
        for(String line : lines) {
            writer.println(line);
        }
        writer.close();
        return script;
    }
}
