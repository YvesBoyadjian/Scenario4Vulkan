/*
 *
 *  Copyright (C) 2000 Silicon Graphics, Inc.  All Rights Reserved. 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  Further, this software is distributed without any warranty that it is
 *  free of the rightful claim of any third person regarding infringement
 *  or the like.  Any license provided herein, whether implied or
 *  otherwise, applies only to this software file.  Patent licenses, if
 *  any, provided herein do not apply to combinations of this program with
 *  other software, or any other product whatsoever.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact information: Silicon Graphics, Inc., 1600 Amphitheatre Pkwy,
 *  Mountain View, CA  94043, or:
 * 
 *  http://www.sgi.com 
 * 
 *  For further information regarding this notice, see: 
 * 
 *  http://oss.sgi.com/projects/GenInfo/NoticeExplan/
 *
 */


/*
 * Copyright (C) 1990,91   Silicon Graphics, Inc.
 *
 _______________________________________________________________________
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 |
 |   $Revision: 1.1.1.1 $
 |
 |   Description:
 |      This file defines the SoPointDetail class.
 |
 |   Author(s)          : Thaddeus Beier, Dave Immel, Howard Look
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.details;

import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SoType;
import jscenegraph.port.Mutable;


////////////////////////////////////////////////////////////////////////////////
//! Stores detail information about vertex-based shapes made of points.
/*!
\class SoPointDetail
\ingroup Details
This class contains detail information about a point in a vertex-based
shape made of points. It is used for returning information about an
intersection with or primitives generated by a set of points. It is
also used by SoFaceDetail and SoLineDetail to return
information about the vertices of faces and line segments.

\par See Also
\par
SoDetail, SoPickedPoint, SoPrimitiveVertex, SoVertexShape, SoFaceDetail, SoLineDetail
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SoPointDetail extends SoDetail implements Mutable {

	  private
	    static final SoType       classTypeId = new SoType();            //!< Type identifier
	  
	    //! Returns type identifier for this class.
	   public static SoType       getClassTypeId() { return new SoType(classTypeId); }

	    //! Returns the type identifier for a specific instance.
	   public SoType      getTypeId() {
		   return classTypeId;
	   }

	
	
  private
    int     coordIndex, materialIndex, normalIndex, texCoordIndex;


   public
    //! Each of these sets one of the indices in the detail
    void        setCoordinateIndex(int i)   { coordIndex    = i; }
    public void        setMaterialIndex(int i)     { materialIndex = i; }
    public void        setNormalIndex(int i)               { normalIndex   = i; }
    public void        setTextureCoordIndex(int i) { texCoordIndex = i; }
    
    // java port
    public void copyFrom(Object other) {
    	SoPointDetail otherPointDetail = (SoPointDetail) other; 
    	//super.copyFrom(other);
        coordIndex = otherPointDetail.coordIndex;
        materialIndex = otherPointDetail.materialIndex;
        normalIndex = otherPointDetail.normalIndex;
        texCoordIndex = otherPointDetail.texCoordIndex;
    	
    }
    
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns an instance that is a copy of this instance. The caller
//    is responsible for deleting the copy when done.
//
// Use: public, virtual
//

public SoDetail 
copy()
//
////////////////////////////////////////////////////////////////////////
{
    SoPointDetail newDetail = new SoPointDetail();

    newDetail.coordIndex       = coordIndex;
    newDetail.materialIndex    = materialIndex;
    newDetail.normalIndex      = normalIndex;
    newDetail.texCoordIndex    = texCoordIndex;

    return newDetail;
}
    

/*!
  Returns index into coordinate set for the point's 3D coordinates.
 */
//! Returns the index of the point within the relevant coordinate node.
public int
getCoordinateIndex()
{
  return coordIndex;
}

/*!
  Returns point's index into set of materials.
 */
//! Returns the index of the material for the point within the relevant
//! material node.
public int
getMaterialIndex()
{
  return materialIndex;
}

/*!
  Returns point's index into set of normals.
 */
//! Returns the index of the surface normal at the point within the
//! relevant normal node. Note that if normals have been generated for a
//! shape, the index may not be into an existing normal node.
public int
getNormalIndex()
{
  return normalIndex;
}

/*!
  Returns point's index into set of texture coordinates.
 */
//! Returns the index of the texture coordinates for the point within the
//! relevant normal node. Note that if texture coordinates have been
//! generated for a shape, the index may not be into an existing texture
//! coordinate node.
public int getTextureCoordIndex()
{
  return texCoordIndex;
}




////////////////////////////////////////////////////////////////////////
//
// Description:
//    Initializes class.
//
// Use: internal
//

public static void initClass()
//
////////////////////////////////////////////////////////////////////////
{
    //SO_DETAIL_INIT_CLASS(SoPointDetail, SoDetail);
    classTypeId.copyFrom( SoType.createType(SoDetail.getClassTypeId(),           
            new SbName("SoPointDetail"), null));
}


    static public int sizeof() { // For memcpy
        return Mutable.sizeof();
    }
}
