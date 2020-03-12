package life.qbic.projectbrowser.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tepi.filtertable.FilterTable;

import life.qbic.portal.utils.ConfigurationManager;
import life.qbic.projectbrowser.controllers.DataHandler;
import life.qbic.projectbrowser.controllers.State;
import life.qbic.projectbrowser.controllers.WorkflowViewController;
import life.qbic.projectbrowser.helpers.GridFunctions;
import life.qbic.projectbrowser.helpers.Utils;
import life.qbic.projectbrowser.model.AChOverviewBean;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;

public class AChView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1148889905515676510L;
	private static final Logger LOG = LogManager.getLogger(AChView.class);

	public final static String navigateToLabel = "ACh";

	private String caption;
	private FilterTable table;
	private Grid projectGrid;
	private VerticalLayout buttonLayoutSection = new VerticalLayout();

	private BeanItemContainer<AChOverviewBean> currentBean;

	private String header;

	private Button export = new Button("Export as TSV");

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	private DataHandler datahandler;

	private String user;

	public AChView(DataHandler datahandler, String caption, String user,
			State state, String resUrl, WorkflowViewController controller,
			ConfigurationManager manager) {
		new VerticalLayout();
		this.projectGrid = new Grid();
		this.datahandler = datahandler;
		this.user = user;
	}

	public void setSizeFull() {

	}

	/**
	 * sets the ContainerDataSource of this view. Should usually contain project
	 * information. Caption is caption.
	 * 
	 * @param achBean
	 * @param newCaption
	 */
	public void setContainerDataSource(
			BeanItemContainer<AChOverviewBean> achBean, String newCaption) {

		caption = newCaption;
		currentBean = achBean;
		currentBean.size();
		projectGrid = new Grid();

		GeneratedPropertyContainer gpcProjects = new GeneratedPropertyContainer(
				achBean);
		
		projectGrid.setContainerDataSource(gpcProjects);

		projectGrid.setHeightMode(HeightMode.ROW);
		projectGrid.setHeightByRows(20);
		projectGrid.getColumn("firstSample").setWidth(120);
		projectGrid.getColumn("lastSample").setWidth(120);
		projectGrid.getColumn("numberOfSamples").setHeaderCaption("# of Samples").setWidth(120);
		projectGrid.getColumn("code").setHeaderCaption("Sub-Project")
				.setWidth(170);

		projectGrid.getColumn("contactPerson").setHeaderCaption(
				"Contact Person");

		projectGrid.getColumn("projectManager").setHeaderCaption(
				"Project Manager");

		projectGrid.setColumnOrder("firstSample", "lastSample",
				"numberOfSamples", "sampleExtraction", "sampleParameter",
				"sampleDevice", "code", "contactPerson", "projectManager");

		projectGrid.setResponsive(true);

		GridFunctions.addColumnFilters(projectGrid, gpcProjects);

	}

	private void setExportButton() {
		buttonLayoutSection.removeAllComponents();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setHeight(null);
		buttonLayout.setWidth("100%");
		buttonLayoutSection.addComponent(buttonLayout);

		buttonLayout.addComponent(this.export);

		StreamResource sr = Utils.getTSVStream(
				Utils.containerToString(currentBean), "ach_project_overview");
		FileDownloader fileDownloader = new FileDownloader(sr);
		fileDownloader.extend(this.export);
	}

	/**
	 * updates view, if height, width or the browser changes.
	 * 
	 * @param browserHeight
	 * @param browserWidth
	 * @param browser
	 */
	public void updateView(int browserHeight, int browserWidth,
			WebBrowser browser) {
		// setWidth((browserWidth * 0.85f), Unit.PIXELS);
	}

	void buildLayout(int browserHeight, int browserWidth, WebBrowser browser) {
		// clean up first
		removeAllComponents();
		setSpacing(true);

		addComponent(projectGrid);
		addComponent(buttonLayoutSection);
		setExportButton();

		setCaption("Sub-Projects");
		setIcon(FontAwesome.TABLE);

		projectGrid.setWidth(100, Unit.PERCENTAGE);
		projectGrid.setSelectionMode(SelectionMode.SINGLE);
		projectGrid.setResponsive(true);

		export.setIcon(FontAwesome.DOWNLOAD);

		setWidth(100, Unit.PERCENTAGE);
		setResponsive(true);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		try {
			loadProjects();
			int height = event.getNavigator().getUI().getPage()
					.getBrowserWindowHeight();
			int width = event.getNavigator().getUI().getPage()
					.getBrowserWindowWidth();
			buildLayout(height, width, event.getNavigator().getUI().getPage()
					.getWebBrowser());
		} catch (Exception e) {
			LOG.error(
					String.format("failed to load projects for user %s", user),
					e);
			removeAllComponents();
			Label error = new Label(
					"Connection to database interrupted. Please try again later.");
			error.addStyleName(ValoTheme.LABEL_FAILURE);
			error.addStyleName(ValoTheme.LABEL_HUGE);
			addComponent(error);
			setComponentAlignment(error, Alignment.MIDDLE_CENTER);
		}
	}

	/**
	 * Enables or disables the component. The user can not interact disabled
	 * components, which are shown with a style that indicates the status,
	 * usually shaded in light gray color. Components are enabled by default.
	 */
	public void setEnabled(boolean enabled) {
		this.export.setEnabled(enabled);
		this.table.setEnabled(enabled);
	}

	/**
	 * refresh all openbis project for current user. Basically currentBean is
	 * overwritten
	 */
	public void loadProjects() {
		BeanItemContainer<AChOverviewBean> projectContainer = new BeanItemContainer<AChOverviewBean>(
				AChOverviewBean.class);

		LOG.info("Loading active ach projects...");
		List<String> activeProjects = new ArrayList<String>();

		activeProjects = datahandler.getDatabaseManager()
				.getActiveProjectsFromMod3();

		List<Project> projects = new ArrayList<Project>();

		// only get active Analytical chemistry projects
		for (String id : activeProjects) {
			Project project = datahandler.getOpenBisClient()
					.getProjectByIdentifier(id);
			projects.add(project);
		}

		LOG.info("Loading all active projects...done.");
		LOG.info("Number of active projects is " + projects.size());
		Boolean isFirstSample = true;
		String device = "";
		String digestion = "";
		String parameter = "";
		String firstSample = "";
		String lastSample = "";
		int numberOfSamples = 0;

		for (Project project : projects) {
			String projectIdentifier = project.getIdentifier();
			String projectCode = project.getCode();
			List<List<Sample>> allProjectSamples = datahandler
					.getOpenBisClient().getSamplesOfProjectByExpType(
							projectIdentifier, "Q_CFH_ELEMENT");

			for (List<Sample> exp : allProjectSamples) {
				for (Sample s : exp) {
					Map<String, String> sampleProperties = s.getProperties();
					numberOfSamples++;
					device = sampleProperties.get("Q_CFH_DEVICES");
					digestion = sampleProperties.get("Q_CFH_DIGESTION");

					if (digestion == null) {
						digestion = "";
					}
					parameter = sampleProperties.get("Q_ELEMENT_DESC");

					if (isFirstSample) {
						isFirstSample = false;
						firstSample = sampleProperties.get("Q_SECONDARY_NAME");

					}
					lastSample = sampleProperties.get("Q_SECONDARY_NAME");
				}

				// now we know next experiment will be called so write all
				// samples
				String cp = datahandler.getDatabaseManager()
						.getContactForProject(projectIdentifier);

				String pm = datahandler.getDatabaseManager().getProjectManager(
						projectIdentifier);

				AChOverviewBean newProjectBean = new AChOverviewBean(
						projectIdentifier, projectCode);

				if (cp.equals("")) {
					newProjectBean.setContactPerson("n/a");
				} else {
					newProjectBean.setContactPerson(cp);
				}

				if (pm.equals("")) {
					newProjectBean.setProjectManager("n/a");
				} else {
					newProjectBean.setProjectManager(pm);
				}

				newProjectBean.setSampleParameter(parameter);
				newProjectBean.setSampleDevice(device);
				newProjectBean.setSampleExtraction(digestion);
				newProjectBean.setFirstSample(firstSample);
				newProjectBean.setLastSample(lastSample);
				newProjectBean.setNumberOfSamples(numberOfSamples);

				projectContainer.addBean(newProjectBean);

				// reset for samples
				isFirstSample = true;
				numberOfSamples = 0;
				firstSample = "";
				lastSample = "";

			}
		}

		if (projectContainer.size() > 0) {
			this.setContainerDataSource(projectContainer, caption);

		}
	}
}