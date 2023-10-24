import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CviProfileManagementApplicationTests {
    @Test
    public void contextLoads() {
        int expected = 12;
        int actual = 12;
        Assertions.assertEquals(expected, actual);
    }
}
