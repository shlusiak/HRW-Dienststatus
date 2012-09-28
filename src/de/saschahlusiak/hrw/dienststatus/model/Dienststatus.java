package de.saschahlusiak.hrw.dienststatus.model;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import android.content.Context;
import android.util.Log;
import de.saschahlusiak.hrw.dienststatus.R;

public class Dienststatus {
	private static final String tag = Dienststatus.class.getSimpleName();
	private static final String uri = "http://nagvis-pub.hs-weingarten.de/cgi-bin/nagxml.pl?all";
	
	private static Document dom = null;
	private static ArrayList<HRWNode> allnodes = new ArrayList<HRWNode>();

	public static ArrayList<HRWNode> getAllNodes() {
		return allnodes;
	}
	
	static String getTextContent(Node property) {
		Text text = (Text)property.getChildNodes().item(0);
		return text.getData();
	}

	private static void parseService(HRWNode node, Node property) {
		HRWService service = new HRWService(null, null, HRWNode.UNSET);
		for (int i = 0; i < property.getChildNodes().getLength(); i++) {
			Node sub = property.getChildNodes().item(i);

			if (sub.getNodeName().equals("name"))
				service.name = getTextContent(sub);
			if (sub.getNodeName().equals("output"))
				service.output = getTextContent(sub);
			if (sub.getNodeName().equals("status"))
				service.status = Integer.valueOf(getTextContent(sub));
			if (sub.getNodeName().equals("acknowledged"))
				service.acknowledged = Integer.valueOf(getTextContent(sub)) == 1;
		}
		if (service.output != null)
			node.output.add(service);
	}
	
	public static HRWNode findNode(String id) {
		for (HRWNode node: allnodes) {
			if (node.id.equals(id))
				return node;
		}
		return null;
	}

	private static void parseLevel(Node item, HRWNode HRWparent) {
		NodeList properties = item.getChildNodes();
		HRWNode node = new HRWNode(HRWparent);
		allnodes.add(node);
		for (int j = 0; j < properties.getLength(); j++) {
			Node property = properties.item(j);
			String name = property.getNodeName();

			if (name.equals("name"))
				node.name = getTextContent(property);
			if (name.equals("title"))
				node.title = getTextContent(property);
			if (name.equals("url"))
				node.url = getTextContent(property);
			if (name.equals("duration"))
				node.duration = getTextContent(property);
			if (name.equals("acknowledged"))
				node.acknowledged = Integer.valueOf(getTextContent(property)) == 1;
			if (name.equals("status"))
				node.status = Integer.valueOf(getTextContent(property));
			if (name.equals("menuindex"))
				node.id = getTextContent(property);
			if (name.equals("output"))
				node.output.add(new HRWService(null, getTextContent(property), HRWNode.UNSET));
			if (name.equals("service"))
				parseService(node, property);
			if (name.equals("group")) {
				node.hasSubItems = true;
				parseLevel(property, node);
			}
			if (name.equals("hostentry")) {
				for (int k = 0; k < property.getChildNodes().getLength(); k++) {
					Node p = property.getChildNodes().item(k);
					if (p.getNodeName().equals("service"))
						parseService(node, p);
				}
			}
		}
	}
	
	public synchronized static String fetch(Context context) {
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest req = new HttpGet(uri);
			req.addHeader("Accept-Encoding", "gzip");

			HttpResponse resp = client.execute(req);

			StatusLine status = resp.getStatusLine();
			if (status.getStatusCode() != 200) {
				Log.d(tag,
						"HTTP error, invalid server status code: "
								+ resp.getStatusLine());

				return context.getString(R.string.invalid_http_status,
						resp.getStatusLine());
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			InputStream is = resp.getEntity().getContent();
			Header contentEncoding = resp.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
			    is = new GZIPInputStream(is);
			}
			dom = builder.parse(is);
		} catch (UnknownHostException e) {
			Log.e(tag, e.getMessage());
			return "Unknown host: " + e.getMessage();
		} catch (Exception e) {
			Log.e(tag, e.getMessage());
			return e.getMessage();
		}
		if (Thread.interrupted()) {
			return null;
		}

		/* Should always be != null */
		if (dom != null) {
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("map");
			synchronized (allnodes) {
				allnodes.clear();

				NodeList properties = items.item(0).getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();

					if (name.equals("group")) {
						parseLevel(property, null);
					}
				}
			}
		}
		return null;
	}
	
	public static String fakeData() {
		synchronized (allnodes) {
			allnodes.clear();
			HRWNode n;

			n = new HRWNode()
				.setName("Top 1")
				.setId("all.1")
				.setStatus(HRWNode.OK);
			allnodes.add(n);

			n = new HRWNode(n)
				.setName("Top 1.1")
				.setId("all.1.1")
				.setStatus(HRWNode.OK);
			allnodes.add(n);			

			n = new HRWNode(n)
				.setName("Top 1.1.1")
				.setId("all.1.1.1")
				.setStatus(HRWNode.OK);
			allnodes.add(n);			

			
			n = new HRWNode()
				.setName("Top 2")
				.setId("all.2")
				.setStatus(HRWNode.WARNING);
			allnodes.add(n);
			
			n = new HRWNode(n)
				.setName("Top 2.1")
				.setId("all.2.1")
				.setStatus(HRWNode.WARNING);
			allnodes.add(n);
			
			
			n = new HRWNode()
				.setName("Top 3")
				.setId("all.3")
				.setStatus(HRWNode.ERROR);
			allnodes.add(n);
			
			n = new HRWNode(n)
				.setName("Top 3.1")
				.setId("all.3.1")
				.setStatus(HRWNode.ERROR);
			allnodes.add(n);			
			
			
			n = new HRWNode()
				.setName("Top 4")
				.setId("all.4")
				.setStatus(HRWNode.OK);
			allnodes.add(n);
			
			n = new HRWNode(n)
				.setName("Top 4.1")
				.setId("all.4.1")
				.setStatus(HRWNode.OK);
			allnodes.add(n);
		}
		return null;
	}

	public static boolean needsFetch() {
		return (dom == null);
	}
}
