package agora.attributes;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimReifierMethodAttributeTest {
    @Test
    public void shouldSerialize() throws NoSuchMethodException, IOException, ClassNotFoundException {
        var method = List.class.getMethod("get", int.class);
        var expected = new PrimitiveReifierMethodAttribute(method);
        var bytes = new ByteArrayOutputStream();
        var output = new ObjectOutputStream(bytes);
        output.writeObject(expected);
        var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        var actual = input.readObject();
        assertEquals(expected, actual);
    }
}