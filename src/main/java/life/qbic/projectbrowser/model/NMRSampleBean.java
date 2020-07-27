package life.qbic.projectbrowser.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;

import com.vaadin.data.util.BeanItemContainer;

import life.qbic.projectbrowser.helpers.UglyToPrettyNameMapper;
import life.qbic.xml.manager.XMLParser;
import life.qbic.xml.properties.Qproperties;

public class NMRSampleBean implements Comparable<Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1740746098057359571L;
	private String id;
	private String code;
	private String type;

	private String prettyType;
	// Map containing parents of the sample and the corresponding sample types
	// private Map<String, String> parents;
	private List<Sample> parents;
	private List<Sample> children;
	private BeanItemContainer<DatasetBean> datasets;
	private Date lastChangedDataset;
	private Map<String, String> properties;
	private Map<String, String> typeLabels;

	private String secondaryName;
	private String additionalInfo;
	private String externalDB;

	private String expType;
	private String expDesc;
	private String solvent;
	private String solventDetails;
	private String concentration;
	private String volume;
	private String pH;
	private String buffer;
	private String date;
	private String storage;
	private String storageDetails;
	private String quantitation;
	private String sampleDesc;
	private String nmrSpec;
	private String probe;
	private String probeDetails;
	private String temperature;

	private UglyToPrettyNameMapper prettyNameMapper = new UglyToPrettyNameMapper();

	public NMRSampleBean(String id, String code, String type,
			List<Sample> parents, BeanItemContainer<DatasetBean> datasets,
			Date lastChangedDataset, Map<String, String> properties,
			Map<String, String> typeLabels, List<Sample> children,
			String secondaryName, String additionalInfo, String externalDB,
			String expType, String expDesc, String solvent,
			String solventDetails, String concentration, String volume,
			String pH, String buffer, String date, String storage,
			String storageDetails, String quantitation, String sampleDesc,
			String nmrSpec, String probe, String probeDetails,
			String temperature) {
		super();
		this.id = id;
		this.code = code;
		this.type = type;
		this.prettyType = prettyNameMapper.getPrettyName(type);
		this.parents = parents;
		this.datasets = datasets;
		this.lastChangedDataset = lastChangedDataset;
		this.properties = properties;
		this.typeLabels = typeLabels;
		this.children = children;
		this.secondaryName = secondaryName;
		this.additionalInfo = additionalInfo;
		this.externalDB = externalDB;
		this.expType = expType;
		this.expDesc = expDesc;
		this.solvent = solvent;
		this.solventDetails = solventDetails;
		this.concentration = concentration;
		this.volume = volume;
		this.pH = pH;
		this.buffer = buffer;
		this.date = date;
		this.storage = storage;
		this.storageDetails = storageDetails;
		this.quantitation = quantitation;
		this.sampleDesc = sampleDesc;
		this.nmrSpec = nmrSpec;
		this.probe = probe;
		this.probeDetails = probeDetails;
		this.temperature = temperature;

	}

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
	}

	public String getExpDesc() {
		return expDesc;
	}

	public void setExpDesc(String expDesc) {
		this.expDesc = expDesc;
	}

	public String getSolvent() {
		return solvent;
	}

	public void setSolvent(String solvent) {
		this.solvent = solvent;
	}

	public String getSolventDetails() {
		return solventDetails;
	}

	public void setSolventDetails(String solventDetails) {
		this.solventDetails = solventDetails;
	}

	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getpH() {
		return pH;
	}

	public void setpH(String pH) {
		this.pH = pH;
	}

	public String getBuffer() {
		return buffer;
	}

	public void setBuffer(String buffer) {
		this.buffer = buffer;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getStorageDetails() {
		return storageDetails;
	}

	public void setStorageDetails(String storageDetails) {
		this.storageDetails = storageDetails;
	}

	public String getQuantitation() {
		return quantitation;
	}

	public void setQuantitation(String quantitation) {
		this.quantitation = quantitation;
	}

	public String getSampleDesc() {
		return sampleDesc;
	}

	public void setSampleDesc(String sampleDesc) {
		this.sampleDesc = sampleDesc;
	}

	public String getNmrSpec() {
		return nmrSpec;
	}

	public void setNmrSpec(String nmrSpec) {
		this.nmrSpec = nmrSpec;
	}

	public String getProbe() {
		return probe;
	}

	public void setProbe(String probe) {
		this.probe = probe;
	}

	public String getProbeDetails() {
		return probeDetails;
	}

	public void setProbeDetails(String probeDetails) {
		this.probeDetails = probeDetails;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public UglyToPrettyNameMapper getPrettyNameMapper() {
		return prettyNameMapper;
	}

	public void setPrettyNameMapper(UglyToPrettyNameMapper prettyNameMapper) {
		this.prettyNameMapper = prettyNameMapper;
	}

	public NMRSampleBean() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.prettyType = prettyNameMapper.getPrettyName(type);
	}

	public List<Sample> getParents() {
		return parents;
	}

	public void setParents(List<Sample> parents) {
		this.parents = parents;
	}

	public BeanItemContainer<DatasetBean> getDatasets() {
		return datasets;
	}

	public void setDatasets(BeanItemContainer<DatasetBean> datasets) {
		this.datasets = datasets;
	}

	public Date getLastChangedDataset() {
		return lastChangedDataset;
	}

	public void setLastChangedDataset(Date lastChangedDataset) {
		this.lastChangedDataset = lastChangedDataset;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Map<String, String> getTypeLabels() {
		return typeLabels;
	}

	public void setTypeLabels(Map<String, String> typeLabels) {
		this.typeLabels = typeLabels;
	}

	@Override
	public int compareTo(Object o) {
		return id.compareTo(((SampleBean) o).getId());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SampleBean) {
			SampleBean b = (SampleBean) o;
			return id.equals(b.getId());
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getParentsFormattedString() {
		String parentsHeader = "This sample has been derived from ";
		String parentsBottom = "<ul>";

		if (this.getParents() == null || this.getParents().isEmpty()) {
			return parentsHeader += "None";
		} else {
			for (Sample sample : this.getParents()) {
				parentsBottom += "<li><b>"
						+ sample.getCode()
						+ "</b> ("
						+ prettyNameMapper.getPrettyName(sample
								.getSampleTypeCode()) + ") </li>";
			}
			parentsBottom += "</ul>";

			return parentsHeader + parentsBottom;
		}
	}

	public String generatePropertiesFormattedString() {
		String propertiesBottom = "<ul> ";

		Iterator<Entry<String, String>> it = this.getProperties().entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry pairs = it.next();
			if (pairs.getKey().equals("Q_PROPERTIES")
					|| pairs.getKey().equals("Q_NOTES")) {
				continue;
			} else {
				propertiesBottom += "<li><b>"
						+ (typeLabels.get(pairs.getKey()) + ":</b> "
								+ pairs.getValue() + "</li>");
			}
		}
		propertiesBottom += "</ul>";

		return propertiesBottom;
	}

	public String generateXMLPropertiesFormattedString() throws JAXBException {

		String xmlPropertiesBottom = "<ul> ";

		Iterator<Entry<String, String>> it = this.getProperties().entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry pairs = it.next();
			if (pairs.getKey().equals("Q_PROPERTIES")) {
				XMLParser xmlParser = new XMLParser();
				JAXBElement<Qproperties> xmlProperties = xmlParser
						.parseXMLString(pairs.getValue().toString());
				Map<String, String> xmlPropertiesMap = xmlParser
						.getMapOfProperties(xmlProperties);

				for (Object o : xmlPropertiesMap.entrySet()) {
					Entry pairsProperties = (Entry) o;

					xmlPropertiesBottom += "<li><b>"
					// + (typeLabels.get(pairsProperties.getKey()) + ":</b> "
							+ (pairsProperties.getKey() + ":</b> "
									+ pairsProperties.getValue() + "</li>");
				}
				break;
			}
		}
		return xmlPropertiesBottom;
	}

	public List<Sample> getChildren() {
		return children;
	}

	public void setChildren(List<Sample> children) {
		this.children = children;
	}

	public String getPrettyType() {
		return prettyType;
	}

	public void setPrettyType(String prettyType) {
		this.prettyType = prettyType;
	}

	public String getSecondaryName() {
		return secondaryName;
	}

	public void setSecondaryName(String secondaryName) {
		this.secondaryName = secondaryName;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getExternalDB() {
		return externalDB;
	}

	public void setExternalDB(String externalDB) {
		this.externalDB = externalDB;
	}

}