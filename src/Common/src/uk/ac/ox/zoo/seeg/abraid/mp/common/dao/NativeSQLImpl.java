package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import static uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQLConstants.*;

/**
 * Contains routines that interact with the PostGIS database using native SQL.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLImpl implements NativeSQL {
    private SessionFactory sessionFactory;

    public NativeSQLImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Finds the first admin unit that contains the specified point.
     * @param point The point.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    @Override
    public Integer findAdminUnitThatContainsPoint(Point point, boolean isGlobal) {
        String query = String.format(ADMIN_UNIT_CONTAINS_POINT_QUERY, getGlobalOrTropical(isGlobal));
        return (Integer) uniqueResult(query, "point", point);
    }

    /**
     * Finds the country that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the country that contains the specified point.
     */
    @Override
    public Integer findCountryThatContainsPoint(Point point) {
        return (Integer) uniqueResult(COUNTRY_CONTAINS_POINT_QUERY, "point", point);
    }

    /**
     * Determines whether one of the land-sea border geometries contains the point.
     * @param point The point.
     * @return True if the point is on land, otherwise false.
     */
    @Override
    public boolean doesLandSeaBorderContainPoint(Point point) {
        Object id = uniqueResult(LAND_SEA_BORDER_CONTAINS_POINT_QUERY, "point", point);
        return (id != null);
    }

    /**
     * Updates the disease_extent table for the specified disease. This is done by using the
     * admin_unit_disease_extent_class table to aggregate the relevant geometries in the admin_unit_global/tropical
     * table.
     * @param diseaseGroupId The disease group ID.
     * @param isGlobal True if the disease is global, false if tropical.
     */
    @Override
    public void updateAggregatedDiseaseExtent(int diseaseGroupId, boolean isGlobal) {
        // Flush the session to ensure that the latest admin_unit_disease_extent_class entries are available
        sessionFactory.getCurrentSession().flush();

        String updateQuery = String.format(UPDATE_DISEASE_EXTENT_QUERY, getGlobalOrTropical(isGlobal));
        executeUpdate(updateQuery, "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Calculates the distance between the specified location and the boundaries of the disease extent of the specified
     * disease group. If the specified point is within the disease extent, it returns zero.
     * @param diseaseGroupId The ID of the disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param locationId The location.
     * @return The distance outside the disease extent, or 0.
     */
    @Override
    public Double findDistanceOutsideDiseaseExtent(int diseaseGroupId, boolean isGlobal, int locationId) {
        String query = String.format(DISTANCE_OUTSIDE_DISEASE_EXTENT, getGlobalOrTropical(isGlobal));
        return (Double) uniqueResult(query, "diseaseGroupId", diseaseGroupId, "locationId", locationId);
    }

    /**
     * Calculates the distance between the specified location and the boundaries of the nearest admin unit not in the
     * disease extent of the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param locationId The location.
     * @return The distance inside the disease extent, or 0.
     */
    @Override
    public Double findDistanceInsideDiseaseExtent(int diseaseGroupId, boolean isGlobal, int locationId) {
        String query = String.format(DISTANCE_INSIDE_DISEASE_EXTENT, getGlobalOrTropical(isGlobal));
        return (Double) uniqueResult(query, "diseaseGroupId", diseaseGroupId, "locationId", locationId);
    }

    private SQLQuery createSQLQuery(String queryString) {
        return sessionFactory.getCurrentSession().createSQLQuery(queryString);
    }

    private Object uniqueResult(String queryString, Object... parameterNamesAndValues) {
        SQLQuery parameterisedSQLQuery = getParameterisedSQLQuery(queryString, parameterNamesAndValues);
        return parameterisedSQLQuery.uniqueResult();
    }

    private Object executeUpdate(String queryString, Object... parameterNamesAndValues) {
        return getParameterisedSQLQuery(queryString, parameterNamesAndValues).executeUpdate();
    }

    private SQLQuery getParameterisedSQLQuery(String queryString, Object... parameterNamesAndValues) {
        SQLQuery query = createSQLQuery(queryString);
        for (int i = 0; i < parameterNamesAndValues.length; i += 2) {
            query.setParameter((String) parameterNamesAndValues[i], parameterNamesAndValues[i + 1]);
        }
        return query;
    }

    private String getGlobalOrTropical(boolean isGlobal) {
        return isGlobal ? GLOBAL : TROPICAL;
    }
}
