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

package org.eclipse.dawnsci.nexus.impl;

import java.util.Set;
import java.util.EnumSet;
import org.eclipse.dawnsci.analysis.api.tree.DataNode;

import org.eclipse.dawnsci.analysis.api.dataset.IDataset;

import org.eclipse.dawnsci.nexus.*;

/**
 * This is the description for the general spatial location
 * of a component - it is used by the NXgeometry class
 * 
 * @version 1.0
 */
public class NXtranslationImpl extends NXobjectImpl implements NXtranslation {

	private static final long serialVersionUID = 1L;  // no state in this class, so always compatible


	public static final Set<NexusBaseClass> PERMITTED_CHILD_GROUP_CLASSES = EnumSet.of(
		NexusBaseClass.NX_GEOMETRY);

	public NXtranslationImpl(final NexusNodeFactory nodeFactory) {
		super(nodeFactory);
	}

	public NXtranslationImpl(final long oid) {
		super(oid);
	}
	
	@Override
	public Class<? extends NXobject> getNXclass() {
		return NXtranslation.class;
	}
	
	@Override
	public NexusBaseClass getNexusBaseClass() {
		return NexusBaseClass.NX_TRANSLATION;
	}
	
	@Override
	public Set<NexusBaseClass> getPermittedChildGroupClasses() {
		return PERMITTED_CHILD_GROUP_CLASSES;
	}
	

	@Override
	public NXgeometry getGeometry() {
		return getChild("geometry", NXgeometry.class);
	}

	@Override
	public void setGeometry(NXgeometry geometry) {
		putChild("geometry", geometry);
	}

	@Override
	public IDataset getDistances() {
		return getDataset(NX_DISTANCES);
	}

	@Override
	public double getDistancesScalar() {
		return getDouble(NX_DISTANCES);
	}

	@Override
	public DataNode setDistances(IDataset distances) {
		return setDataset(NX_DISTANCES, distances);
	}

	@Override
	public DataNode setDistancesScalar(double distances) {
		return setField(NX_DISTANCES, distances);
	}

}
