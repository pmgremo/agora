package agora.tools;

import agora.reflection.*;

import java.io.*;

public class SingleRoot implements Serializable {
    public AgoraGlobals globalStructures;
    public Up reflectionWrappers;
    public SingleRoot(AgoraGlobals g,Up u)
    {
	this.globalStructures = g;
	this.reflectionWrappers = u;
    }
}


