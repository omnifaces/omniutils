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
