import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Indexer {
	public static final int DEFAULT_BATCH_SIZE = 10000;
	public static final int NUM_CORES = 2;
	public static boolean PARALLEL = false;
	public static int coresUsed = 0;
	
	private List<String> files = null;
	
	private long startTime = 0;
	private long endTime = 0;
	
	public Indexer(List<String> files) {
		this.files = files;
	}
	
	public void index() {
		if (PARALLEL) {
			Log.debug("Parallel mode.");
		}
		Log.debug("Optimising setup");
		//if (Curl.createIndex("telegraaf") == 0) {
		//	Log.debug("Created index: telegraaf");
		//} else {
		//	Log.error("Failed to create index: telegraaf");
		//}
		if (Curl.runPreOpt() == 0) {
			Log.debug("Successfully ran pre-optimisations");
		} else {
			Log.error("Failed to run pre-optimisations");
		}

		Log.debug("Indexing " + files.size() + " files.");
		startTime = System.currentTimeMillis();
		for (String file: files) {
			Log.debug("Splitting file: " + file + "...");
			try {
				FileUtil.splitES(file, DEFAULT_BATCH_SIZE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.debug("Finished splitting!");
			
			Log.debug("Indexing...");
			File folder = new File(".");
			File[] files = folder.listFiles();

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					String fileName = files[i].getName();
					
					if (fileName.endsWith(".es")) {
						if (PARALLEL) {
							while (coresUsed == NUM_CORES) {
								try {Thread.sleep(100);} catch (InterruptedException e) {}
							}
							IndexUnit unit = new IndexUnit(files[i]);
							unit.start();
						} else {
							System.out.println("Indexing: " + fileName);
							Curl.post(fileName);
							files[i].delete();
							System.out.println("Indexed: " + fileName);
						}
					}
				}
			}
			
			Log.debug("Finished indexing file!");
		}
		if (Curl.runPostOpt() == 0) {
			Log.debug("Successfully ran post-optimisations");
		} else {
			Log.error("Failed to run post-optimisations");
		}
		
		endTime = System.currentTimeMillis();
		
		float minutes = (endTime - startTime) / 60000.0f;
		Log.debug(String.format("Indexed %d files in %f minutes!", files.size(), minutes));
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Syntax: java -jar Indexer.jar <input file>");
			System.exit(1);
		}
		if (args.length == 2) {
			Indexer.PARALLEL = true;
		}
		
		// Get the list of files to index
		List<String> files = null;
		try {
			files = FileUtil.asList(args[0]);
		} catch (FileNotFoundException e) {
			Log.error("Could not find file: " + args[0]);
		} catch (IOException e) {
			Log.error("Failed to read file: " + args[0]);
		}
		
		Indexer indexer = new Indexer(files);
		indexer.index();
	}
}
