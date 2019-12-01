package com.tsystem.webcrawler.utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestProcessor {
	private static final Logger logger=LoggerFactory.getLogger(HttpRequestProcessor.class);

	public static Document processRequest(String url) throws IOException {
		Document document = null;
		if (url != null && !url.isEmpty()) {
			document = Jsoup.connect(url).get();
		}
		return document;
	}

	public static Map<String, Elements> fetchLinkAndImg(Document document) {
		Map<String, Elements> linksAndImages = new HashMap<>();
		if (document != null) {
			Elements links = document.select("a");
			Elements images = document.select("img");
			if (!links.isEmpty()) {
				linksAndImages.put("LINKS", links);
			}
			if (!images.isEmpty()) {
				linksAndImages.put("IMAGES", images);
			}
		}
		return linksAndImages;
	}

	public static void fetchAttributeValueOfElement(Elements elements, String attrName, Set<String> listToModify) {
		Iterator<Element> elementIterator = elements.iterator();
		while (elementIterator.hasNext()) {
			String attr = elementIterator.next().attr(attrName);
			if (attrName.equals("href") ) {
				if((attr.startsWith("http") || attr.startsWith("https")) && !attr.isEmpty()) {
					listToModify.add(attr);
				}
			} else {
				listToModify.add(attr);
			}
		}
	}

}
