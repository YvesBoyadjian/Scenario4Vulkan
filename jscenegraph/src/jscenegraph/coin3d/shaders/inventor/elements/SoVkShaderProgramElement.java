package jscenegraph.coin3d.shaders.inventor.elements;

import jscenegraph.coin3d.inventor.lists.SbList;
import jscenegraph.coin3d.shaders.SoGLShaderProgram;
import jscenegraph.coin3d.shaders.SoVkShaderProgram;
import jscenegraph.database.inventor.elements.SoElement;
import jscenegraph.database.inventor.elements.SoReplacedElement;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;

public class SoVkShaderProgramElement extends SoReplacedElement {

    public SoVkShaderProgram shaderProgram;

    private final SbList<Integer> objectids = new SbList<Integer>();
    private boolean enabled;

    public void destructor()
    {
        this.shaderProgram = null;
        super.destructor();
    }

    public void
    init(SoState state)
    {
        super.init(state);
        this.shaderProgram = null;
        this.enabled = false;
    }

    public static void
    enable(SoState state, boolean onoff)
    {
        SoVkShaderProgramElement element =
                (SoVkShaderProgramElement) SoElement.getElement(state,classStackIndexMap.get(SoVkShaderProgramElement.class));
        element.enabled = onoff;
        element.objectids.truncate(0);

        if (element.shaderProgram != null) {
            if (onoff) {
                if (!element.shaderProgram.isEnabled()) element.shaderProgram.enable(state);
            }
            else {
                if (element.shaderProgram.isEnabled()) element.shaderProgram.disable(state);
            }
            element.shaderProgram.getShaderObjectIds(element.objectids);
        }
    }

    public static void
    set(SoState state, SoNode node,
        SoVkShaderProgram program)
    {
        SoVkShaderProgramElement element =
                (SoVkShaderProgramElement)SoReplacedElement.getElement(state,classStackIndexMap.get(SoVkShaderProgramElement.class), node);

        if (program != element.shaderProgram) {
            if (element.shaderProgram != null) element.shaderProgram.disable(state);
        }
        element.shaderProgram = program;
        element.enabled = false;
        element.objectids.truncate(0);
        if (program != null) program.getShaderObjectIds(element.objectids);
        // don't enable new program here. The node will call enable()
        // after setting up all the objects
    }

    public static SoVkShaderProgram
    get(SoState state)
    {
        final SoElement element = getConstElement(state, classStackIndexMap.get(SoVkShaderProgramElement.class));
        assert(element!=null);
        return (( SoVkShaderProgramElement )element).shaderProgram;
    }

    public void
    push(SoState state)
    {
        SoVkShaderProgramElement  prev = (SoVkShaderProgramElement ) getNextInStack();
        assert(prev!=null);
        this.shaderProgram = prev.shaderProgram;
        this.enabled = prev.enabled;
        this.nodeId = prev.nodeId;
        this.objectids.operator_assign( prev.objectids);
        // capture previous element since we might or might not change the
        // GL state in set/pop
        prev.capture(state);
    }

    public void
    pop(SoState state, final SoElement prevTopElement)
    {
        SoVkShaderProgramElement  elem = (SoVkShaderProgramElement )prevTopElement;
        if (this.shaderProgram != elem.shaderProgram) {
            if (elem.shaderProgram != null) {
                elem.shaderProgram.disable(state);
                elem.enabled = false;
            }
            if (this.shaderProgram != null) {
                if (this.enabled) this.shaderProgram.enable(state);
            }
        }
        else if (this.shaderProgram != null) {
            if (this.enabled != elem.enabled) {
                if (this.enabled) this.shaderProgram.enable(state);
                else this.shaderProgram.disable(state);
            }
        }
        elem.shaderProgram = null;
    }

    public boolean
    matches(final SoElement element)
    {
        SoVkShaderProgramElement elem = (SoVkShaderProgramElement) element;
        return (this.enabled == elem.enabled) && (this.objectids.operator_equal_equal(elem.objectids));
    }

    public SoElement
    copyMatchInfo()
    {
        SoVkShaderProgramElement elem =
                (SoVkShaderProgramElement) super.copyMatchInfo();

        elem.enabled = this.enabled;
        elem.objectids.operator_assign( this.objectids);
        return elem;
    }
}
