/*
 * Autopsy Forensic Browser
 *
 * Copyright 2020 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.datasourcesummary.ui;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.datasourcesummary.uiutils.DataFetchResult;
import org.sleuthkit.autopsy.datasourcesummary.uiutils.DataFetchWorker;
import org.sleuthkit.autopsy.datasourcesummary.uiutils.DataFetchWorker.DataFetchComponents;
import org.sleuthkit.autopsy.datasourcesummary.uiutils.EventUpdateHandler;
import org.sleuthkit.autopsy.datasourcesummary.uiutils.LoadableComponent;
import org.sleuthkit.autopsy.datasourcesummary.uiutils.SwingWorkerSequentialExecutor;
import org.sleuthkit.autopsy.datasourcesummary.uiutils.UpdateGovernor;
import org.sleuthkit.autopsy.ingest.ModuleContentEvent;
import org.sleuthkit.autopsy.ingest.ModuleDataEvent;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.DataSource;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * Base class from which other tabs in data source summary derive.
 */
abstract class BaseDataSourceSummaryPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(BaseDataSourceSummaryPanel.class.getName());

    private final SwingWorkerSequentialExecutor executor = new SwingWorkerSequentialExecutor();
    private final EventUpdateHandler updateHandler;
    private final List<UpdateGovernor> governors;

    private DataSource dataSource;

    /**
     * In charge of determining when an update is necessary. In instances where
     * a datasource is concerned, this checks to see if the datasource is the
     * current datasource. Otherwise, it delegates to the underlying governors
     * for the object.
     */
    private final UpdateGovernor updateGovernor = new UpdateGovernor() {
        /**
         * Checks to see if artifact is from a datasource.
         *
         * @param art The artifact.
         * @param ds  The datasource.
         *
         * @return True if in datasource; false if not or exception.
         */
        private boolean isInDataSource(BlackboardArtifact art, DataSource ds) {
            try {

                return (art.getDataSource() != null && art.getDataSource().getId() == ds.getId());
            } catch (TskCoreException ex) {
                logger.log(Level.WARNING, "There was an error fetching datasource for artifact.", ex);
                return false;
            }
        }

        @Override
        public boolean isRefreshRequired(ModuleDataEvent evt) {
            DataSource ds = getDataSource();
            // make sure there is an event.
            if (ds == null || evt == null) {
                return false;
            }

            //if there are no artifacts with matching datasource, return
            // if no artifacts are present, pass it on just in case there was something wrong with ModuleDataEvent
            if (evt.getArtifacts() != null
                    && !evt.getArtifacts().isEmpty()
                    && !evt.getArtifacts().stream().anyMatch((art) -> isInDataSource(art, ds))) {
                return false;
            }

            // otherwise, see if there is something that wants updates
            for (UpdateGovernor governor : governors) {
                if (governor.isRefreshRequired(evt)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean isRefreshRequired(ModuleContentEvent evt) {
            DataSource ds = getDataSource();
            // make sure there is an event.
            if (ds == null || evt == null) {
                return false;
            }

            try {
                // if the underlying content has a datasource and that datasource != the 
                // current datasource, return false
                if (evt.getSource() instanceof Content
                        && ((Content) evt.getSource()).getDataSource() != null
                        && ((Content) evt.getSource()).getDataSource().getId() != ds.getId()) {
                    return false;
                }
            } catch (TskCoreException ex) {
                // on an exception, keep going for tolerance sake
                logger.log(Level.WARNING, "There was an error fetching datasource for content.", ex);
            }

            for (UpdateGovernor governor : governors) {
                if (governor.isRefreshRequired(evt)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean isRefreshRequiredForCaseEvent(PropertyChangeEvent evt) {
            for (UpdateGovernor governor : governors) {
                if (governor.isRefreshRequiredForCaseEvent(evt)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public Set<Case.Events> getCaseEventUpdates() {
            // return the union of all case events sets from delegates.
            return governors.stream()
                    .filter(governor -> governor.getCaseEventUpdates() != null)
                    .flatMap(governor -> governor.getCaseEventUpdates().stream())
                    .collect(Collectors.toSet());
        }
    };

    /**
     * Main constructor.
     *
     * @param governors The items governing when this panel should receive
     *                  updates.
     */
    protected BaseDataSourceSummaryPanel(UpdateGovernor... governors) {
        this.governors = (governors == null) ? Collections.emptyList() : Arrays.asList(governors);
        this.updateHandler = new EventUpdateHandler(this::onRefresh, updateGovernor);
        this.updateHandler.register();
    }

    /**
     * Closes listeners and resources.
     */
    public void close() {
        executor.cancelRunning();
        updateHandler.unregister();
    }

    /**
     * Sets datasource to visualize in the panel.
     *
     * @param dataSource The datasource to use in this panel.
     */
    synchronized void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.executor.cancelRunning();
        onNewDataSource(this.dataSource);
    }

    /**
     * @return The current data source.
     */
    protected synchronized DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Submits the following swing workers for execution in sequential order. If
     * there are any previous workers, those workers are cancelled.
     *
     * @param workers The workers to submit for execution.
     */
    protected void submit(List<? extends SwingWorker<?, ?>> workers) {
        executor.submit(workers);
    }

    /**
     * When a data source is updated this function is triggered.
     *
     * @param dataSource The data source.
     */
    synchronized void onRefresh() {
        // trigger on new data source with the current data source
        fetchInformation(this.dataSource);
    }

    /**
     * Action that is called when information needs to be retrieved (on refresh
     * or on new data source).
     *
     * @param dataSource The datasource to fetch information about.
     */
    protected abstract void fetchInformation(DataSource dataSource);

    /**
     * Utility method to be called when solely updating information (not showing
     * a loading screen) that creates swing workers from the data source
     * argument and data fetch components and then submits them to run.
     *
     * @param dataFetchComponents The components to be run.
     * @param dataSource          The data source argument.
     */
    protected void fetchInformation(List<DataFetchComponents<DataSource, ?>> dataFetchComponents, DataSource dataSource) {
        // create swing workers to run for each loadable item
        List<DataFetchWorker<?, ?>> workers = dataFetchComponents
                .stream()
                .map((components) -> new DataFetchWorker<>(components, dataSource))
                .collect(Collectors.toList());

        // submit swing workers to run
        if (!workers.isEmpty()) {
            submit(workers);
        }
    }

    /**
     * When a new dataSource is added, this method is called.
     *
     * @param dataSource The new dataSource.
     */
    protected abstract void onNewDataSource(DataSource dataSource);

    /**
     * Utility method that shows a loading screen with loadable components,
     * create swing workers from the datafetch components and data source
     * argument and submits them to be executed.
     *
     * @param dataFetchComponents The components to register.
     * @param loadableComponents  The components to set to a loading screen.
     * @param dataSource          The data source argument.
     */
    protected void onNewDataSource(
            List<DataFetchComponents<DataSource, ?>> dataFetchComponents,
            List<? extends LoadableComponent<?>> loadableComponents,
            DataSource dataSource) {
        // if no data source is present or the case is not open,
        // set results for tables to null.
        if (dataSource == null || !Case.isCaseOpen()) {
            dataFetchComponents.forEach((item) -> item.getResultHandler()
                    .accept(DataFetchResult.getSuccessResult(null)));

        } else {
            // set tables to display loading screen
            loadableComponents.forEach((table) -> table.showDefaultLoadingMessage());

            fetchInformation(dataSource);
        }
    }
}
