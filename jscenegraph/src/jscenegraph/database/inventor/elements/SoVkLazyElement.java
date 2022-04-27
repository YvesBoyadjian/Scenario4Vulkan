package jscenegraph.database.inventor.elements;

import jscenegraph.database.inventor.misc.SoState;

public class SoVkLazyElement extends SoLazyElement {

    //! Return the top (current) instance of the element in the state
    //! Note it does NOT cause cache dependency!
    //! It also casts away the  .
    public  static  SoVkLazyElement getInstance( SoState state)
    {return (SoVkLazyElement )
            (state.getConstElement(classStackIndexMap.get(SoVkLazyElement.class)));}

    public void reset(SoState state, int bitmask) {
    }
}
