package jscenegraph.port.core;

import com.jogamp.opengl.GL2;
import jscenegraph.coin3d.shaders.SoGLShaderProgram;
import jscenegraph.coin3d.shaders.inventor.elements.SoGLShaderProgramElement;
import jscenegraph.database.inventor.SbVec3fSingle;
import jscenegraph.database.inventor.elements.SoGLCacheContextElement;
import jscenegraph.database.inventor.elements.SoModelMatrixElement;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.port.Destroyable;
import jscenegraph.port.SbVec3fArray;
import jscenegraph.port.memorybuffer.FloatMemoryBuffer;

import static com.jogamp.opengl.GL.*;

public class GLVertexAttribList extends VertexAttribList {

    public class GLList implements List, Destroyable {
        GL2 gl2;
        final SbVec3fSingle translation = new SbVec3fSingle();
        java.util.List<Float> vertices;
        final int[] vbo = new int[1];
        SbVec3fArray vboArray;

        public GLList(GL2 gl2) {
            this.gl2 = gl2;
        }

        public void glTranslatef(float x, float y, float z) {
            translation.setX(x); translation.setY(y); translation.setZ(z);
        }

        public void glEndList() {
            gl2.glGenBuffers(1, vbo);
            gl2.glBindBuffer(GL_ARRAY_BUFFER,vbo[0]);

            int numVertices = vertices.size()/3;
            vboArray = new SbVec3fArray(FloatMemoryBuffer.allocateFloats(numVertices*3));
            float x,y,z;
            int j=0;
            for(int i=0; i<numVertices;i++) {
                x = vertices.get(j); j++;
                y = vertices.get(j); j++;
                z = vertices.get(j); j++;
                vboArray.setValueXYZ(i,x,y,z);
            }
            gl2.glBufferData(GL_ARRAY_BUFFER,vboArray.sizeof(),vboArray.toFloatBuffer(),GL_STATIC_DRAW);
            gl2.glBindBuffer(GL_ARRAY_BUFFER,0);
        }

        public void call(SoNode node) {
            gl2.glBindBuffer(GL_ARRAY_BUFFER,vbo[0]);
            gl2.glVertexAttribPointer(0,3,GL_FLOAT,false,/*3*Float.BYTES*/0,0);

            gl2.glEnableVertexAttribArray(0);

            gl2.glDrawArrays(GL_TRIANGLES,0,(int)vboArray.length());

            gl2.glDisableVertexAttribArray(0);

            gl2.glBindBuffer(GL_ARRAY_BUFFER,0);

            if(!translation.isNull()) {
                SoModelMatrixElement.translateBy(state, node, translation);
            }

            SoGLShaderProgram sp = SoGLShaderProgramElement.get(state);

            if(null!=sp &&sp.isEnabled())
            {
                // Dependent of SoModelMatrixElement
                sp.updateStateParameters(state);
            }
        }

        public void setVerticesList(java.util.List<Float> vertices) {
            this.vertices = vertices;
        }

        @Override
        public void destructor() {
            gl2.glDeleteBuffers(1,vbo);
            vbo[0] = 0;
        }
    }

    /**
     * Constructor
     * @param state
     * @param numToAllocate
     */
    public GLVertexAttribList(SoState state, int numToAllocate) {
        super(state, numToAllocate);

        // We must depend on the GL cache context; we can't assume that a
        // cache is valid between any two render actions, since the render
        // actions could be directed at different X servers on different
        // machines (with different ideas about which display lists have
        // been created).
        context = SoGLCacheContextElement.get(state); // TODO VK
    }

    public List glNewList(int key) {
        List l = new GLList(state.getGL2());
        lists.put(key,l);
        return l;
    }
}
