package program.secondTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecondTest {

    private TestExecutable testExecutable = new TestExecutable();

    @Test
    @DisplayName("Test if true holds")
    void testMethod() {
        String s = testExecutable.append123("123");
        assertEquals(s, "123123");
    }

}