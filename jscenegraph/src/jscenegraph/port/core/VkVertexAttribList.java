package jscenegraph.port.core;

import com.jogamp.opengl.GL2;
import jscenegraph.coin3d.shaders.SoGLShaderProgram;
import jscenegraph.coin3d.shaders.SoVkShaderProgram;
import jscenegraph.coin3d.shaders.inventor.elements.SoGLShaderProgramElement;
import jscenegraph.coin3d.shaders.inventor.elements.SoVkShaderProgramElement;
import jscenegraph.database.inventor.SbVec3fSingle;
import jscenegraph.database.inventor.elements.SoModelMatrixElement;
import jscenegraph.database.inventor.elements.SoVkRenderVarsElement;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.port.Destroyable;
import jscenegraph.port.SbVec3fArray;
import jscenegraph.port.memorybuffer.FloatMemoryBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.*;
import port.Port;
import vulkanguide.AllocatedBuffer;
import vulkanguide.VulkanEngine;

import java.nio.Buffer;
import java.nio.LongBuffer;

import static com.jogamp.opengl.GL.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public class VkVertexAttribList extends VertexAttribList {

    public class VkList implements List, Destroyable {

        GL2 gl2;
        final SbVec3fSingle translation = new SbVec3fSingle();
        java.util.List<Float> vertices;
        final AllocatedBuffer vertexBuffer = new AllocatedBuffer();
        final int[] vbo = new int[1];
        SbVec3fArray vboArray;

        public VkList(GL2 gl2) {
            this.gl2 = gl2;
        }

        public void glTranslatef(float x, float y, float z) {
            translation.setX(x); translation.setY(y); translation.setZ(z);
        }

        public void glEndList() {

            int numVertices = vertices.size()/3;

            if (0==numVertices) {
                return;
            }

            long bufferSize = numVertices * 3 * Float.BYTES;
            //allocate vertex buffer
            final VkBufferCreateInfo stagingBufferInfo = VkBufferCreateInfo.create();
            stagingBufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            stagingBufferInfo.pNext(0);
            //this is the total size, in bytes, of the buffer we are allocating
            stagingBufferInfo.size(bufferSize);
            //this buffer is going to be used as a Vertex Buffer
            stagingBufferInfo.usage(VK_BUFFER_USAGE_TRANSFER_SRC_BIT);

            //let the VMA library know that this data should be writeable by CPU, but also readable by GPU
            final VmaAllocationCreateInfo vmaallocInfo = VmaAllocationCreateInfo.create();
            vmaallocInfo.usage(VMA_MEMORY_USAGE_CPU_ONLY);

            final AllocatedBuffer stagingBuffer = new AllocatedBuffer();

            LongBuffer dummy1 = memAllocLong(1);
            PointerBuffer dummy2 = memAllocPointer(1);

            long allocator = SoVkRenderVarsElement.getInit(state).allocator;

            int retCode = vmaCreateBuffer(allocator, stagingBufferInfo, vmaallocInfo,
                    dummy1,
                    dummy2,
                    null);

            stagingBuffer._buffer[0] = dummy1.get(0);
            stagingBuffer._allocation = dummy2.get(0);

            memFree(dummy1);
            memFree(dummy2);

            //copy vertex data
            PointerBuffer data = memAllocPointer(1);
            retCode = vmaMapMemory(allocator,stagingBuffer._allocation,data);

            Buffer dummy = Port.dataf(vertices);

            MemoryUtil.memCopy(memAddress(dummy),data.get(),bufferSize);

            vmaUnmapMemory(allocator,stagingBuffer._allocation);

            //allocate vertex buffer
            final VkBufferCreateInfo vertexBufferInfo = VkBufferCreateInfo.create();
            vertexBufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            vertexBufferInfo.pNext(0);
            //this is the total size, in bytes, of the buffer we are allocating
            vertexBufferInfo.size(bufferSize);
            //this buffer is going to be used as a Vertex Buffer
            vertexBufferInfo.usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT);

            //let the VMA library know that this data should be gpu native
            vmaallocInfo.usage(VMA_MEMORY_USAGE_GPU_ONLY);

            LongBuffer dummy3 = memAllocLong(1);
            PointerBuffer dummy4 = memAllocPointer(1);

            //allocate the buffer
            retCode = vmaCreateBuffer(allocator,vertexBufferInfo,vmaallocInfo,
                    dummy3,
                    dummy4,
                    null);

            vertexBuffer._buffer[0] = dummy3.get(0);
            vertexBuffer._allocation = dummy4.get(0);

            VkDevice device = SoVkRenderVarsElement.getInit(state).device.device[0];
            VkQueue queue = SoVkRenderVarsElement.getRenderData(state).graphics_queue;
            long pool = SoVkRenderVarsElement.getRenderData(state).upload_command_pool[0];
            final long[] fence = SoVkRenderVarsElement.getRenderData(state).upload_fence;

            VulkanEngine.immediate_submit_static(
                    device,
                    queue,
                    pool,
                    fence,
                    (VkCommandBuffer cmd) -> {
                        final VkBufferCopy.Buffer copy = VkBufferCopy.create(1);
                        copy.dstOffset ( 0);
                        copy.srcOffset ( 0);
                        copy.size ( bufferSize);
                        vkCmdCopyBuffer(cmd, stagingBuffer._buffer[0], vertexBuffer._buffer[0], /*1,*/ copy);
                    }
            );

            vmaDestroyBuffer(allocator, stagingBuffer._buffer[0], stagingBuffer._allocation);


//            gl2.glGenBuffers(1, vbo);
//            gl2.glBindBuffer(GL_ARRAY_BUFFER,vbo[0]);
//
//            vboArray = new SbVec3fArray(FloatMemoryBuffer.allocateFloats(numVertices*3));
//            float x,y,z;
//            int j=0;
//            for(int i=0; i<numVertices;i++) {
//                x = vertices.get(j); j++;
//                y = vertices.get(j); j++;
//                z = vertices.get(j); j++;
//                vboArray.setValueXYZ(i,x,y,z);
//            }
//            gl2.glBufferData(GL_ARRAY_BUFFER,vboArray.sizeof(),vboArray.toFloatBuffer(),GL_STATIC_DRAW);
//            gl2.glBindBuffer(GL_ARRAY_BUFFER,0);
        }

        public void call(SoNode node) {

            int numVertices = vertices.size()/3;

            if (0!=numVertices) {

                VkCommandBuffer cmd = SoVkRenderVarsElement.getImageData(state).command_buffer;
                final long[] offset = new long[1];

                VK10.vkCmdBindVertexBuffers(cmd,0,vertexBuffer._buffer,offset);
                vkCmdDraw(cmd,numVertices,1,0,0);

            }
            //gl2.glBindBuffer(GL_ARRAY_BUFFER,vbo[0]);
            //gl2.glVertexAttribPointer(0,3,GL_FLOAT,false,/*3*Float.BYTES*/0,0);

            //gl2.glEnableVertexAttribArray(0);

            //gl2.glDrawArrays(GL_TRIANGLES,0,(int)vboArray.length());

            //gl2.glDisableVertexAttribArray(0);

            //gl2.glBindBuffer(GL_ARRAY_BUFFER,0);

            if(!translation.isNull()) {
                SoModelMatrixElement.translateBy(state, node, translation);
            }

            SoVkShaderProgram sp = SoVkShaderProgramElement.get(state);

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

            long allocator = SoVkRenderVarsElement.getInit(state).allocator;

            vmaDestroyBuffer(allocator,vertexBuffer._buffer[0],vertexBuffer._allocation);

//            gl2.glDeleteBuffers(1,vbo);
//            vbo[0] = 0;
        }
    }

    /**
     * Constructor
     * @param state
     * @param numToAllocate
     */
    public VkVertexAttribList(SoState state, int numToAllocate) {
        super(state, numToAllocate);
    }

    public List glNewList(int key) {
        List l = new VkVertexAttribList.VkList(state.getGL2());
        lists.put(key,l);
        return l;
    }
}
