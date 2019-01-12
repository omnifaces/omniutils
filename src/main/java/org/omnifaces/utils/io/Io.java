/*
 * Copyright 2019 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Io {

	private Io() {
	}

	public static void transferData(InputStream in, OutputStream out) throws IOException {
		transferData(in, out, 1024);
	}

	public static void transferData(InputStream in, OutputStream out, int transferBlockSize) throws IOException {
		byte[] buffer = new byte[transferBlockSize];
		int read;

		while ((read = in.read(buffer)) >= 0) {
			out.write(buffer, 0, read);
		}
	}

}
