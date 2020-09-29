package io.github.arlol.newlinechecker;

import static io.github.arlol.newlinechecker.NewlinecheckerApplication.checkIfNewlineAtEof;
import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class NewlinecheckerTests {

	@Test
	public void unixNewlines() throws Exception {
		test(false, "Hello\nThere\n");
	}

	@Test
	public void unixNewlinesNoEof() throws Exception {
		test(true, "Hello\nThere");
	}

	@Test
	public void windowsNewlines() throws Exception {
		test(false, "Hello\r\nThere\r\n");
	}

	@Test
	public void windowsNewlinesNoEof() throws Exception {
		test(true, "Hello\r\nThere");
	}

	@Test
	public void empty() throws Exception {
		test(false, "");
	}

	private void test(boolean expected, String content) throws Exception {
		Path tempFile = Files.createTempFile(null, null);
		Files.writeString(tempFile, content);
		assertEquals(expected, checkIfNewlineAtEof(tempFile.toString()));
	}

}
