package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

import java.util.List;

/**
 * Interface for the Location entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface LocationDao {
    /**
     * Gets a location by ID.
     * @param id The ID
     * @return The location, or null if it does not exist.
     */
    Location getById(Integer id);

    /**
     * Gets all locations.
     * @return All locations.
     */
    List<Location> getAll();

    /**
     * Gets a location by GeoNames ID.
     * @param geoNameId The GeoNames ID.
     * @return The location, or null if not found.
     */
    Location getByGeoNameId(int geoNameId);

    /**
     * Gets locations by point and precision. This returns a list of locations as there may be several at the same
     * point.
     * @param point The point.
     * @param precision The precision.
     * @return The locations at this point. If none is found, the list is empty.
     */
    List<Location> getByPointAndPrecision(Point point, LocationPrecision precision);

    /**
     * Saves the specified location.
     * @param location The location to save.
     */
    void save(Location location);
}
