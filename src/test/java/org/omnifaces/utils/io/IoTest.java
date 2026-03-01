/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.omnifaces.utils.io.Io.transferData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class IoTest {

	@Test
	public void transferDataCopiesAllBytesFromInputToOutput() throws IOException {
		byte[] data = {1, 2, 3, 4, 5};
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transferData(new ByteArrayInputStream(data), out);
		assertArrayEquals(data, out.toByteArray());
	}

	@Test
	public void transferDataWithSmallBlockSizeStillCopiesAllBytes() throws IOException {
		byte[] data = new byte[4096];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i % 128);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transferData(new ByteArrayInputStream(data), out, 7);
		assertArrayEquals(data, out.toByteArray());
	}

	@Test
	public void transferDataWithEmptyInputProducesEmptyOutput() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transferData(new ByteArrayInputStream(new byte[0]), out);
		assertArrayEquals(new byte[0], out.toByteArray());
	}

}
