/*-
 *******************************************************************************
 * Copyright (c) 2015 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This file was auto-generated from the NXDL XML definition.
 * Generated at: 2016-01-13T18:08:19.722Z
 *******************************************************************************/

package org.eclipse.dawnsci.nexus;

import org.eclipse.dawnsci.analysis.api.tree.DataNode;

import org.eclipse.dawnsci.analysis.api.dataset.IDataset;

/**
 * Use ``NXcollection`` to gather together any set of terms.
 * The original suggestion is to use this as a container
 * class for the description of a beamline.
 * For NeXus validation, ``NXcollection`` will always generate
 * a warning since it is always an optional group.
 * Anything (groups, fields, or attributes) placed in
 * an ``NXcollection`` group will not be validated.
 * 
 * @version 1.0
 */
public interface NXcollection extends NXobject {

	public static final String NX_BEAMLINE = "beamline";
	/**
	 * name of the beamline for this collection
	 * 
	 * @return  the value.
	 */
	public IDataset getBeamline();
	
	/**
	 * name of the beamline for this collection
	 * 
	 * @param beamline the beamline
	 */
	public DataNode setBeamline(IDataset beamline);

	/**
	 * name of the beamline for this collection
	 * 
	 * @return  the value.
	 */
	public String getBeamlineScalar();

	/**
	 * name of the beamline for this collection
	 * 
	 * @param beamline the beamline
	 */
	public DataNode setBeamlineScalar(String beamline);

}
