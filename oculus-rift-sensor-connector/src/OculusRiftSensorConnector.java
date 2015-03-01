/*
 * Copyright (C) 2015, Markus Sprunck <sprunck.markus@gmail.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - The name of its contributor may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.ovrTrackingCap_MagYawCorrection;
import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.ovrTrackingCap_Orientation;
import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.ovrTrackingCap_Position;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oculusvr.capi.Hmd;

/**
 * HTTP web server that provides tracking info from Oculus Rift Sensor Data.
 */
public class OculusRiftSensorConnector {

	private static final Logger LOGGER = Logger.getLogger(OculusRiftSensorConnector.class.getName());

	private static final int port = 80801;

	public static void main(final String[] args) throws UnknownHostException {

		LOGGER.log(Level.INFO, "Oculus Rift Sensor Connector is starting");

		Hmd.initialize();
		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Wait not possible ", e);
		}
		final Hmd hmd = Hmd.create(0);
		if (hmd != null) {
			hmd.configureTracking(ovrTrackingCap_Orientation | ovrTrackingCap_MagYawCorrection
					| ovrTrackingCap_Position, 0);

			LOGGER.log(Level.INFO, "Oculus Rift is ready");

			final FetcherThread target = new FetcherThread(hmd);
			final Thread t1 = new Thread(target);
			t1.start();
			LOGGER.log(Level.INFO, "Fetcher thread started");

			final Thread serverThread = new WebServer(port, target);
			serverThread.start();
			LOGGER.log(Level.INFO, "HTTP Server started at url: http:\\\\localhost:" + port);

			try {
				t1.join();
			} catch (final InterruptedException ex) {
				Logger.getLogger(OculusRiftSensorConnector.class.getName()).log(Level.SEVERE, null, ex);
			}
			hmd.destroy();
			Hmd.shutdown();

		} else {
			LOGGER.log(Level.SEVERE, "Unable to get data from Oculus Rift, maybe device is not properly connected");
		}

		LOGGER.log(Level.INFO, "Oculus Rift Sensor Connector is stopped");
	}

}
