package jscenegraph.coin3d.shaders;

import jscenegraph.coin3d.inventor.lists.SbList;
import jscenegraph.coin3d.inventor.nodes.SoShaderObject;
import jscenegraph.coin3d.shaders.inventor.nodes.SoShaderProgram;
import jscenegraph.coin3d.shaders.inventor.nodes.SoShaderProgramEnableCB;
import jscenegraph.database.inventor.SbMatrix;
import jscenegraph.database.inventor.elements.SoModelMatrixElement;
import jscenegraph.database.inventor.elements.SoVkRenderVarsElement;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.port.Destroyable;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import vkbootstrap.example.ImageData;
import vkbootstrap.example.RenderData;
import vulkanguide.GPUObjectData;
import vulkanguide.MeshPushConstants;
import vulkanguide.RenderObject;
import vulkanguide.VulkanEngine;

import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.vma.Vma.vmaMapMemory;
import static org.lwjgl.util.vma.Vma.vmaUnmapMemory;
import static org.lwjgl.vulkan.VK10.*;

public class SoVkShaderProgram implements Destroyable {

    private SoVkGLSLShaderProgram   vkglslShaderProgram;

    private boolean isenabled;
    private SoShaderProgramEnableCB enablecb;
    private Object enablecbclosure;
    private final SbList <Integer> objectids = new SbList<>();

    private SoShaderProgram owner;

    public SoVkShaderProgram() {
        this.vkglslShaderProgram = new SoVkGLSLShaderProgram();

        this.isenabled = false;
        this.enablecb = null;
        this.enablecbclosure = null;
    }

    public void destructor()
    {
        this.vkglslShaderProgram.destructor();
    }

    public boolean isEnabled() {
        return this.isenabled;
    }

    public void updateStateParameters(SoState state) {
        if(this.owner != null) {
            int cachecontext = 0;//SoGLCacheContextElement.get(state);
            int cnt = owner.shaderObject.getNumNodes();
            for (int i = 0; i <cnt; i++) {
                SoNode node = owner.shaderObject.operator_square_bracket(i).get();
                if (node.isOfType(SoShaderObject.getClassTypeId())) {
                    ((SoShaderObject )node).updateStateMatrixParameters(cachecontext, state);
                    //((SoShaderObject )node).updateLights(cachecontext,state);
                }
            }

            // ________________________________________________________________________________ Object

            RenderData rd = SoVkRenderVarsElement.getRenderData(state);
            VulkanEngine ve = rd.getEngine();

            PointerBuffer objectData = memAllocPointer(1);
            vmaMapMemory(ve._allocator, ve.get_current_frame().objectBuffer._allocation, objectData);

            /*GPUObjectData*/long objectSSBO = objectData.get(0);

            int count = ve._renderables.size();
            List<RenderObject> first = ve._renderables;

            SbMatrix modelm = SoModelMatrixElement.get(state);
            Matrix4f model = new Matrix4f();//object.transformMatrix;
            modelm.toMatrix4f(model);

            for (int i = 0; i < count; i++)
            {
                RenderObject object = first.get(i);
                GPUObjectData.setModelMatrix(objectSSBO + i* GPUObjectData.sizeof(), model);
            }

            vmaUnmapMemory(ve._allocator, ve.get_current_frame().objectBuffer._allocation);

            memFree(objectData);

            //SbMatrix modelm = SoModelMatrixElement.get(state);
            //Matrix4f model = new Matrix4f();//object.transformMatrix;
            modelm.toMatrix4f(model);
            //final render matrix, that we are calculating on the cpu
            Matrix4f mesh_matrix = model;

            final MeshPushConstants constants = new MeshPushConstants();
            constants.render_matrix.set( mesh_matrix);

            //List<RenderObject> first = ve._renderables;
            RenderObject object = first.get(0);

            ImageData imageData = SoVkRenderVarsElement.getImageData(state);
            VkCommandBuffer cmd = imageData.command_buffer;

            //upload the mesh to the gpu via pushconstants
            VK10.vkCmdPushConstants(cmd, object.material.pipelineLayout/*rd.pipeline_layout[0]*/, VK_SHADER_STAGE_VERTEX_BIT, 0, /*MeshPushConstants.sizeof(),*/ constants.toFloatBuffer());


            // ________________________________________________________________________________ End Object
//            ImageData imageData = SoVkRenderVarsElement.getImageData(state);
//
//            //object data descriptor
//            vkCmdBindDescriptorSets(
//                    imageData.command_buffer,
//                    VK_PIPELINE_BIND_POINT_GRAPHICS,
//                    ve._materials.get("defaultmesh").pipelineLayout,
//                    1, /*1,*/
//                    ve.get_current_frame().objectDescriptor, /*0,*/
//                    null
//            );

        }
    }

    public void enable(SoState state) {
        this.vkglslShaderProgram.enable();

        this.isenabled = true;
        if (this.enablecb != null) {
            this.enablecb.invoke(this.enablecbclosure, state, true);
        }
    }

    public void disable(SoState state) {
        this.vkglslShaderProgram.disable();

        this.isenabled = false;
        if (this.enablecb != null) {
            this.enablecb.invoke(this.enablecbclosure, state, false);
        }
    }

    public void getShaderObjectIds(SbList<Integer> objectids) {
        objectids.operator_assign(this.objectids);
    }

    public void setOwner(SoShaderProgram soShaderProgram) {
        this.owner = soShaderProgram;
    }

    public void removeShaderObjects() {
        this.vkglslShaderProgram.removeShaderObjects();
        this.vkglslShaderProgram.removeProgramParameters();
        this.objectids.truncate(0);
    }

    public void addShaderObject(SoVkShaderObject shader) {
        this.objectids.append(shader.getShaderObjectId());
    }
}
