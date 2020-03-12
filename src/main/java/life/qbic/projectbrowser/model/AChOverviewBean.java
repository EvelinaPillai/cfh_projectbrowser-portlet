package life.qbic.projectbrowser.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ProgressBar;

public class AChOverviewBean implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -477843905706560380L;
	private String id; // project-ID
	private String code;
	private String space; // uvb
	private String contact;
	private String projectManager;
	private String firstSample;
	private String lastSample;
	private int numberOfSamples;
	private String sampleDevice;
	private String sampleExtraction;
	private String sampleParameter;
	private String status;

	
	public AChOverviewBean(String projectIdentifier, String projectCode) {
		this.id = projectIdentifier;
		this.code = projectCode;
	}

	public String getContactPerson() {
		return contact;
	}

	public void setContactPerson(String contactPerson) {
		this.contact = contactPerson;
	}

	public String getProjectManager() {
		return this.projectManager;
	}

	public void setProjectManager(String manager) {
		this.projectManager = manager;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getFirstSample() {
		return firstSample;
	}

	public void setFirstSample(String firstSample) {
		this.firstSample = firstSample;
	}

	public String getLastSample() {
		return lastSample;
	}

	public void setLastSample(String lastSample) {
		this.lastSample = lastSample;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public String getSampleDevice() {
		return sampleDevice;
	}

	public void setSampleDevice(String sampleDevice) {
		this.sampleDevice = sampleDevice;
	}

	public String getSampleExtraction() {
		return sampleExtraction;
	}

	public void setSampleExtraction(String sampleExtraction) {
		this.sampleExtraction = sampleExtraction;
	}

	public String getSampleParameter() {
		return sampleParameter;
	}

	public void setSampleParameter(String sampleParameter) {
		this.sampleParameter = sampleParameter;
	}
}
