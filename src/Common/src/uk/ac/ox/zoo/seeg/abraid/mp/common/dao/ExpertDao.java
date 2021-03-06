package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;

/**
 * Interface for the Expert entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ExpertDao {
    /**
     * Gets all experts.
     * @return A list of all experts.
     */
    List<Expert> getAll();

    /**
     * Gets an expert by its id.
     * @param id The expert's id.
     * @return The expert.
     */
    Expert getById(Integer id);

    /**
     * Gets a page worth of publicly visible experts.
     * @param pageNumber The page number to return.
     * @param pageSize The size of the pages to split the visible experts into.
     * @return A page worth of publicly visible experts
     */
    List<Expert> getPageOfPubliclyVisible(int pageNumber, int pageSize);

    /**
     * Gets a count of the publicly visible experts.
     * @return The count.
     */
    long getCountOfPubliclyVisible();

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    Expert getByEmail(String email);

    /**
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    void save(Expert expert);

}
