package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.List;
import java.util.function.Consumer;

/**
 * Decoder to parse the root element of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */
@Decoder(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentDecoder extends QppXmlDecoder {

	/*  Constants for lookups and tests */
	public static final String PROGRAM_NAME = "programName";
	public static final String ENTITY_TYPE = "entityType";
	public static final String MIPS_PROGRAM_NAME = "mips";
	public static final String CPCPLUS_PROGRAM_NAME = "cpcplus";
	public static final String ENTITY_ID = "entityId";
	static final String MIPS = "MIPS";
	static final String MIPS_GROUP = "MIPS_GROUP";
	static final String MIPS_INDIVIDUAL = "MIPS_INDIV";
	static final String ENTITY_GROUP = "group";
	static final String ENTITY_INDIVIDUAL = "individual";
	static final String CPCPLUS = "CPCPLUS";

	/**
	 * internalDecode parses the xml fragment into thisNode
	 *
	 * @param element Element
	 * @param thisNode Node
	 * @return DecodeResult.TreeFinished thisNode gets the newly parsed xml fragment
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setProgramNameOnNode(element, thisNode);
		setEntityIdOnNode(element, thisNode);
		setNationalProviderIdOnNode(element, thisNode);
		setTaxProviderTaxIdOnNode(element, thisNode);
		processComponentElement(element, thisNode);
		return DecodeResult.TREE_FINISHED;
	}

	/**
	 * Looks up the entity Id from the element if the program name is CPC+
	 * <id root="2.16.840.1.113883.3.249.5.1" extension="AR000000"
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setEntityIdOnNode(Element element, Node thisNode) {
		if (!CPCPLUS_PROGRAM_NAME.equals(thisNode.getValue(PROGRAM_NAME))) {
			return;
		}
		Consumer<? super Attribute> consumer = p ->
				thisNode.putValue(ENTITY_ID, p.getValue());
		setOnNode(element, getXpath(ENTITY_ID), consumer, Filters.attribute(), true);
	}

	/**
	 * Will decode the program name from the xml
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setProgramNameOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p -> {
			String[] nameEntityPair = getProgramNameEntityPair(p.getValue());
			thisNode.putValue(PROGRAM_NAME, nameEntityPair[0]);
			thisNode.putValue(ENTITY_TYPE, nameEntityPair[1]);
		};
		setOnNode(element, getXpath(PROGRAM_NAME), consumer, Filters.attribute(), true);
	}

	/**
	 * Will decode the NPI from the xml
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setNationalProviderIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p ->
				thisNode.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, p.getValue());
		setOnNode(element, getXpath(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER),
				consumer, Filters.attribute(), true);
	}

	/**
	 * Will decode the TPI from the xml
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setTaxProviderTaxIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p ->
				thisNode.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, p.getValue());
		setOnNode(element, getXpath(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER),
				consumer, Filters.attribute(), true);
	}

	/**
	 * Continues decoding the elements that are children of Clinical Document.
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void processComponentElement(Element element, Node thisNode) {
		Consumer<? super List<Element>> consumer = p -> this.decode(p, thisNode);
		setOnNode(element, getXpath("components"), consumer, Filters.element(), false);
	}

	/**
	 * decodes the program name and entity type from the name
	 *
	 * @param name String one of MIPS, MIPS_GROUP, MIPS_INDIV, or CPCPLUS
	 * @return array of String program name, entity type
	 */
	private String[] getProgramNameEntityPair(String name) {
		String[] pairs = new String[2];
		if (MIPS.equalsIgnoreCase(name) || MIPS_INDIVIDUAL.equalsIgnoreCase(name)) {
			pairs[0] = MIPS_PROGRAM_NAME;
			pairs[1] = ENTITY_INDIVIDUAL;
		} else if (MIPS_GROUP.equalsIgnoreCase(name)) {
			pairs[0] = MIPS_PROGRAM_NAME;
			pairs[1] = ENTITY_GROUP;
		} else if (CPCPLUS.equalsIgnoreCase(name)) {
			pairs[0] = CPCPLUS_PROGRAM_NAME;
			pairs[1] = "";
		} else {
			pairs[0] = name.toLowerCase(); //Unknown case
			pairs[1] = ENTITY_INDIVIDUAL;
		}
		return pairs;
	}

	/**
	 * Returns the xpath from the path-correlation.json meta data
	 *
	 * @param attribute Key to the coorelation data
	 * @return xpath expression as a string
	 */
	private String getXpath(String attribute) {
		return PathCorrelator.getXpath(TemplateId.CLINICAL_DOCUMENT.name(), attribute, defaultNs.getURI());
	}
}
