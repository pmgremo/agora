package agora.tools;

import agora.reflection.Up;

import java.io.Serializable;

public class SingleRoot implements Serializable {
    public AgoraGlobals globalStructures;
    public Up reflectionWrappers;

    public SingleRoot(AgoraGlobals g, Up u) {
        this.globalStructures = g;
        this.reflectionWrappers = u;
    }
}


