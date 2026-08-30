package io.github.arlol.newlinechecker;

import static io.github.arlol.newlinechecker.NewlinecheckerApplication.checkIfNewlineAtEof;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

	@Test
	public void gitRestoresInterruptStatus() throws Exception {
		AtomicReference<IllegalStateException> thrown = new AtomicReference<>();
		AtomicReference<Throwable> unexpected = new AtomicReference<>();
		AtomicBoolean stillInterrupted = new AtomicBoolean();
		// waitFor() only throws InterruptedException when it is reached before
		// the subprocess has been reaped, so retry until that race is won.
		Thread worker = new Thread(() -> {
			Instant deadline = Instant.now().plus(Duration.ofSeconds(30));
			while (Instant.now().isBefore(deadline)) {
				Thread.currentThread().interrupt();
				try {
					NewlinecheckerApplication.git();
				} catch (IllegalStateException e) {
					stillInterrupted
							.set(Thread.currentThread().isInterrupted());
					thrown.set(e);
					return;
				} catch (RuntimeException e) {
					unexpected.set(e);
					return;
				}
			}
		});
		worker.start();
		worker.join(Duration.ofSeconds(60));

		Assertions.assertNull(unexpected.get(), "git could not be run");
		Assertions.assertFalse(worker.isAlive(), "worker did not finish");
		Assertions.assertNotNull(thrown.get(), "waitFor was never interrupted");
		Assertions.assertInstanceOf(
				InterruptedException.class,
				thrown.get().getCause()
		);
		Assertions.assertTrue(
				stillInterrupted.get(),
				"interrupt status was not restored"
		);
	}

	private void test(boolean expected, String content) throws Exception {
		Path tempFile = Files.createTempFile(null, null);
		Files.writeString(tempFile, content);
		Assertions.assertEquals(
				expected,
				checkIfNewlineAtEof(tempFile.toString())
		);
	}

}
