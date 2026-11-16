import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.lang.Integer;

public class Main {
	private static String ip = null;
	private static int port = 80;
	private static long detached_pid = -1;

	private static void log_usage () {
		System.err.println("usage: mock [ flag [args] ]");
		System.err.println("\t[--start] [-s]");
		System.err.println("\t[--kill] [-k]");
		System.err.println("\t[--fake-get-request URI] [-r URI]");
	}

	private static void start_api () {
		if (Main.detached_pid != -1) {
			System.out.println("API Already Started.");
			return;
		}

		System.out.println("Starting API...");

		// java -cp "mock.jar:*" MockHTTPServer
		ProcessBuilder pb = new ProcessBuilder("java", "-cp",
			"mock.jar:*", "MockHTTPServer");
		
		// >>/dev/null 2>/dev/null
		pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
		pb.redirectError(ProcessBuilder.Redirect.DISCARD);

		// run and get the process/processid
		try {
			Main.detached_pid = pb.start().pid();
		} catch (Exception e) {
			System.err.println("Failure to Start.");
			System.exit(1);
		}

		System.out.println("API Started.");
	}

	private static void stop_api () {
		if (Main.detached_pid == -1) {
			System.out.println("API Already Stopped.");
			return;
		}

		System.out.println("Stopping API...");

		// try polite shutdown
		String ip = "" + Main.ip + ":" + Main.port;
		HTTPRequests.set_ip(ip);
		try {
			HTTPRequests.query("/shutdown");
			if (HTTPRequests.status() == 200)
				return;
		} catch (Exception e) {}

		// if failure, then run kill
		System.out.println("Polite Shutdown Failure...");
		System.out.println("Killing process...");
		ProcessBuilder pb = new ProcessBuilder("kill", "" +
			Main.detached_pid);
		
		// >>/dev/null 2>/dev/null
		pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
		pb.redirectError(ProcessBuilder.Redirect.DISCARD);

		// run and get the process/processid
		try {
			pb.start();
		} catch (Exception e) {
			System.err.println("kill command couldn't" +
				" be run. Command failed");
			System.exit(1);
		}
		Main.detached_pid = -1;

		System.out.println("API Stopped.");
	}

	private static void gloves_GET_mapping () {
		// generate mock data
		int index = (int)(5 * Math.random());
		int[] left = new int[5];
		int[] right = new int[5];
		for (int i = 0; i < 5; i++) {
			left[i] = (int)(Math.random() + 0.5);
			right[i] = (int)(Math.random() + 0.5);
		}

		// Convert arrays to strings
		String leftString = "[" + left[0];
		String rightString = "[" + right[0];
		for (int i = 1; i < 5; i++) {
			leftString += ", " + left[i];
			rightString += ", " + right[i];
		}
		leftString += "],\n";
		rightString += "]\n";
		

		// create response
		String response = "{\n\"index\": " + index + ",\n\"left\": "
			+ leftString + "\"right\": " + rightString + "}";
		System.out.println(response);
	}

	private static void handle_unmapped_GET (String URI) {
		String response = "{\n\"type\": \"http://" +
		(Main.ip == null ? "" : ip) + "\",\n\"title\": \"Resource Not" +
		" Found\",\n\"status\": 404,\n\"detail\": \"The request URI \'"+
		URI + "\' does not exist.\",\n\"instance\": \"" + URI +
		"\"\n}";
		System.out.println(response);
	}

	public static void get_pid () {
		String command = "ps aux | grep MockHTTPServer | grep -v grep" +
			" | awk '{print $2}'";

		ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
		
		// 2>/dev/null
		pb.redirectError(ProcessBuilder.Redirect.DISCARD);

		// run and get the process/proceess output
		String output = "";
		try {
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream(),
				StandardCharsets.UTF_8));
			output = reader.readLine();
			if (output == null)
				output = "";
			reader.close();
		} catch (Exception e) {
			System.err.println("get_pid command couldn't" +
				" be run. Unable to identify server pid.");
			System.exit(1);
		}

		// read and set output from the process
		if (output.strip() == "") { // no pid, its not running
			Main.detached_pid = -1;
			return;
		}

		try { // grab pid from output
			Main.detached_pid = Integer.parseInt(output);
		} catch (Exception e) { // no pid, not running
			Main.detached_pid = -1;
		}
	}

	public static void main (String[] args) {
		if (args.length != 1 && args.length != 2) {
			Main.log_usage();
			System.exit(1);
		}

		// find the server pid if its running already
		get_pid();
		Main.ip = "http://localhost";

		// handle one argument case
		if (args.length == 1) {
			// start
			if (args[0].equals("--start") || args[0].equals("-s")) {
				Main.start_api();
				return;
			}

			// stop
			if (args[0].equals("--kill") || args[0].equals("-k")) {
				Main.stop_api();
				return;
			}
			
			// invalid arguments
			Main.log_usage();
			System.exit(1);
		}

		// if arglist has two args, fake must be requested
		if (!args[0].equals("--fake-get-request") &&
				!args[0].equals("-r")) {
			Main.log_usage();
			System.exit(1);
		}

		// handle request
		if (args[1].equals("/gloves"))
			Main.gloves_GET_mapping();
		else
			Main.handle_unmapped_GET(args[1]);
	}
}
