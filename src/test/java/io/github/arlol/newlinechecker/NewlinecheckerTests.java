package io.github.arlol.newlinechecker;

import static io.github.arlol.newlinechecker.NewlinecheckerApplication.checkIfNewlineAtEof;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class NewlinecheckerTests {

	@Test
	public void unixNewlines() throws Exception {
		assertFalse(checkIfNewlineAtEof(
				new File("src/test/resources/unix-newlines.txt")));
	}

	@Test
	public void unixNewlinesNoEof() throws Exception {
		assertTrue(checkIfNewlineAtEof(
				new File("src/test/resources/unix-newlines-no-eof.txt")));
	}

	@Test
	public void windowsNewlines() throws Exception {
		assertFalse(checkIfNewlineAtEof(
				new File("src/test/resources/windows-newlines.txt")));
	}

	@Test
	public void windowsNewlinesNoEof() throws Exception {
		assertTrue(checkIfNewlineAtEof(
				new File("src/test/resources/windows-newlines-no-eof.txt")));
	}

	@Test
	public void empty() throws Exception {
		assertFalse(
				checkIfNewlineAtEof(new File("src/test/resources/empty.txt")));
	}

}
