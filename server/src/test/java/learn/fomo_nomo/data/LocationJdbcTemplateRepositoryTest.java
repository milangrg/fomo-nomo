package learn.fomo_nomo.data;

import learn.fomo_nomo.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LocationJdbcTemplateRepositoryTest {

    @Autowired
    LocationJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindById() {
        // 	(location_id, address, state, city, postal, location_name)
        //     (1, '3300 Riverfront Walk', 'NY', 'Buffalo', '14202', 'Riverside Restaurant'),
        Location expected = new Location(
                1,
                "3300 Riverfront Walk",
                "NY",
                "Buffalo",
                "14202",
                "Riverside Restaurant"
        );

        Location actual = repository.findById(1);
        assertEquals(expected, actual);
    }



}