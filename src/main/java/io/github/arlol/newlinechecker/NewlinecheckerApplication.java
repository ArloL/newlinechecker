package io.github.arlol.newlinechecker;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

public class NewlinecheckerApplication {

	public static void main(String[] args)
			throws IOException, InterruptedException {
		var files = Stream.concat(jgit().stream(), git().stream())
				.filter(NewlinecheckerApplication::filterByFilename)
				.distinct()
				.filter(NewlinecheckerApplication::checkIfNewlineAtEof)
				.collect(toList());
		files.forEach(file -> {
			System.out.println(file + " does not have a newline at EOF");
		});
		System.exit(files.isEmpty() ? 0 : 1);
	}

	private static List<String> jgit() throws IOException {
		List<String> result = new ArrayList<>();
		try (Repository repository = new FileRepositoryBuilder()
				.setMustExist(true)
				.readEnvironment()
				.findGitDir()
				.build();
				RevWalk revWalk = new RevWalk(repository);
				TreeWalk treeWalk = new TreeWalk(repository)) {
			ObjectId headId = repository.resolve("HEAD");
			RevCommit headCommit = revWalk.parseCommit(headId);
			treeWalk.addTree(headCommit.getTree());
			while (treeWalk.next()) {
				if (treeWalk.isSubtree()) {
					treeWalk.enterSubtree();
				} else {
					result.add(treeWalk.getPathString());
				}
			}
		}
		return result;
	}

	private static List<String> git() throws IOException, InterruptedException {
		Process process = new ProcessBuilder().command(Arrays
				.asList("git", "grep", "-I", "--files-with-matches", "\"\""))
				.start();
		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()))) {
			return bufferedReader.lines().collect(Collectors.toList());
		} finally {
			process.waitFor();
		}
	}

	private static boolean filterByFilename(String filename) {
		return !filename.contains(".idea/");
	}

	public static boolean checkIfNewlineAtEof(String file) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(file,
				"r")) {
			if (fileIsEmptyOrBinary(randomAccessFile)) {
				return false;
			}
			randomAccessFile.seek(randomAccessFile.length() - 1);
			return randomAccessFile.read() != 10;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static boolean fileIsEmptyOrBinary(RandomAccessFile randomAccessFile)
			throws IOException {
		byte[] bytes = new byte[8000];
		randomAccessFile.seek(0);
		int readResult = randomAccessFile.read(bytes, 0, 8000);
		if (readResult == -1) {
			// empty file
			return true;
		}
		for (int i = 0; i < readResult; i++) {
			if (bytes[i] == 0) {
				// binary file
				return true;
			}
		}
		return false;
	}

}
