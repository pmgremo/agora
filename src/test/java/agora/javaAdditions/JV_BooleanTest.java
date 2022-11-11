package agora.javaAdditions;

import agora.errors.AgoraError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JV_BooleanTest {

    @Test
    public void shouldNot() {
        assertTrue(JV_Boolean.notB(Boolean.FALSE));
    }

}