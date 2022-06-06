package jscenegraph.coin3d.shaders;

import jscenegraph.coin3d.misc.SoGL;

public class SoVkShaderObject {
    public String sourcehint;
    private
    SoGLShaderObject.ShaderType shadertype;
    private boolean isActiveFlag ;
    private boolean paramsdirty;
    private int id;

    private static int shaderid = 0;

    public SoVkShaderObject()
    {
        this.isActiveFlag = true;
        this.shadertype = SoGLShaderObject.ShaderType.VERTEX;
        this.paramsdirty = true;
        //this.glctx = SoGL.cc_glglue_instance(cachecontext);
        //this.cachecontext = cachecontext;
        this.id = ++shaderid;
    }

    public void setShaderType(SoGLShaderObject.ShaderType vertex) {
    }

    public void load(String cachedSourceProgram) {
    }

    public void setIsActive(boolean isactive) {
    }

    public int getShaderObjectId() {
        return this.id;
    }
}
