package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnit;

import java.util.List;

/**
 * Interface for the AdminUnit entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitDao {
    /**
     * Gets all administrative units.
     * @return A list of all administrative units.
     */
    List<AdminUnit> getAll();
}