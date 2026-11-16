import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HTTPRequests {
	private static String ip;
	private static int response_status;
	private static String response_body;

	public static void set_ip (String ip) {
		HTTPRequests.ip = ip;
	}

	public static void query (String URI) {
		// handle building request
		String url = HTTPRequests.ip + URI;
		java.net.URI uri = java.net.URI.create(url); 
		HttpRequest request = HttpRequest.newBuilder(uri).build();

		// handle getting the client and sending
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = null;
		try {
			response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		} catch (Exception e) {
			throw new RuntimeException("Unable to send. " +
					"Check Network.");
		}

		// save output
		HTTPRequests.response_status = response.statusCode();
		HTTPRequests.response_body = response.body();
	}

	public static int status () {
		return HTTPRequests.response_status;
	}

	public static String get_body () {
		return HTTPRequests.response_body;
	}
}
