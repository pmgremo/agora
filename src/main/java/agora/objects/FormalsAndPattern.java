package agora.objects;

import agora.patterns.Pattern;

import java.io.Serializable;

/**
 * This class represents a simple record that is used for collecting
 * the pattern, category and formal parameters when walking
 * through a list of modifiers like LOCAL PUBLIC MIXIN CLONING METHOD:.
 * Since the arguments in this record are accessed hundreds of times
 * when evaluating an Agora program, we have decided to make the
 * items publicly available, for efficiency reasons.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    1:49 am
 */

public record FormalsAndPattern(String[] formals, Pattern pattern, int cat) implements Serializable {
    public FormalsAndPattern withCat(int cat) {
        return new FormalsAndPattern(formals, pattern, cat);
    }
}
