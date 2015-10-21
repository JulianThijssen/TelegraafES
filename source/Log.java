
public class Log {
	public static void out(String message) {
		System.out.println("[OUT]: " + message);
	}
	
	public static void debug(String message) {
		System.out.println("[DEBUG]: " + message);
	}
	
	public static void error(String message) {
		System.err.println("[ERROR]: " + message);
		System.exit(1);
	}
}
