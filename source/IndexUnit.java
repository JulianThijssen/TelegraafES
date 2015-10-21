import java.io.File;


public class IndexUnit implements Runnable {
	private File file;
	
	public IndexUnit(File file) {
		this.file = file;
	}

	public void start() {
		Indexer.coresUsed++;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		String fileName = file.getName();
		System.out.println("Indexing: " + fileName);
		Curl.post(fileName);
		file.delete();
		System.out.println("Indexed: " + fileName);
		Indexer.coresUsed--;
	}
}
