package de.saschahlusiak.hrw.dienststatus.model;

import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.util.Log;
import de.saschahlusiak.hrw.dienststatus.R;

public class Dienststatus {
	private static final String tag = Dienststatus.class.getSimpleName();
	private static final HttpUriRequest uri = new HttpGet(
			"http://nagvis-pub.hs-weingarten.de/cgi-bin/nagxml.pl?all");
	
	private static Document dom = null;
	private static ArrayList<HRWNode> allnodes = new ArrayList<HRWNode>();

	public static ArrayList<HRWNode> getAllNodes() {
		return allnodes;
	}

	private static void parseService(HRWNode node, Node property) {
		HRWService service = new HRWService(null, null, HRWNode.UNSET);
		for (int i = 0; i < property.getChildNodes().getLength(); i++) {
			Node sub = property.getChildNodes().item(i);

			if (sub.getNodeName().equals("name"))
				service.name = sub.getTextContent();
			if (sub.getNodeName().equals("output"))
				service.output = sub.getTextContent();
			if (sub.getNodeName().equals("status"))
				service.status = Integer.valueOf(sub.getTextContent());
			if (sub.getNodeName().equals("acknowledged"))
				service.acknowledged = Integer.valueOf(sub.getTextContent()) == 1;
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
				node.name = property.getTextContent();
			if (name.equals("title"))
				node.title = property.getTextContent();
			if (name.equals("url"))
				node.url = property.getTextContent();
			if (name.equals("duration"))
				node.duration = property.getTextContent();
			if (name.equals("acknowledged"))
				node.acknowledged = Integer.valueOf(property.getTextContent()) == 1;
			if (name.equals("status"))
				node.status = Integer.valueOf(property.getTextContent());
			if (name.equals("menuindex"))
				node.id = property.getTextContent();
			if (name.equals("output"))
				node.output.add(new HRWService(null, property.getTextContent(), HRWNode.UNSET));
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
			final HttpResponse resp = client.execute(uri);

			final StatusLine status = resp.getStatusLine();
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
			dom = builder.parse(resp.getEntity().getContent());
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

	public static boolean needsFetch() {
		return (dom == null);
	}
}
