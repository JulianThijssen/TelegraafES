import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class FileUtil {
	public static void splitES(String path, int batchSize) throws DOMException, IOException, SAXException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		int batch = 0;
		int lines = 0;
		
		String outPath = path.replace(".xml", "-" + batch + ".es");
		BufferedReader in = new BufferedReader(new FileReader(path));
		PrintWriter out = new PrintWriter(outPath, "UTF-8");
		
		Gson gson = new GsonBuilder().create();

		String line = null;
		
		while ((line = in.readLine()) != null) {
			if (line.startsWith("<pm:root>")) {
				JsonDocument jsonDoc = new JsonDocument();
				Document doc = builder.parse(new InputSource(new StringReader(line)));
				
				Element root = doc.getDocumentElement();
				Node rootChild = root.getFirstChild();
				
				while (rootChild != null) {
					String rootChildName = rootChild.getNodeName();

					if ("pm:meta".equals(rootChildName)) {
						Node metaChild = rootChild.getFirstChild();
						
						while (metaChild != null) {
							String metaChildName = metaChild.getNodeName();
							
							if ("dc:date".equals(metaChildName)) {
								jsonDoc.date = metaChild.getTextContent();
							}
							if ("dc:subject".equals(metaChildName)) {
								jsonDoc.subject = metaChild.getTextContent();
							}
							if ("dc:identifier".equals(metaChildName)) {
								jsonDoc.id = metaChild.getTextContent();
							}
							
							metaChild = metaChild.getNextSibling();
						}
					}
					if ("pm:content".equals(rootChildName)) {
						jsonDoc.source = rootChild.getAttributes().getNamedItem("pm:source").getNodeValue();
						
						Node contentChild = rootChild.getFirstChild();

						while (contentChild != null) {
							String contentChildName = contentChild.getNodeName();
							
							if ("title".equals(contentChildName)) {
								jsonDoc.title = contentChild.getTextContent();
							}
							if ("text".equals(contentChildName)) {
								jsonDoc.text = contentChild.getTextContent();
							}
							
							contentChild = contentChild.getNextSibling();
						}
					}
					
					rootChild = rootChild.getNextSibling();
				}
				
				String json = gson.toJson(jsonDoc);
				out.println("{ \"index\" : { \"_index\" : \"telegraaf\", \"_type\" : \"doc\", \"_id\" : \"" + jsonDoc.id + "\" } }");
				out.println(json);
				
				lines++;
				
				if (lines >= batchSize) {
					out.println();
					out.close();
					lines = 0;
					batch++;
					outPath = path.replace(".xml", "-" + batch + ".es");
					out = new PrintWriter(outPath, "UTF-8");
				}
			}
		}
		
		in.close();
		out.close();
	}
	
	public static List<String> asList(String path) throws FileNotFoundException, IOException {
		List<String> list = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(path));
		
		String line = null;
		while ((line = in.readLine()) != null) {
			list.add(line);
		}
		in.close();
		
		return list;
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Syntax: java -jar FileSplitter.jar <input file>");
			System.exit(1);
		}
		
		String path = args[0];
		try {
			FileUtil.splitES(path, 10000);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
}
