package tools;

import reflection.*;
import runtime.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;

public class SingleRoot implements Serializable {
    public AgoraGlobals globalStructures;
    public Up reflectionWrappers;
    public SingleRoot(AgoraGlobals g,Up u)
    {
	this.globalStructures = g;
	this.reflectionWrappers = u;
    }
}


