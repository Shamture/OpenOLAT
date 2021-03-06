/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.quality.analysis.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.stack.BreadcrumbPanel;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.modules.forms.FiguresBuilder;
import org.olat.modules.forms.SessionFilter;
import org.olat.modules.forms.model.xml.Form;
import org.olat.modules.forms.ui.EvaluationFormReportsController;
import org.olat.modules.forms.ui.ReportSegment;
import org.olat.modules.forms.ui.ReportSegmentEvent;
import org.olat.modules.quality.analysis.AnalysisSearchParameter;
import org.olat.modules.quality.analysis.AnlaysisFigures;
import org.olat.modules.quality.analysis.QualityAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 04.09.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class AnalysisReportController extends BasicController implements FilterableController {
	
	private final VelocityContainer mainVC;
	
	private EvaluationFormReportsController reportsCtrl;

	private final Form form;
	private final String formName;
	private ReportSegment currentSegment;

	@Autowired
	private QualityAnalysisService analysisService;

	public AnalysisReportController(UserRequest ureq, WindowControl wControl, Form form, String formName,
			ReportSegment currentSegment) {
		super(ureq, wControl);
		this.form = form;
		this.formName = formName;
		this.currentSegment = currentSegment;
		mainVC = createVelocityContainer("reporting");
		putInitialPanel(mainVC);
	}

	@Override
	public void setBreadcrumbPanel(BreadcrumbPanel stackPanel) {
		//
	}

	@Override
	public void onFilter(UserRequest ureq, AnalysisSearchParameter searchParams) {
		mainVC.clear();
		SessionFilter filter = analysisService.createSessionFilter(searchParams);
		
		FiguresBuilder figuresBuilder = FiguresBuilder.builder();
		figuresBuilder.addCustomFigure(translate("report.figure.form.name"), formName);
		AnlaysisFigures analyticFigures = analysisService.loadFigures(searchParams);
		figuresBuilder.withNumberOfParticipations(analyticFigures.getParticipationCount());
		figuresBuilder.addCustomFigure(translate("report.figure.number.data.collections"),
				analyticFigures.getDataCollectionCount().toString());
		
		reportsCtrl = new EvaluationFormReportsController(ureq, getWindowControl(), form, filter, currentSegment, null, figuresBuilder.build());
		listenTo(reportsCtrl);
		mainVC.put("report", reportsCtrl.getInitialComponent());
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		//
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if (source == reportsCtrl && event instanceof ReportSegmentEvent) {
			// Save current segment between filter changes
			ReportSegmentEvent rsEvent = (ReportSegmentEvent) event;
			currentSegment = rsEvent.getSegment();
			fireEvent(ureq, event);
		}
		super.event(ureq, source, event);
	}

	@Override
	protected void doDispose() {
		//
	}


}
