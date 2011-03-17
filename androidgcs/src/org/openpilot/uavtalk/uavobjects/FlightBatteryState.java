/**
 ******************************************************************************
 *
 * @file       uavobjecttemplate.cpp
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2010.
 * @brief      Template for an uavobject in java
 *             This is a autogenerated file!! Do not modify and expect a result.
 *             Battery status information.
 *
 * @see        The GNU Public License (GPL) Version 3
 *
 *****************************************************************************/
/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.openpilot.uavtalk.uavobjects;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.openpilot.uavtalk.UAVObjectManager;
import org.openpilot.uavtalk.UAVObject;
import org.openpilot.uavtalk.UAVDataObject;
import org.openpilot.uavtalk.UAVObjectField;

/**
Battery status information.

generated from flightbatterystate.xml
 **/
public class FlightBatteryState extends UAVDataObject {

	public FlightBatteryState() {
		super(OBJID, ISSINGLEINST, ISSETTINGS, NAME);
		
		List<UAVObjectField> fields = new ArrayList<UAVObjectField>();
		

		List<String> VoltageElemNames = new ArrayList<String>();
		VoltageElemNames.add("0");
		fields.add( new UAVObjectField("Voltage", "V", UAVObjectField.FieldType.FLOAT32, VoltageElemNames, null) );

		List<String> CurrentElemNames = new ArrayList<String>();
		CurrentElemNames.add("0");
		fields.add( new UAVObjectField("Current", "A", UAVObjectField.FieldType.FLOAT32, CurrentElemNames, null) );

		List<String> PeakCurrentElemNames = new ArrayList<String>();
		PeakCurrentElemNames.add("0");
		fields.add( new UAVObjectField("PeakCurrent", "A", UAVObjectField.FieldType.FLOAT32, PeakCurrentElemNames, null) );

		List<String> AvgCurrentElemNames = new ArrayList<String>();
		AvgCurrentElemNames.add("0");
		fields.add( new UAVObjectField("AvgCurrent", "A", UAVObjectField.FieldType.FLOAT32, AvgCurrentElemNames, null) );

		List<String> ConsumedEnergyElemNames = new ArrayList<String>();
		ConsumedEnergyElemNames.add("0");
		fields.add( new UAVObjectField("ConsumedEnergy", "mAh", UAVObjectField.FieldType.FLOAT32, ConsumedEnergyElemNames, null) );

		List<String> EstimatedFlightTimeElemNames = new ArrayList<String>();
		EstimatedFlightTimeElemNames.add("0");
		fields.add( new UAVObjectField("EstimatedFlightTime", "sec", UAVObjectField.FieldType.FLOAT32, EstimatedFlightTimeElemNames, null) );


		// Compute the number of bytes for this object
		int numBytes = 0;
		ListIterator<UAVObjectField> li = fields.listIterator();
		while(li.hasNext()) {
			numBytes += li.next().getNumBytes();
		}
		NUMBYTES = numBytes;

		// Initialize object
		initializeFields(fields, ByteBuffer.allocate(NUMBYTES), NUMBYTES);
		// Set the default field values
		setDefaultFieldValues();
		// Set the object description
		setDescription(DESCRIPTION);
	}

	/**
	 * Create a Metadata object filled with default values for this object
	 * @return Metadata object with default values
	 */
	public Metadata getDefaultMetadata() {
		UAVObject.Metadata metadata = new UAVObject.Metadata();
		metadata.gcsAccess = UAVObject.AccessMode.ACCESS_READONLY;
		metadata.gcsTelemetryAcked = UAVObject.Acked.FALSE;
		metadata.gcsTelemetryUpdateMode = UAVObject.UpdateMode.UPDATEMODE_MANUAL;
		metadata.gcsTelemetryUpdatePeriod = 0;

		metadata.flightAccess = UAVObject.AccessMode.ACCESS_READWRITE;
		metadata.flightTelemetryAcked = UAVObject.Acked.FALSE;
		metadata.flightTelemetryUpdateMode = UAVObject.UpdateMode.UPDATEMODE_PERIODIC;
		metadata.flightTelemetryUpdatePeriod = 1000;

		metadata.loggingUpdateMode = UAVObject.UpdateMode.UPDATEMODE_NEVER;
		metadata.loggingUpdatePeriod = 0;
		return metadata;
	}

	/**
	 * Initialize object fields with the default values.
	 * If a default value is not specified the object fields
	 * will be initialized to zero.
	 */
	public void setDefaultFieldValues()
	{
		getField("Voltage").setValue(0);
		getField("Current").setValue(0);
		getField("PeakCurrent").setValue(0);
		getField("AvgCurrent").setValue(0);
		getField("ConsumedEnergy").setValue(0);
		getField("EstimatedFlightTime").setValue(0);

	}

	/**
	 * Create a clone of this object, a new instance ID must be specified.
	 * Do not use this function directly to create new instances, the
	 * UAVObjectManager should be used instead.
	 */
	public UAVDataObject clone(int instID) {
		// TODO: Need to get specific instance to clone
		try {
			FlightBatteryState obj = new FlightBatteryState();
			obj.initialize(instID, this.getMetaObject());
			return obj;
		} catch  (Exception e) {
			return null;
		}
	}

	/**
	 * Static function to retrieve an instance of the object.
	 */
	public FlightBatteryState GetInstance(UAVObjectManager objMngr, int instID)
	{
	    return (FlightBatteryState)(objMngr.getObject(FlightBatteryState.OBJID, instID));
	}

	// Constants
	protected static final int OBJID = 0x791A50E;
	protected static final String NAME = "FlightBatteryState";
	protected static String DESCRIPTION = "Battery status information.";
	protected static final boolean ISSINGLEINST = 1 == 1;
	protected static final boolean ISSETTINGS = 0 == 1;
	protected static int NUMBYTES = 0;


}