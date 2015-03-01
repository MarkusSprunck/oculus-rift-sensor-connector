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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class is a minimal web server to provide data to a external client of the
 * Oculus Rift Sensor Data
 */
public class WebServer extends Thread {

	private static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());

	private static final String NL = System.getProperty("line.separator");

	private final FetcherThread fetcher;

	private final int port;

	public WebServer(final int port, final FetcherThread fetcherThread) {
		this.port = port;
		this.fetcher = fetcherThread;
	}

	@Override
	public void run() {
		boolean isRunning = true;
		Socket connection = null;
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
			while (isRunning) {

				connection = server.accept();
				final OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				final InputStream in = new BufferedInputStream(connection.getInputStream());
				final String request = readFirstLineOfRequest(in);

				if (request.startsWith("GET /terminate")) {
					isRunning = false;
				} else {

					if (request.indexOf("HTTP/1.1") != -1) {
						final String headerString = "HTTP/1.1 200 OK" + NL + "Server: Oculus Rift" + NL
								+ "Content-Type: application/json" + NL + NL;
						final byte[] header = headerString.getBytes("UTF-8");
						out.write(header);
						out.flush();
					}

					// Create content of response
					final String data = this.fetcher.getLatestData();
					out.write(data.getBytes());
					out.flush();

					LOGGER.log(Level.FINEST, data);
				}

				// Close all resources
				connection.close();
				in.close();
				out.close();
			}

		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

		try {
			server.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	private String readFirstLineOfRequest(final InputStream in) throws IOException {
		final StringBuffer request = new StringBuffer(200);
		while (true) {
			final int character = in.read();
			if ((character == '\n') || (character == '\r') || (character == -1)) {
				break;
			}
			request.append((char) character);
		}
		return request.toString();
	}

}