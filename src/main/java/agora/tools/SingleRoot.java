package agora.tools;

import agora.reflection.Up;

import java.io.Serializable;

public record SingleRoot(AgoraGlobals globalStructures, Up reflectionWrappers) implements Serializable {
}


