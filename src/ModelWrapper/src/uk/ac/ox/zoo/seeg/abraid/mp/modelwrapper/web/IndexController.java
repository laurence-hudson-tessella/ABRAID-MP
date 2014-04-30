package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.SourceCodeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller for the ModelWrapper Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class IndexController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(IndexController.class);
    private static final String LOG_FAILED_TO_GET_REPOSITORY_VERSIONS = "Failed to get repository versions.";

    private static final Pattern USERNAME_REGEX = Pattern.compile("^[a-z0-9_-]{3,15}$");

    // Regex from http://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=^[^\\s]{6,128}$)((?=.*?\\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[^\\w\\d\\s])(?=.*?[a-z])|(?=.*?[^\\w\\d\\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[A-Z])(?=.*?[^\\w\\d\\s]))^.*$"); ///CHECKSTYLE:SUPPRESS LineLengthCheck

    private final ConfigurationService configurationService;
    private final SourceCodeManager sourceCodeManager;

    @Autowired
    public IndexController(ConfigurationService configurationService, SourceCodeManager sourceCodeManager) {
        this.configurationService = configurationService;
        this.sourceCodeManager = sourceCodeManager;
    }

    /**
     * TEMP.
     * @param model Temp.
     * @return Temp.
     */
    @RequestMapping(value = "/covariates", method = RequestMethod.GET)
    public String showCovariatesPage(Model model) {
        return "covariates";
    }

    /**
     * Request map for the index page.
     * @param model The ftl data model.
     * @return The ftl index page name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndexPage(Model model) {
        List<String> modelVersions;

        try {
            modelVersions = sourceCodeManager.getAvailableVersions();
        } catch (Exception e) {
            LOGGER.error(LOG_FAILED_TO_GET_REPOSITORY_VERSIONS, e);
            modelVersions = new ArrayList<>();
        }

        String rPath = null;
        try {
            rPath = configurationService.getRExecutablePath();
        } catch (ConfigurationException e) {
            rPath = "";
        }

        model.addAttribute("repository_url", configurationService.getModelRepositoryUrl());
        model.addAttribute("model_version", configurationService.getModelRepositoryVersion());
        model.addAttribute("available_versions", modelVersions);
        model.addAttribute("run_duration", configurationService.getMaxModelRunDuration());
        model.addAttribute("r_path", rPath);
        model.addAttribute("covariate_directory", configurationService.getCovariateDirectory());

        return "index";
    }

    /**
     * Updates the modelwrapper authentication details.
     * @param username The new username.
     * @param password The new password.
     * @param passwordConfirmation Confirmation of the new password.
     * @return 204 for success, 400 for failure.
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity updateAuthenticationDetails(String username, String password, String passwordConfirmation) {
        boolean validRequest =
            !StringUtils.isEmpty(username) && USERNAME_REGEX.matcher(username).matches() &&
            !StringUtils.isEmpty(password) && PASSWORD_REGEX.matcher(password).matches() &&
            password.equals(passwordConfirmation);

        if (validRequest) {
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            configurationService.setAuthenticationDetails(username, passwordHash);

            // Respond with a 204, this is equivalent to a 200 (OK) but without any content.
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

}
