package jscenegraph.port.core;

import com.jogamp.opengl.GL2;
import jscenegraph.coin3d.shaders.SoGLShaderProgram;
import jscenegraph.coin3d.shaders.inventor.elements.SoGLShaderProgramElement;
import jscenegraph.database.inventor.SbVec3f;
import jscenegraph.database.inventor.SbVec3fSingle;
import jscenegraph.database.inventor.SoInput;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.elements.SoGLCacheContextElement;
import jscenegraph.database.inventor.elements.SoModelMatrixElement;
import jscenegraph.database.inventor.misc.SoBase;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.port.Destroyable;
import jscenegraph.port.SbVec3fArray;
import jscenegraph.port.memorybuffer.FloatMemoryBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jogamp.opengl.GL.*;

public abstract class VertexAttribList implements Destroyable {
    protected SoState state;
    private int num;
    private int refCount;
    protected int context;

    protected final Map<Integer,List> lists = new HashMap<>();

    public interface List extends Destroyable {

        void call(SoNode node);

        void glTranslatef(float x, float y, float z);

        void glEndList();

        void setVerticesList(java.util.List<Float> vertices);
    }

    public VertexAttribList(SoState state, int numToAllocate) {
        this.state = state;
        num = numToAllocate;
    }

    @Override
    public void destructor() {
        for(List list : lists.values()) {
            Destroyable.delete(list);
        }
    }

    public    int getContext() { return context; }

////////////////////////////////////////////////////////////////////////
//
// Description:
//
//
// Use: public

    public void
    ref()
//
////////////////////////////////////////////////////////////////////////
    {
        ++refCount;
    }

////////////////////////////////////////////////////////////////////////
//
// Description:
//
//
// Use: public

    public void
    unref() {
        unref(null);
    }
    public void
    unref(SoState state)
//
////////////////////////////////////////////////////////////////////////
    {
        --refCount;
        if (refCount <= 0) {
            // Let the CacheContextElement delete us:
            destructor();
//        // Let SoGLCacheContext delete this instance the next time context is current.
//        SoGLCacheContextElement.scheduleDelete(state, this);
        }
    }

    public void callList(int key, SoNode node) {
        lists.get(key).call(node);
    }

    abstract public List glNewList(int key);

    public void glTranslatef(int key,float x, float y, float z) {
        lists.get(key).glTranslatef(x,y,z);
    }

    public void glEndList(int key) {
        lists.get(key).glEndList();
    }
}
