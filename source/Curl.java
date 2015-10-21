import java.io.IOException;

public class Curl {
	public static int createIndex(String name) {
		String[] command = {"curl", "-XPUT", "localhost:9200/telegraaf/doc"};
		
		return execute(command);
	}
	
	public static int runPreOpt() {
		String[] command = {"sh", "pre_opt.sh"};
		
		return execute(command);
	}
	
	public static int runPostOpt() {
		String[] command = {"sh", "post_opt.sh"};
		
		return execute(command);
	}
	
	public static int post(String fileName) {
		String[] command = {"curl", "-s", "-XPOST", "localhost:9200/_bulk", "--data-binary", "@" + fileName};
		
		return execute(command);
	}
	
	/** Executes the bash command and saves the output */
	public static int execute(String[] command) {
		ProcessBuilder pb = new ProcessBuilder(command);
		
		try {
			Process p = pb.start();

			p.getInputStream().close();
			int exitCode = p.waitFor();

			return exitCode;
		} catch (IOException e) {
			Log.error("Curl command failed to execute");
		} catch (InterruptedException e) {
			Log.error("Curl command got interrupted");
		}
		return -1;
	}
}
