/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects. Copyright (C) "2016” Christopher
 * Mohr, David Wojnar, Andreas Friedrich
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package life.qbic.projectbrowser.views;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.tepi.filtertable.FilterTable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable.RowHeaderMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import ch.systemsx.cisd.openbis.generic.shared.dto.EventPE.EntityType;
import life.qbic.projectbrowser.controllers.*;
import life.qbic.projectbrowser.helpers.UglyToPrettyNameMapper;
import life.qbic.projectbrowser.helpers.Utils;
import life.qbic.projectbrowser.helpers.ViewTablesClickListener;
import life.qbic.projectbrowser.helpers.DatasetViewFilterGenerator;
import life.qbic.projectbrowser.helpers.DatasetViewFilterDecorator;
import life.qbic.projectbrowser.model.DatasetBean;
import life.qbic.projectbrowser.model.ExperimentBean;
import life.qbic.projectbrowser.model.NMRSampleBean;
import life.qbic.projectbrowser.model.SampleBean;
import life.qbic.projectbrowser.model.TestSampleBean;
import life.qbic.projectbrowser.components.MultiscaleComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExperimentView extends VerticalLayout implements View {

	/**
   * 
   */
	private static final long serialVersionUID = -9156593640161721690L;
	private static final Logger LOG = LogManager
			.getLogger(ExperimentView.class);

	public final static String navigateToLabel = "experiment";
	FilterTable table;
	VerticalLayout expview_content;

	private Button export;
	private DataHandler datahandler;
	private State state;
	private String resourceUrl;
	private VerticalLayout buttonLayoutSection;
	private FileDownloader fileDownloader;
	private ExperimentBean currentBean;
	private Label generalInfoLabel;
	private Label statContentLabel;
	private Label propertiesContentLabel;

	private UglyToPrettyNameMapper prettyNameMapper = new UglyToPrettyNameMapper();
	private TabSheet expview_tab;
	private Label experimentalFactorLabel;
	private Label idLabel;

	private VerticalLayout innerNotesComponent;
	private MultiscaleController controller;
	private MultiscaleComponent noteComponent;
	
	private BeanItemContainer<NMRSampleBean> nmrSamples =   new BeanItemContainer<NMRSampleBean>(NMRSampleBean.class);

	public ExperimentView(DataHandler datahandler, State state,
			String resourceurl, MultiscaleController controller) {
		this(datahandler, state, controller);
		this.resourceUrl = resourceurl;
	}

	public ExperimentView(DataHandler datahandler, State state,
			MultiscaleController controller) {
		this.datahandler = datahandler;
		this.state = state;
		this.controller = controller;
		resourceUrl = "javascript;";
		initView();
	}

	/**
	 * init this view. builds the layout skeleton Menubar Description and others
	 * Statistics Experiment Table Graph
	 */
	void initView() {
		setWidth(100, Unit.PERCENTAGE);
		setResponsive(true);

		expview_content = new VerticalLayout();
		expview_content.setResponsive(true);
		expview_content.setMargin(new MarginInfo(true, true, false, false));

		expview_tab = new TabSheet();
		expview_tab.setWidth(100, Unit.PERCENTAGE);
		expview_tab.setResponsive(true);

		expview_tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		expview_tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		expview_tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);

		expview_content.addComponent(expview_tab);

		expview_tab.addTab(initDescription(), "General Information").setIcon(
				FontAwesome.INFO_CIRCLE);
		// expview_tab.addTab(initStatistics(),
		// "Statistics").setIcon(FontAwesome.CHECK_CIRCLE);
		expview_tab.addTab(initProperties(), "Metadata").setIcon(
				FontAwesome.LIST_UL);
		expview_tab.addTab(initTable(), "Samples").setIcon(FontAwesome.TINT);
		initNoteComponent();
		expview_tab.addTab(innerNotesComponent).setIcon(FontAwesome.PENCIL);

		expview_content.setWidth(100, Unit.PERCENTAGE);
		this.addComponent(expview_content);
	}

	private void initNoteComponent() {

		innerNotesComponent = new VerticalLayout();

		innerNotesComponent.setIcon(FontAwesome.NAVICON);
		innerNotesComponent.setCaption("Experiment Notes");

		noteComponent = new MultiscaleComponent(controller);
		innerNotesComponent.addComponent(noteComponent);
		innerNotesComponent.setMargin(new MarginInfo(true, false, false, true));
	}

	private void updateNoteComponent() {
		noteComponent = new MultiscaleComponent(controller);
		noteComponent.updateUI(currentBean.getId(), EntityType.EXPERIMENT);
		innerNotesComponent.removeAllComponents();
		innerNotesComponent.addComponent(noteComponent);
	}

	/**
	 * This function should be called each time currentBean is changed
	 */
	public void updateContent() {
		updateContentDescription();
		updateContentStatistics();
		updateContentProperties();
		updateContentTable();
		updateNoteComponent();
		updateContentButtonLayout();
	}

	/**
	 * 
	 * @return
	 */
	HorizontalLayout initButtonLayout() {
		this.export = new Button("Export as TSV");
		buttonLayoutSection = new VerticalLayout();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(new MarginInfo(false, false, true, true));
		buttonLayout.setHeight(null);
		buttonLayout.setWidth("100%");
		buttonLayoutSection.setSpacing(true);
		buttonLayoutSection.addComponent(buttonLayout);
		buttonLayoutSection.setMargin(new MarginInfo(false, false, true, true));
		buttonLayout.addComponent(this.export);
		return buttonLayout;
	}

	void updateContentButtonLayout() {
		if (fileDownloader != null)
			this.export.removeExtension(fileDownloader);
		StreamResource sr = new StreamResource(null, resourceUrl);
		if(nmrSamples.size() >0 ){
			BeanItemContainer<NMRSampleBean> samples = nmrSamples;
			sr = Utils.getTSVStream(
				Utils.containerToString(samples),
				currentBean.getId());
		}else {
			BeanItemContainer<SampleBean> samples = currentBean.getSamples();
			sr = Utils.getTSVStream(
				Utils.containerToString(samples),
				currentBean.getId());
		}
		fileDownloader = new FileDownloader(sr);
		fileDownloader.extend(this.export);
	}

	/**
	 * initializes the description layout
	 * 
	 * @return
	 */
	VerticalLayout initDescription() {
		VerticalLayout generalInfo = new VerticalLayout();
		VerticalLayout generalInfoContent = new VerticalLayout();
		idLabel = new Label("");
		generalInfoLabel = new Label("");
		statContentLabel = new Label("");

		generalInfoContent.addComponent(idLabel);
		generalInfo.setMargin(new MarginInfo(true, false, true, true));
		generalInfoContent.addComponent(generalInfoLabel);
		generalInfoContent.setMargin(new MarginInfo(true, false, true, true));
		generalInfoContent.addComponent(statContentLabel);
		generalInfoContent.setSpacing(true);

		generalInfo.addComponent(generalInfoContent);

		return generalInfo;
	}

	void updateContentDescription() {
		idLabel.setValue(String.format("Identifier: %s", currentBean.getId()));
		generalInfoLabel.setValue(String.format("Stage:\t %s",
				prettyNameMapper.getPrettyName(currentBean.getType())));
		statContentLabel.setValue(String.format(
				"This experimental step involves %s sample(s)", currentBean
						.getSamples().size()));

	}

	/**
	 * 
	 * @return
	 * 
	 */
	VerticalLayout initStatistics() {
		VerticalLayout statistics = new VerticalLayout();

		HorizontalLayout statContent = new HorizontalLayout();
		// statContent.setCaption("Statistics");
		// statContent.setIcon(FontAwesome.BAR_CHART_O);

		// int numberOfDatasets =
		// dh.datasetMap.get(experimentBean.getId()).size();
		statContentLabel = new Label("");

		statContent.addComponent(statContentLabel);
		statContent.setMargin(new MarginInfo(true, false, true, true));

		// statContent.addComponent(new
		// Label(String.format("%s dataset(s).",numberOfDatasets )));
		// statContent.setMargin(true);
		// statContent.setMargin(new MarginInfo(false, false, false, true));
		// statContent.setSpacing(true);

		/*
		 * if (numberOfDatasets > 0) {
		 * 
		 * String lastSample = "No samples available"; if
		 * (experimentBean.getLastChangedSample() != null) { lastSample =
		 * experimentBean.getLastChangedSample();// .split("/")[2]; }
		 * statContent.addComponent(new Label(String.format( "Last change %s",
		 * String.format( "occurred in sample %s (%s)", lastSample,
		 * experimentBean.getLastChangedDataset().toString())))); }
		 */

		statistics.addComponent(statContent);
		// statistics.setMargin(true);

		// Properties of experiment
		// VerticalLayout properties = new VerticalLayout();
		// VerticalLayout propertiesContent = new VerticalLayout();
		// propertiesContent.setCaption("Properties");
		// propertiesContent.setIcon(FontAwesome.LIST_UL);
		// propertiesContentLabel = new Label("", ContentMode.HTML);
		// propertiesContent.addComponent(propertiesContentLabel);
		// properties.addComponent(propertiesContent);
		// propertiesContent.setMargin(new MarginInfo(true, false, false,
		// true));

		// properties.setMargin(true);
		// statistics.addComponent(properties);

		statistics.setMargin(new MarginInfo(true, false, true, true));
		statistics.setSpacing(true);

		return statistics;
	}

	/**
   * 
   */
	void updateContentStatistics() {
		statContentLabel.setValue(String.format("%s sample(s),", currentBean
				.getSamples().size()));
	}

	VerticalLayout initProperties() {
		// Properties of experiment
		VerticalLayout properties = new VerticalLayout();
		VerticalLayout propertiesContent = new VerticalLayout();
		// propertiesContent.setCaption("Properties");
		// propertiesContent.setIcon(FontAwesome.LIST_UL);
		propertiesContentLabel = new Label("", ContentMode.HTML);
		propertiesContentLabel.setCaption("Properties");
		experimentalFactorLabel = new Label("", ContentMode.HTML);
		experimentalFactorLabel.setCaption("Workflow Settings");

		propertiesContent.addComponent(propertiesContentLabel);
		propertiesContent.addComponent(experimentalFactorLabel);

		properties.addComponent(propertiesContent);
		propertiesContent.setMargin(new MarginInfo(true, false, true, true));

		return properties;
	}

	void updateContentProperties() {
		try {
			propertiesContentLabel.setValue(currentBean
					.generatePropertiesFormattedString());
			experimentalFactorLabel.setValue(currentBean
					.generateXMLPropertiesFormattedString());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	VerticalLayout initTable() {
		this.table = this.buildFilterTable();
		this.tableClickChangeTreeView();
		VerticalLayout tableSection = new VerticalLayout();
		HorizontalLayout tableSectionContent = new HorizontalLayout();
		// tableSectionContent.setCaption("Registered Samples");
		// tableSectionContent.setIcon(FontAwesome.FLASK);
		tableSectionContent.addComponent(this.table);
		tableSectionContent.setMargin(new MarginInfo(true, true, false, true));

		// tableSectionContent.setMargin(true);
		// tableSection.setMargin(true);
		tableSection.setMargin(new MarginInfo(true, false, false, true));

		// this.table.setWidth("100%");
		tableSection.setWidth("100%");
		tableSectionContent.setWidth("100%");

		tableSection.addComponent(tableSectionContent);

		this.export = new Button("Export as TSV");
		buttonLayoutSection = new VerticalLayout();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(new MarginInfo(false, false, false, true));
		buttonLayout.setHeight(null);
		buttonLayout.setWidth("100%");
		buttonLayoutSection.setSpacing(true);
		buttonLayoutSection.addComponent(buttonLayout);
		buttonLayoutSection.setMargin(new MarginInfo(true, false, true, false));
		buttonLayout.addComponent(this.export);

		tableSection.addComponent(buttonLayoutSection);

		return tableSection;
	}

	void updateContentTable() {
		// Nothing to do here at the moment
		// table is already set in setdataresource
	}

	public void setResourceUrl(String resourceurl) {
		this.resourceUrl = resourceurl;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public String getNavigatorLabel() {
		return navigateToLabel;
	}

	/**
	 * sets the ContainerDataSource for showing it in a table and the id of the
	 * current Openbis Experiment. The id is shown in the caption.
	 * 
	 * @param experimentBean
	 */
	public void setContainerDataSource(ExperimentBean experimentBean) {
		this.currentBean = experimentBean;
		BeanItemContainer<SampleBean> samples = experimentBean.getSamples();

		// samples.getBeanType().equals(NMRSample);

		for (Iterator<SampleBean> i = samples.getItemIds().iterator(); i.hasNext();) {
			// Get the current item identifier, which is an integer.
//			int iid = (Integer) i.next();
			SampleBean item = i.next();
			// Now get the actual item from the table.
			//SampleBean item = samples.getItem(iid).getBean();
			if (item.getType().equals("Q_CFH_NMR_RUN")) {
				NMRSampleBean newItem = new NMRSampleBean();

				newItem.setCode(item.getCode());
				newItem.setType(item.getType());
				newItem.setId(item.getId());
				
				newItem.setAdditionalInfo(item.getProperties().get("Q_ADDITIONAL_INFO"));
				newItem.setExternalDB(item.getProperties().get(
						"Q_EXTERNALDB_ID"));
				newItem.setSecondaryName(item.getProperties().get(
						"Q_SECONDARY_NAME"));
				newItem.setProperties(item.getProperties());

				newItem.setExpType(item.getProperties().get("NMR_EXP_TYPES"));
				newItem.setExpDesc(item.getProperties().get("NMR_EXP_DESC"));
				newItem.setSolvent(item.getProperties().get("NMR_SOLVENT"));
				newItem.setConcentration(item.getProperties().get(
						"NMR_CONCENTRATION"));
				newItem.setVolume(item.getProperties().get("NMR_VOLUME"));
				newItem.setpH(item.getProperties().get("NMR_PH"));
				newItem.setBuffer(item.getProperties().get("NMR_BUFFER"));
				newItem.setDate(item.getProperties().get("NMR_DATE"));
				newItem.setStorage(item.getProperties().get("NMR_STORAGE"));
				newItem.setQuantitation(item.getProperties().get(
						"NMR_QUANTITATION"));
				newItem.setSampleDesc(item.getProperties().get(
						"NMR_SAMPLE_DESC"));
				newItem.setNmrSpec(item.getProperties().get("NMR_SPECTROMETER"));
				newItem.setProbe(item.getProperties().get("NMR_PROBE"));
				newItem.setTemperature(item.getProperties().get(
						"NMR_TEMPERATURE"));
				newItem.setProbeDetails(item.getProperties().get(
						"NMR_PROBE_DETAILS"));
				newItem.setSolventDetails(item.getProperties().get(
						"NMR_SOLVENT_DETAILS"));
				newItem.setStorageDetails(item.getProperties().get(
						"NMR_STORAGE_DETAILS"));
				
				newItem.setPrettyType(item.getPrettyType());
				newItem.setParents(item.getParents());
				newItem.setChildren(item.getChildren());
				newItem.setDatasets(item.getDatasets());
				newItem.setLastChangedDataset(item.getLastChangedDataset());
				newItem.setProperties(item.getProperties());
				newItem.setTypeLabels(item.getTypeLabels());

				nmrSamples.addBean(newItem);
			}
		}

		if(nmrSamples.size() >0){
			this.table.setContainerDataSource(nmrSamples);
			this.table.setVisibleColumns(new Object[] { "code","secondaryName", "prettyType", "expDesc", "expType", "additionalInfo", "buffer",
					"volume", "temperature", "concentration", "pH", 
					"quantitation", "sampleDesc","solvent", "solventDetails", "storage","storageDetails", "nmrSpec", "probe", "probeDetails","date" });
		}else{
			this.table.setContainerDataSource(experimentBean.getSamples());
			this.table.setVisibleColumns(new Object[] { "code", "prettyType" });
		}
		
		

		int rowNumber = this.table.size();

		if (rowNumber == 0) {
			this.table.setVisible(false);
		} else {
			this.table.setVisible(true);
			this.table.setPageLength(Math.max(3, Math.min(rowNumber, 10)));
		}

	}

	private void tableClickChangeTreeView() {
		table.setSelectable(true);
		table.setImmediate(true);
		this.table.addValueChangeListener(new ViewTablesClickListener(table,
				SampleView.navigateToLabel));
	}

	private FilterTable buildFilterTable() {
		FilterTable filterTable = new FilterTable();
		filterTable.setSizeFull();

		filterTable.setFilterDecorator(new DatasetViewFilterDecorator());
		filterTable.setFilterGenerator(new DatasetViewFilterGenerator());

		filterTable.setFilterBarVisible(true);

		filterTable.setSelectable(true);
		filterTable.setImmediate(true);

		filterTable.setRowHeaderMode(RowHeaderMode.INDEX);

		filterTable.setColumnCollapsingAllowed(true);

		filterTable.setColumnReorderingAllowed(true);

		filterTable.setColumnHeader("code", "QBiC ID");
		filterTable.setColumnHeader("prettyType", "Sample Type");
		filterTable.setColumnHeader("secondaryName", "Secondary Name");
		filterTable.setColumnHeader("expDesc", "Experiment Description");
		filterTable.setColumnHeader("expType", "Experiment Type");
		filterTable.setColumnHeader("additionalInfo","Exp. Details");
		filterTable.setColumnHeader("buffer","Buffer");
		filterTable.setColumnHeader("volume","Volume");
		filterTable.setColumnHeader("temperature","Temperature [K]");
		filterTable.setColumnHeader("concentration","Concentration");
		filterTable.setColumnHeader("pH","pH");
		
		filterTable.setColumnHeader("quantitation","Quantitation");
		filterTable.setColumnHeader("sampleDesc","Sample Description");
		filterTable.setColumnHeader("solvent","Solvent");
		filterTable.setColumnHeader("solventDetails","Solvent Details");
		filterTable.setColumnHeader("storage","Storage");
		filterTable.setColumnHeader("storageDetails","Storage Details");
		filterTable.setColumnHeader("nmrSpec","Spectrometer");
		filterTable.setColumnHeader("probe","Probe");
		filterTable.setColumnHeader("probeDetails","Probe Details");
		filterTable.setColumnHeader("date", "Date");
		

		return filterTable;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		String currentValue = event.getParameters();
		// TODO updateContent only if currentExperiment is not equal to
		// newExperiment
		this.table.unselect(this.table.getValue());
		this.setContainerDataSource(datahandler.getExperiment2(currentValue));
		updateContent();
	}

	public ExperimentBean getCurrentBean() {
		return currentBean;
	}

	/**
	 * Enables or disables the component. The user can not interact disabled
	 * components, which are shown with a style that indicates the status,
	 * usually shaded in light gray color. Components are enabled by default.
	 */
	public void setEnabled(boolean enabled) {
		this.export.setEnabled(enabled);
		this.table.setEnabled(enabled);
		// this.createBarcodesMenuItem.getParent().setEnabled(false);
		// this.downloadCompleteProjectMenuItem.getParent().setEnabled(false);
		// this.toolbar.setEnabled(enabled);
	}

}
