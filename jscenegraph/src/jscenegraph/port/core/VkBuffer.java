package jscenegraph.port.core;

import jscenegraph.database.inventor.elements.SoVkRenderVarsElement;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.port.Destroyable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.*;
import port.Port;
import vulkanguide.AllocatedBuffer;
import vulkanguide.VulkanEngine;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public class VkBuffer implements Destroyable {
    final private SoState state;
    final private FloatBuffer vertices;

    final AllocatedBuffer vertexBuffer = new AllocatedBuffer();

    public VkBuffer(SoState state, FloatBuffer vertices) {
        this.state = state;
        this.vertices = vertices.slice();

        int numVertices = this.vertices.remaining()/3;

        if(0==numVertices) {
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

        Buffer dummy = this.vertices;

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
    }

    public void call() {

        int numVertices = vertices.remaining()/3;

        if (0 != numVertices) {

            VkCommandBuffer cmd = SoVkRenderVarsElement.getImageData(state).command_buffer;
            final long[] offset = new long[1];

            VK10.vkCmdBindVertexBuffers(cmd,0,vertexBuffer._buffer,offset);
            vkCmdDraw(cmd,numVertices,1,0,0);
        }
    }

    @Override
    public void destructor() {

        long allocator = SoVkRenderVarsElement.getInit(state).allocator;

        vmaDestroyBuffer(allocator,vertexBuffer._buffer[0],vertexBuffer._allocation);

    }
}
