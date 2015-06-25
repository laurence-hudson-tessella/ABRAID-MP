package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;

/**
 * Helper for the AccountController, separated out into a class to isolate the transaction/exception rollback.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariatesControllerHelper {
    private final CovariateService covariateService;
    private final DiseaseService diseaseService;
    private static final String ERROR_CREATE_SUBDIRECTORY = "Could not create subdirectory for new covariate file";

    @Autowired
    public CovariatesControllerHelper(CovariateService covariateService, DiseaseService diseaseService) {
        this.covariateService = covariateService;
        this.diseaseService = diseaseService;
    }

    public JsonCovariateConfiguration getCovariateConfiguration() throws IOException {
        checkForNewCovariateFilesOnDisk();
        return new JsonCovariateConfiguration(
            convert(diseaseService.getAllDiseaseGroups(), new Converter<DiseaseGroup, JsonModelDisease>() {
                @Override
                public JsonModelDisease convert(DiseaseGroup diseaseGroup) {
                    return new JsonModelDisease(diseaseGroup.getId(), diseaseGroup.getName());
                }
            }),
            convert(covariateService.getAllCovariateFiles(), new Converter<CovariateFile, JsonCovariateFile>() {
                @Override
                public JsonCovariateFile convert(CovariateFile covariateFile) {
                    return new JsonCovariateFile(
                        covariateFile.getFile(),
                        covariateFile.getName(),
                        covariateFile.getInfo(),
                        covariateFile.getHide(),
                        extract(covariateFile.getEnabledDiseaseGroups(), on(DiseaseGroup.class).getId())
                    );
                }
            })
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public  void setCovariateConfiguration(JsonCovariateConfiguration config) throws IOException {
    }

    public void saveNewCovariateFile(String name, String path, MultipartFile file) throws IOException {
        String covariateDirectory = covariateService.getCovariateDirectory();
        writeCovariateFileToDisk(file, path);

        // Add the entry in the covariate config
        addCovariateToDatabase(name, path);
    }


    private void addCovariateToDatabase(String name, String path) throws IOException {
        String relativePath = extractRelativePath(path);
        covariateService.saveCovariateFile(new CovariateFile(
                name,
                relativePath,
                false,
                ""
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkForNewCovariateFilesOnDisk() throws IOException {
        final Path covariateDirectoryPath = Paths.get(covariateService.getCovariateDirectory());
        File covariateDirectory = covariateDirectoryPath.toFile();

        if (covariateDirectory.exists()) {
            Collection<File> files = FileUtils.listFiles(covariateDirectory, null, true);
            Collection<String> paths = convert(files, new Converter<File, String>() {
                public String convert(File file) {
                    Path subPath = covariateDirectoryPath.relativize(file.toPath());
                    return FilenameUtils.separatorsToUnix(subPath.toString());
                }
            });

            Collection<CovariateFile> knownFiles = covariateService.getAllCovariateFiles();
            Collection<String> knownPaths = extract(knownFiles, on(CovariateFile.class).getFile());

            paths.removeAll(knownPaths);

            if (paths.size() != 0) {
                LOGGER.info(String.format(LOG_ADDING_FILES_TO_COVARIATE_CONFIG, paths.size()));
            }

            for (String path : paths) {
                addCovariateToDatabase("", path);
            }
        }
    }

    private void writeCovariateFileToDisk(MultipartFile file, String path) throws IOException {
        // Create directory
        createDirectoryForCovariate(path);

        File serverFile = Paths.get(path).toFile();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(file.getBytes());
        stream.close();
    }

    private void createDirectoryForCovariate(String path) throws IOException {
        File dir = Paths.get(path).getParent().toFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException(ERROR_CREATE_SUBDIRECTORY);
            }
        }
    }

    public String extractTargetPath(String subdirectory, MultipartFile file) {
        String covariateDirectory = covariateService.getCovariateDirectory();
        Path path = Paths.get(covariateDirectory, subdirectory, file.getOriginalFilename()).normalize();
        return FilenameUtils.separatorsToUnix(path.toAbsolutePath().toString());
    }

    private String extractRelativePath(String path) {
        Path parent = Paths.get(covariateService.getCovariateDirectory()).toAbsolutePath();
        Path child = Paths.get(path).toAbsolutePath();
        Path relativePath = parent.relativize(child).normalize();
        return FilenameUtils.separatorsToUnix(relativePath.toString());
    }
}
