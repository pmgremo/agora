package agora.javaAdditions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JV_BooleanTest {

    @Test
    public void shouldNot() {
        assertTrue(JV_Boolean.notB(Boolean.FALSE));
    }

}