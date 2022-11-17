package agora.javaAdditions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JV_FloatTest {

    public static final float variance = 0.000003f;

    private static Stream<Arguments> shouldPlusArguments() {
        return Stream.of(
                Arguments.of(4.14f, 1),
                Arguments.of(13.14f, 10L),
                Arguments.of(4.14f, 1.00F),
                Arguments.of(4.14f, 1.00D)
        );
    }

    @ParameterizedTest(name = "3.14f + {1} = {0}")
    @MethodSource("shouldPlusArguments")
    public void shouldPlus(Float expected, Number right) {
        assertEquals(expected, JV_Float.plus(3.14f, right), variance);
    }

    private static Stream<Arguments> shouldMinArguments() {
        return Stream.of(
                Arguments.of(2.14f, 1),
                Arguments.of(-6.86f, 10L),
                Arguments.of(2.14f, 1.00F),
                Arguments.of(2.14f, 1.00D)
        );
    }

    @ParameterizedTest(name = "3.14f - {1} = {0}")
    @MethodSource("shouldMinArguments")
    public void shouldMin(Float expected, Number right) {
        assertEquals(expected, JV_Float.min(3.14f, right), variance);
    }

    private static Stream<Arguments> shouldMultArguments() {
        return Stream.of(
                Arguments.of(6.28f, 2),
                Arguments.of(62.800003f, 20L),
                Arguments.of(7.85f, 2.50F),
                Arguments.of(6.908f, 2.20D)
        );
    }

    @ParameterizedTest(name = "3.14f * {1} = {0}")
    @MethodSource("shouldMultArguments")
    public void shouldMult(Float expected, Number right) {
        assertEquals(expected, JV_Float.multiply(3.14f, right), variance);
    }

    private static Stream<Arguments> shouldDivideArguments() {
        return Stream.of(
                Arguments.of(1.57f, 2),
                Arguments.of(0.157f, 20L),
                Arguments.of(1.256f, 2.50F),
                Arguments.of(1.4272728f, 2.20D)
        );
    }

    @ParameterizedTest(name = "3.14f / {1} = {0}")
    @MethodSource("shouldDivideArguments")
    public void shouldDiv(Float expected, Number right) {
        assertEquals(expected, JV_Float.divide(3.14f, right), variance);
    }

    private static Stream<Arguments> shouldPowerArguments() {
        return Stream.of(
                Arguments.of(9.859601f, 2),
                Arguments.of(8.68147E9f, 20L),
                Arguments.of(17.471256f, 2.50F),
                Arguments.of(12.394963f, 2.20D)
        );
    }

    @ParameterizedTest(name = "3.14f / {1} = {0}")
    @MethodSource("shouldPowerArguments")
    public void shouldPower(Float expected, Number right) {
        assertEquals(expected, JV_Float.power(3.14f, right), variance);
    }

    private static Stream<Arguments> shouldEqualArguments() {
        return Stream.of(
                Arguments.of(false, 3),
                Arguments.of(false, 314L),
                Arguments.of(true, 3.14F),
                Arguments.of(true, 3.14D)
        );
    }

    @ParameterizedTest(name = "3.14f = {1} is {0}")
    @MethodSource("shouldEqualArguments")
    public void shouldEqual(Boolean expected, Number right) {
        assertEquals(expected, JV_Float.equalsF(3.14f, right));
    }

    private static Stream<Arguments> shouldSmArguments() {
        return Stream.of(
                Arguments.of(true, 4),
                Arguments.of(true, 314L),
                Arguments.of(false, 3.13F),
                Arguments.of(false, 3.10D)
        );
    }

    @ParameterizedTest(name = "3.14f < {1} is {0}")
    @MethodSource("shouldSmArguments")
    public void shouldSm(Boolean expected, Number right) {
        assertEquals(expected, JV_Float.smF(3.14f, right));
    }

    private static Stream<Arguments> shouldGtArguments() {
        return Stream.of(
                Arguments.of(false, 4),
                Arguments.of(false, 314L),
                Arguments.of(true, 3.13F),
                Arguments.of(true, 3.10D)
        );
    }

    @ParameterizedTest(name = "3.14f > {1} is {0}")
    @MethodSource("shouldGtArguments")
    public void shouldGt(Boolean expected, Number right) {
        assertEquals(expected, JV_Float.gtF(3.14f, right));
    }

    private static Stream<Arguments> shouldSmeArguments() {
        return Stream.of(
                Arguments.of(true, 4),
                Arguments.of(true, 314L),
                Arguments.of(true, 3.14F),
                Arguments.of(false, 3.10D)
        );
    }

    @ParameterizedTest(name = "3.14f > {1} is {0}")
    @MethodSource("shouldSmeArguments")
    public void shouldSme(Boolean expected, Number right) {
        assertEquals(expected, JV_Float.smeF(3.14f, right));
    }

    private static Stream<Arguments> shouldGteArguments() {
        return Stream.of(
                Arguments.of(false, 4),
                Arguments.of(false, 314L),
                Arguments.of(true, 3.14F),
                Arguments.of(true, 3.10D)
        );
    }

    @ParameterizedTest(name = "3.14f > {1} is {0}")
    @MethodSource("shouldGteArguments")
    public void shouldGte(Boolean expected, Number right) {
        assertEquals(expected, JV_Float.gteF(3.14f, right));
    }
}