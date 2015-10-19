package uva.elastic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		InetSocketTransportAddress host = new InetSocketTransportAddress(
				InetAddress.getByName("188.166.61.246"), 9300);
		System.out.println("Hello World!");

		// Node node = nodeBuilder().clusterName("yourclustername").node();
		// //Client client = node.client();
		// Settings settings = ImmutableSettings.settingsBuilder()
		// .put("cluster.name", "myClusterName").build();
		// Client client = new TransportClient(settings);
		// client.addTransportAddress(host);

		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "elasticsearch").build();
		Client client = new TransportClient(settings);
		client = (Client) ((TransportClient) client)
				.addTransportAddress(new InetSocketTransportAddress(
						"188.166.61.246", 9301));
		// Client client = new TransportClient().addTransportAddress(new
		// InetSocketTransportAddress("188.166.61.246", 9300));
//
//		QueryBuilder qb = matchQuery(
//		    "name",                  
//		    "kimchy elasticsearch"   
//		);
//		
		// .addTransportAddress(host);
		Gson gson = new GsonBuilder().create();

		// on shutdown
		client.close();

		// node.close();
	}
}
