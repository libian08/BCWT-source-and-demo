package edu.ttu.cvial.imageio.plugins.ttc.v50;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class TTCImageV50Metadata extends IIOMetadata {

	static final boolean standardMetadataFormatSupported = false;
	public static final String nativeMetadataFormatName =
		"edu.ttu.cvial.imageio.plugins.ttc.v50.TTCImageV50Metadata";
	static final String nativeMetadataFormatClassName =
		"edu.ttu.cvial.imageio.plugins.ttc.v50.TTCImageV50MetadataFormat";
	static final String[] extraMetadataFormatNames = null;
	static final String[] extraMetadataFormatClassNames = null;
    
	// Keyword/value pairs
	List keywords = new ArrayList();
	List values = new ArrayList();

	public TTCImageV50Metadata() {
		super(standardMetadataFormatSupported,
		      nativeMetadataFormatName,
		      nativeMetadataFormatClassName,
		      extraMetadataFormatNames,
		      extraMetadataFormatClassNames);
	}

	public IIOMetadataFormat getMetadataFormat(String formatName) {
		if (!formatName.equals(nativeMetadataFormatName)) {
			throw new IllegalArgumentException("Bad format name!");
		}
		return TTCImageV50MetadataFormat.getDefaultInstance();
	}


	public Node getAsTree(String formatName) {
		if (!formatName.equals(nativeMetadataFormatName)) {
			throw new IllegalArgumentException("Bad format name!");
		}

		// Create a root node
		IIOMetadataNode root =
			new IIOMetadataNode(nativeMetadataFormatName);

		// Add a child to the root node for each keyword/value pair
		Iterator keywordIter = keywords.iterator();
		Iterator valueIter = values.iterator();
		while (keywordIter.hasNext()) {
			IIOMetadataNode node =
				new IIOMetadataNode("KeywordValuePair");
			node.setAttribute("keyword", (String)keywordIter.next());
			node.setAttribute("value", (String)valueIter.next());
			root.appendChild(node);
		}

		return root;
	}

	public boolean isReadOnly() {
	    return false;
	}

	public void reset() {
	    this.keywords = new ArrayList();
	    this.values = new ArrayList();
	}

	public void mergeTree(String formatName, Node root)
		throws IIOInvalidTreeException {
		if (!formatName.equals(nativeMetadataFormatName)) {
			throw new IllegalArgumentException("Bad format name!");
		}

		Node node = root;
		if (!node.getNodeName().equals(nativeMetadataFormatName)) {
			fatal(node, "Root must be " + nativeMetadataFormatName);
		}
		node = node.getFirstChild();
		while (node != null) {
			if (!node.getNodeName().equals("KeywordValuePair")) {
				fatal(node, "Node name not KeywordValuePair!");
			}
			NamedNodeMap attributes = node.getAttributes();
			Node keywordNode = attributes.getNamedItem("keyword");
			Node valueNode = attributes.getNamedItem("value");
			if (keywordNode == null || valueNode == null) {
				fatal(node, "Keyword or value missing!");
			}

			// Store keyword and value
			keywords.add((String)keywordNode.getNodeValue());
			values.add((String)valueNode.getNodeValue());

			// Move to the next sibling
			node = node.getNextSibling();
		}
	}

	private void fatal(Node node, String reason)
		throws IIOInvalidTreeException {
		throw new IIOInvalidTreeException(reason, node);
	}
}
