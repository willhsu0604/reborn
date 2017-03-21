package program.firstTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FirstTest {

    private TestExecutable testExecutable = new TestExecutable();

    @Test
    @DisplayName("TestExecutable.testMethod pass in 123 should return 123123")
    void testMethod() {
        String s = testExecutable.append123("123");
        assertEquals(s, "123123");
    }

}
