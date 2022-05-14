package jscenegraph.coin3d.shaders.inventor.elements;

import jscenegraph.coin3d.shaders.SoGLShaderProgram;
import jscenegraph.coin3d.shaders.SoVkShaderProgram;
import jscenegraph.database.inventor.elements.SoElement;
import jscenegraph.database.inventor.elements.SoReplacedElement;
import jscenegraph.database.inventor.misc.SoState;

public class SoVkShaderProgramElement extends SoReplacedElement {

    public SoVkShaderProgram shaderProgram;

    public static SoVkShaderProgram
    get(SoState state)
    {
        final SoElement element = getConstElement(state, classStackIndexMap.get(SoVkShaderProgramElement.class));
        assert(element!=null);
        return (( SoVkShaderProgramElement )element).shaderProgram;
    }
}
