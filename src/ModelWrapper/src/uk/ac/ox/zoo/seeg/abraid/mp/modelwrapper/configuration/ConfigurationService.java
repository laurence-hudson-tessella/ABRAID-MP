package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

/**
 * Service interface for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public interface ConfigurationService {
    /**
     * Updates the current modelwrapper authentication details.
     * @param username The new username.
     * @param passwordHash The bcrypt hash of the new password.
     */
    void setAuthenticationDetails(String username, String passwordHash);

    /**
     * Gets the current modelwrapper authentication username.
     * @return The username.
     */
    String getAuthenticationUsername();

    /**
     * Gets the current modelwrapper authentication password hash.
     * @return The password hash.
     */
    String getAuthenticationPasswordHash();

    /**
     * Get the current remote repository url to use as a source for the model.
     * @return The repository url.
     */
    String getModelRepositoryUrl();

    /**
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    String getCacheDirectory();
}
