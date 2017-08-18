/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.example.connectivity.connection;

import org.apache.log4j.Logger;
import org.mobicents.slee.connector.remote.RemoteSleeConnectionService;
import org.restcomm.slee.service.events.CustomEvent;

import javax.slee.EventTypeID;
import javax.slee.connection.ExternalActivityHandle;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SleeConnectionTest implements SleeConnectionTestMBean {

	private static final Logger logger = Logger.getLogger(SleeConnectionTest.class);

	private final static String eventName = "org.restcomm.slee.service.connectivity.Event_1";
	private final static String eventVendor = "org.restcomm";
	private final static String eventVersion = "1.0";

	public final static String OBJECT_NAME = "org.restcomm.slee:type=SleeConnectionTest";

	private String rmiAddress = "localhost";
	private int rmiPort = 5555;

	public SleeConnectionTest(String rmiAddress, int rmiPort) {
		this.rmiAddress = rmiAddress;
		this.rmiPort = rmiPort;
	}

	public void fireEvent(String messagePassed) {
		logger.info("Attempting call to RemoteSleeConnectionService.");
		try {
			Registry registry = LocateRegistry.getRegistry(this.rmiAddress, this.rmiPort);
			logger.info("rmiAddress is: " + this.rmiAddress + " and rmiPort is: " + this.rmiPort);

			RemoteSleeConnectionService rmiStub =
					(RemoteSleeConnectionService) registry.lookup("RemoteSleeConnectionService");
			logger.info("rmiStub is: " + rmiStub);

			ExternalActivityHandle handle = rmiStub.createActivityHandle();
			logger.info("handle is: " + handle);

			EventTypeID requestType = rmiStub.getEventTypeID(eventName, eventVendor, eventVersion);
			logger.info("The event type is: " + requestType);

			CustomEvent customEvent = new CustomEvent();
			customEvent.setMessage(messagePassed);
			rmiStub.fireEvent(customEvent, requestType, handle, null);

		} catch (Exception e) {
			logger.error("Exception caught in event fire method!", e);
		}
	}
}
