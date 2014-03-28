package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;

import java.util.List;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the HealthMapCountryDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapCountryDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private HealthMapCountryDao healthMapCountryDao;

    @Test
    public void getAllDiseaseGroups() {
        List<HealthMapCountry> countries = healthMapCountryDao.getAll();
        assertThat(countries).hasSize(224);
    }

    @Test
    public void getHealthMapCountryWithNoAssociatedSEEGCountries() {
        // Arrange
        int id = 143;
        String healthMapCountryName = "Maldives";

        // Act
        HealthMapCountry healthMapCountry = healthMapCountryDao.getByName(healthMapCountryName);

        // Assert
        assertThat(healthMapCountry).isNotNull();
        assertThat(healthMapCountry.getId()).isEqualTo(id);
        assertThat(healthMapCountry.getName()).isEqualTo(healthMapCountryName);
        assertThat(healthMapCountry.getCountries()).isNotNull();
        assertThat(healthMapCountry.getCountries()).isEmpty();
    }

    @Test
    public void getHealthMapCountryWithOneAssociatedSEEGCountry() {
        // Arrange
        int id = 28;
        String healthMapCountryName = "Trinidad & Tobago";

        // Act
        HealthMapCountry healthMapCountry = healthMapCountryDao.getByName(healthMapCountryName);

        // Assert
        assertThat(healthMapCountry).isNotNull();
        assertThat(healthMapCountry.getId()).isEqualTo(id);
        assertThat(healthMapCountry.getName()).isEqualTo(healthMapCountryName);
        assertThat(healthMapCountry.getCountries()).isNotNull();
        assertThat(healthMapCountry.getCountries()).hasSize(1);
        for (Country country : healthMapCountry.getCountries()) {
            assertThat(country.getGaulCode()).isEqualTo(246);
            assertThat(country.getName()).isEqualTo("Trinidad and Tobago");
        }
    }

    @Test
    public void getHealthMapCountryWithTwoAssociatedSEEGCountries() {
        // Arrange
        int id = 107;
        String healthMapCountryName = "Norway";

        // Act
        HealthMapCountry healthMapCountry = healthMapCountryDao.getByName(healthMapCountryName);

        // Assert
        assertThat(healthMapCountry).isNotNull();
        assertThat(healthMapCountry.getId()).isEqualTo(id);
        assertThat(healthMapCountry.getName()).isEqualTo(healthMapCountryName);
        Set<Country> countries = healthMapCountry.getCountries();
        assertThat(countries).isNotNull();
        assertThat(countries).hasSize(2);

        Country country1 = findCountryByGaulCode(countries, 234);
        assertThat(country1).isNotNull();
        assertThat(country1.getName()).isEqualTo("Svalbard and Jan Mayen Islands");

        Country country2 = findCountryByGaulCode(countries, 186);
        assertThat(country2).isNotNull();
        assertThat(country2.getName()).isEqualTo("Norway");
    }

    @Test
    public void getHealthMapCountryByInvalidName() {
        String healthMapCountryName = "This country does not exist";
        HealthMapCountry healthMapCountry = healthMapCountryDao.getByName(healthMapCountryName);
        assertThat(healthMapCountry).isNull();
    }

    private Country findCountryByGaulCode(Set<Country> countries, int gaulCode) {
        for (Country country : countries) {
            if (country.getGaulCode() == gaulCode) {
                return country;
            }
        }
        return null;
    }
}
