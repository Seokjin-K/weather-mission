package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WeatherApplicationTests {

    @Test
    void equalTest() {
        // given
        // when
        // then
        assertEquals(1, 1);
    }

    @Test
    void nullTest() {
        // given
        // when
        // then
        assertNull(null);
    }

    @Test
    void trueTest() {
        // given
        // when
        // then
        assertEquals(1, 1);
    }

}
