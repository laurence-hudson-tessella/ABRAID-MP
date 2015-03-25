package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeometryServiceTest {
    private GeometryService geometryService;
    private CountryDao countryDao;
    private HealthMapCountryDao healthMapCountryDao;
    private AdminUnitQCDao adminUnitQCDao;
    private NativeSQL nativeSQL;
    private LandSeaBorderDao landSeaBorderDao;

    @Before
    public void setUp() {
        countryDao = mock(CountryDao.class);
        healthMapCountryDao = mock(HealthMapCountryDao.class);
        adminUnitQCDao = mock(AdminUnitQCDao.class);
        nativeSQL = mock(NativeSQL.class);
        landSeaBorderDao = mock(LandSeaBorderDao.class);
        geometryService = new GeometryServiceImpl(countryDao, healthMapCountryDao, adminUnitQCDao, nativeSQL, landSeaBorderDao);
    }

    @Test
    public void getAllCountries() {
        // Arrange
        List<Country> countries = Arrays.asList(new Country());
        when(countryDao.getAll()).thenReturn(countries);

        // Act
        List<Country> testCountries = geometryService.getAllCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getAllHealthMapCountries() {
        // Arrange
        List<HealthMapCountry> countries = Arrays.asList(new HealthMapCountry());
        when(healthMapCountryDao.getAll()).thenReturn(countries);

        // Act
        List<HealthMapCountry> testCountries = geometryService.getAllHealthMapCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getAllAdminUnits() {
        // Arrange
        List<AdminUnitQC> adminUnits = Arrays.asList(new AdminUnitQC());
        when(adminUnitQCDao.getAll()).thenReturn(adminUnits);

        // Act
        List<AdminUnitQC> testAdminUnits = geometryService.getAllAdminUnitQCs();

        // Assert
        assertThat(testAdminUnits).isSameAs(adminUnits);
    }

    @Test
    public void getAllLandSeaBorders() {
        // Arrange
        List<LandSeaBorder> landSeaBorders = Arrays.asList(new LandSeaBorder());
        when(landSeaBorderDao.getAll()).thenReturn(landSeaBorders);

        // Act
        List<LandSeaBorder> testLandSeaBorders = geometryService.getAllLandSeaBorders();

        // Assert
        assertThat(testLandSeaBorders).isSameAs(landSeaBorders);
    }

    @Test
    public void findAdminUnitGlobalThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findAdminUnitThatContainsPoint(point, true)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = geometryService.findAdminUnitGlobalThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findAdminUnitThatContainsPoint(point, false)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = geometryService.findAdminUnitTropicalThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void findCountryThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findCountryThatContainsPoint(point)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = geometryService.findCountryThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void doesLandSeaBorderContainPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        when(nativeSQL.doesLandSeaBorderContainPoint(point)).thenReturn(true);

        // Act
        boolean result = geometryService.doesLandSeaBorderContainPoint(point);

        // Assert
        assertThat(result).isTrue();
    }
}
