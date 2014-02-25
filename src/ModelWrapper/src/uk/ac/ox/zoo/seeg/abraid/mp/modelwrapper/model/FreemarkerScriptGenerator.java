package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A freemarker based ScriptGenerator to generate model run scripts based on a template file.
 * Copyright (c) 2014 University of Oxford
 */
public class FreemarkerScriptGenerator implements ScriptGenerator {
    private static final String SCRIPT_FILE_NAME = "modelRun.R";
    private static final String TEMPLATE_FILE_NAME = "ModelRunTemplate.ftl";
    private static final String ASCII = "US-ASCII";

    /**
     * Creates a model run script file in the working directory for the given configuration.
     * @param runConfiguration The model run configuration.
     * @param workingDirectory The directory in which the script should be created.
     * @param dryRun Indicates whether the full model should run.
     * @return The script file.
     * @throws IOException Thrown in response to issues creating the script file.
     */
    @Override
    public File generateScript(RunConfiguration runConfiguration, File workingDirectory, boolean dryRun)
            throws IOException {
        //Load template from source folder
        Template template = loadTemplate();

        // Build the data-model
        Map<String, Object> data = buildDataModel(runConfiguration, dryRun);

        // File output
        File scriptFile = applyTemplate(workingDirectory, template, data);

        return scriptFile;
    }

    private static File applyTemplate(File workingDirectory, Template template, Map<String, Object> data)
            throws IOException {
        File scriptFile = Paths.get(workingDirectory.getAbsolutePath(), SCRIPT_FILE_NAME).toFile();
        Writer fileWriter = null;
        try {
            fileWriter = new OutputStreamWriter(new FileOutputStream(scriptFile), Charset.forName(ASCII).newEncoder());
            template.process(data, fileWriter);
            fileWriter.flush();
        } catch (TemplateException e) {
            throw new IOException("Either could not read the template file or the file was invalid.", e);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
        return scriptFile;
    }

    private Template loadTemplate() throws IOException {
        //Freemarker configuration object
        Configuration config = new Configuration();

        config.setClassForTemplateLoading(this.getClass(), "");
        return config.getTemplate(TEMPLATE_FILE_NAME);
    }

    private static Map<String, Object> buildDataModel(RunConfiguration runConfiguration, boolean dryRun) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("run", runConfiguration.getRunName());
        data.put("dry_run", dryRun);
        data.put("verbosity", 0);
        data.put("disease", "P.vivax");
        data.put("model_version", "bbc934aefeabbd5579f65973a5aa90e180145176");
        data.put("outbreak_file", "outbreakData.csv");
        data.put("extent_file", "extentData.csv");
        data.put("covariants", new String[] {"file1.csv", "file2.csv"});
        return data;
    }
}