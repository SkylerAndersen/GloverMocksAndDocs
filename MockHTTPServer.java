import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

public class MockHTTPServer {
	private static int port;
	private static HttpServer httpServer;

	private static String default_response_mapping (String URI) {
		String response = "{\n\"type\": \"http://" +
		MockHTTPServer.httpServer.getAddress().toString() +
		"\",\n\"title\": \"Resource Not" +
		" Found\",\n\"status\": 404,\n\"detail\": \"The request URI \'"+
		URI + "\' does not exist.\",\n\"instance\": \"" + URI +
		"\"\n}";
		return response;
	}

	private static String gloves_GET_mapping () {
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
		return response;
	}

	private static String getUpstreamResponse () {
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
		String response = "{\n\"role\": \"primary\",\n\"hand\": " +
			"\"right\",\n\"index\": " + index + ",\n\"left\": " +
			leftString + "\"right\": " + rightString + "}";
		return response;
	}

	private static void gloves (HttpExchange payload) {
		// handle response logic
		boolean isGetRequest = payload.getRequestMethod().equals("GET");
		
		// Extract query parameters
		String query = payload.getRequestURI().getRawQuery();
		boolean switchToUpstreamAPI =
			(query != null && query.equals("server=esp32"));

		// regular response logic for none or unrecognized params
		String response = isGetRequest ? gloves_GET_mapping() :
					default_response_mapping("/gloves");

		// override if we want upstream response
		if (switchToUpstreamAPI && isGetRequest)
			response = getUpstreamResponse();

		// convert response to bytes
		byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

		// prepare to send response
		try {
			payload.sendResponseHeaders(200, bytes.length);
			OutputStream body = payload.getResponseBody();
			body.write(bytes);
			body.flush();
			body.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void shutdown (HttpExchange payload) {
		// handle response
		String response = "Shutting down...";

		// convert response to bytes
		byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

		// prepare to send response
		try {
			payload.sendResponseHeaders(200, bytes.length);
			OutputStream body = payload.getResponseBody();
			body.write(bytes);
			body.flush();
			body.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// shutdown server
		httpServer.stop(1);

		// quit the program
		System.exit(0);
	}

	public static void main (String[] args) {
		port = 80;
		InetSocketAddress socAddr = new InetSocketAddress(port);

		// create server, and bind to socket
		try {
			httpServer = HttpServer.create(socAddr, 0);

		} catch (Exception e) { // if bind to port 80 fails
			// try switching to socket 8080
			System.err.println("Could not start on port 80" +
				": Switching to port 8080.");
			port = 8080;
			socAddr = new InetSocketAddress(port);
			try {
				httpServer = HttpServer.create(socAddr, 0);
			} catch (Exception ex) {
				System.err.println("Critical error has" +
					" occurred. Shutting down.");
				ex.printStackTrace();
				throw new RuntimeException("Unable to" +
					" create an HTTP Server.");
			}
		}

		// create bindings for http requests
		httpServer.createContext("/gloves", payload -> gloves(payload));
		httpServer.createContext("/shutdown", payload -> 
					shutdown(payload));

		// start
		httpServer.start();
	}
}
