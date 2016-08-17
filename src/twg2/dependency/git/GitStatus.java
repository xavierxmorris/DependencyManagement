package twg2.dependency.git;

import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import lombok.val;
import twg2.collections.builder.ListUtil;
import twg2.io.exec.ExecuteCmd;
import twg2.io.exec.ProcessIoStreamFactory;
import twg2.logging.LoggingImpl;
import twg2.logging.LoggingPrefixFormat;
import twg2.text.stringUtils.StringSplit;

/** Tool for running 'git status' on a project
 * @author TeamworkGuy2
 * @since 2016-08-10
 */
public class GitStatus {

	@AllArgsConstructor
	public static class Options {
		public String gitPath;
		public String projectPath;
	}




	@AllArgsConstructor
	public static class SourceControlFileAndStatus {
		public String path;
		public String status;


		@Override
		public String toString() {
			return "(" + status + ") " + path;
		}
	}




	/** Run 'git status' on the specified project and return a list of modified/added/deleted file and directory names parsed from the git output
	 * @param opts the 'git status' paths and options
	 * @return a list of file paths and statuses
	 */
	public static List<SourceControlFileAndStatus> runGitStatus(Options opts) {
		val log = new LoggingImpl(Level.WARNING, System.out, LoggingPrefixFormat.NONE);
		val streams = new ProcessIoStreamFactory.MemoryStreams();

		ExecuteCmd.execSync(opts.gitPath + " -C " + opts.projectPath + " status", streams, log);

		val str = new String(streams.getOutputStreams().get(0).toByteArray(), Charset.forName("ASCII"));
		val lns = StringSplit.split(str, '\n');
		val fileLns = ListUtil.filter(lns, (ln) -> ln.startsWith("\t"));
		val filesAndTypes = ListUtil.map(fileLns, (ln) -> {
			int separatorIdx = ln.indexOf(':');
			String status = separatorIdx > -1 ? ln.substring(0, separatorIdx).trim() : "added";
			String path = ln.substring(separatorIdx + 1).trim();
			return new SourceControlFileAndStatus(path, status);
		});
		return filesAndTypes;
	}

}
