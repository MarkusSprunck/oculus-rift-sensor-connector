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

import java.util.Locale;

import org.apache.log4j.Logger;

import com.oculusvr.capi.Hmd;
import com.oculusvr.capi.OvrQuaternionf;
import com.oculusvr.capi.OvrVector3f;
import com.oculusvr.capi.TrackingState;

/**
 * Reads the sensor data from Oculus Rift
 */
class FetcherThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(FetcherThread.class.getName());

	private final Hmd hmd;

	private long id = 0;

	private static SensorData sensorData = new SensorData();

	public synchronized String getLatestData() {
		return "oculus_rift_callback({\"values\" : " + FetcherThread.sensorData.toString() + "});";
	}

	public FetcherThread(final Hmd hmd) {
		this.hmd = hmd;
	}

	@Override
	public void run() {
		while (true) {

			final TrackingState sensorState = this.hmd.getSensorState(Hmd.getTimeInSeconds());
			final OvrVector3f pos = sensorState.HeadPose.Pose.Position;
			final OvrQuaternionf quat = sensorState.HeadPose.Pose.Orientation;
			
			synchronized (this) {
				FetcherThread.sensorData.setSensorData(this.id++, pos.x, pos.y, pos.z, quat.x, quat.y, quat.z, quat.w);
			}

			try {
				Thread.sleep(1);
			} catch (final InterruptedException ex) {
				LOGGER.error(ex.getMessage());
			}
		}
	}

	public static class SensorData {

		private long id = 0;
		private double px = 0.0;
		private double py = 0.0;
		private double pz = 0.0;
		private double qx = 0.0;
		private double qy = 0.0;
		private double qz = 0.0;
		private double qw = 0.0;

		public void setSensorData(final long id, final double px, final double py, final double pz, final double qx,
				final double qy, final double qz, final double qw) {
			this.id = id;
			this.px = px;
			this.py = py;
			this.pz = pz;
			this.qx = qx;
			this.qy = qy;
			this.qz = qz;
			this.qw = qw;
		}

		@Override
		public String toString() {
			return String.format(Locale.US, "[%d, %.15f,  %.15f,  %.15f, %.15f, %.15f,  %.15f,  %.15f]", this.id, this.px, this.py, this.pz,
					this.qx, this.qy, this.qz, this.qw);
		}
	}
}