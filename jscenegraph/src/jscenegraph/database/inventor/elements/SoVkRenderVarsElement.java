package jscenegraph.database.inventor.elements;

import jscenegraph.database.inventor.misc.SoState;
import vkbootstrap.example.Cleaner;
import vkbootstrap.example.ImageData;
import vkbootstrap.example.Init;
import vkbootstrap.example.RenderData;

public class SoVkRenderVarsElement extends SoElement {

    private Init init;
    private RenderData data;
    private Cleaner cleaner;
    private ImageData imageData;

    //! Return the top (current) instance of the element in the state
    //! Note it does NOT cause cache dependency!
    //! It also casts away the  .
    public  static  SoVkRenderVarsElement getInstance( SoState state)
    {return (SoVkRenderVarsElement )
            (state.getConstElement(classStackIndexMap.get(SoVkRenderVarsElement.class)));}

    @Override
    public boolean matches(SoElement elt) {
        return (init == (( SoVkRenderVarsElement ) elt).init) &&
                (data == (( SoVkRenderVarsElement ) elt).data) &&
                (cleaner == (( SoVkRenderVarsElement ) elt).cleaner) &&
                (imageData == (( SoVkRenderVarsElement ) elt).imageData);
    }

    @Override
    public SoElement copyMatchInfo() {
        SoVkRenderVarsElement result =
                (SoVkRenderVarsElement )getTypeId().createInstance();

        result.init = init;
        result.data = data;
        result.cleaner = cleaner;
        result.imageData = imageData;

        return result;
    }

    public void setElt(Init init, RenderData data, Cleaner cleaner, ImageData imageData) {
        this.init = init;
        this.data = data;
        this.cleaner = cleaner;
        this.imageData = imageData;
    }

    public static Init getInit(SoState state) {
        SoVkRenderVarsElement elt = (SoVkRenderVarsElement)
                getConstElement(state, classStackIndexMap.get(SoVkRenderVarsElement.class));
        return elt.init;
    }

    public static RenderData getRenderData(SoState state) {
        SoVkRenderVarsElement elt = (SoVkRenderVarsElement)
                getConstElement(state, classStackIndexMap.get(SoVkRenderVarsElement.class));
        return elt.data;
    }
    public static Cleaner getCleaner(SoState state) {
        SoVkRenderVarsElement elt = (SoVkRenderVarsElement)
                getConstElement(state, classStackIndexMap.get(SoVkRenderVarsElement.class));
        return elt.cleaner;
    }

    public static ImageData getImageData(SoState state) {
        SoVkRenderVarsElement elt = (SoVkRenderVarsElement)
                getConstElement(state, classStackIndexMap.get(SoVkRenderVarsElement.class));
        return elt.imageData;
    }
}
