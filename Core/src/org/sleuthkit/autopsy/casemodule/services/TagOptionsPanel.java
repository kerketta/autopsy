/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011-2018 Basis Technology Corp.
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
package org.sleuthkit.autopsy.casemodule.services;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;
import org.sleuthkit.autopsy.corecomponents.OptionsPanel;
import org.sleuthkit.autopsy.ingest.IngestManager;
import org.sleuthkit.datamodel.TagName;
import org.sleuthkit.datamodel.TskData;
import org.sleuthkit.autopsy.coreutils.Logger;

/**
 * A panel to allow the user to create and delete custom tag types.
 */
@SuppressWarnings("PMD.SingularField") // UI widgets cause lots of false positives
final class TagOptionsPanel extends javax.swing.JPanel implements OptionsPanel {

    private static final long serialVersionUID = 1L;
    private static final TagName.HTML_COLOR DEFAULT_COLOR = TagName.HTML_COLOR.NONE;
    private final DefaultListModel<TagNameDefinition> tagTypesListModel;
    private Set<TagNameDefinition> tagTypes;
    private IngestJobEventPropertyChangeListener ingestJobEventsListener;
    private Set<String> updatedStatusTags;

    /**
     * Creates new form TagOptionsPanel
     */
    TagOptionsPanel() {
        tagTypesListModel = new DefaultListModel<>();
        tagTypes = new TreeSet<>(TagNameDefinition.getTagNameDefinitions());
        updatedStatusTags = new HashSet<>();
        initComponents();
        customizeComponents();
    }

    @Messages({"TagOptionsPanel.panelDescriptionTextArea.text=Create and manage tags. "
        + "Tags can be applied to files and results in the case. Notable tags will cause "
        + "items tagged with them to be flagged as notable when using a central repository. "
        + "Changing the status of a tag will only effect items in the current case.",
        "TagOptionsPanel.ingestRunningWarningLabel.text=Cannot make changes to existing tags when ingest is running!",
        "TagOptionsPanel.descriptionLabel.text=Tag Description:",
        "TagOptionsPanel.notableYesOrNoLabel.text=",
        "TagOptionsPanel.isNotableLabel.text=Tag indicates item is notable: ",
        "TagOptionsPanel.editTagNameButton.text=Edit Tag"})

    private void customizeComponents() {
        tagNamesList.setModel(tagTypesListModel);
        tagNamesList.addListSelectionListener((ListSelectionEvent event) -> {
            updatePanel();
        });
        addIngestJobEventsListener();
    }

    /**
     * Add a property change listener that listens to ingest job events to
     * disable the buttons on the panel if ingest is running. This is done to
     * prevent changes to user-defined types while the type definitions are in
     * use.
     */
    private void addIngestJobEventsListener() {
        ingestJobEventsListener = new IngestJobEventPropertyChangeListener();
        IngestManager.getInstance().addIngestJobEventListener(ingestJobEventsListener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        modifyTagTypesListPanel = new javax.swing.JPanel();
        tagTypesListLabel = new javax.swing.JLabel();
        TagNameScrollPane = new javax.swing.JScrollPane();
        tagNamesList = new javax.swing.JList<>();
        newTagNameButton = new javax.swing.JButton();
        deleteTagNameButton = new javax.swing.JButton();
        editTagNameButton = new javax.swing.JButton();
        panelDescriptionScrollPane = new javax.swing.JScrollPane();
        panelDescriptionTextArea = new javax.swing.JTextArea();
        tagTypesAdditionalPanel = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        isNotableLabel = new javax.swing.JLabel();
        notableYesOrNoLabel = new javax.swing.JLabel();
        ingestRunningWarningLabel = new javax.swing.JLabel();

        jPanel1.setPreferredSize(new java.awt.Dimension(750, 490));

        jScrollPane2.setPreferredSize(new java.awt.Dimension(750, 490));

        jSplitPane1.setDividerLocation(450);
        jSplitPane1.setDividerSize(1);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(748, 488));

        org.openide.awt.Mnemonics.setLocalizedText(tagTypesListLabel, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.tagTypesListLabel.text")); // NOI18N

        tagNamesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TagNameScrollPane.setViewportView(tagNamesList);

        newTagNameButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/images/add-tag.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(newTagNameButton, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.newTagNameButton.text")); // NOI18N
        newTagNameButton.setMaximumSize(new java.awt.Dimension(111, 25));
        newTagNameButton.setMinimumSize(new java.awt.Dimension(111, 25));
        newTagNameButton.setPreferredSize(new java.awt.Dimension(111, 25));
        newTagNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTagNameButtonActionPerformed(evt);
            }
        });

        deleteTagNameButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/images/delete-tag.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(deleteTagNameButton, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.deleteTagNameButton.text")); // NOI18N
        deleteTagNameButton.setMaximumSize(new java.awt.Dimension(111, 25));
        deleteTagNameButton.setMinimumSize(new java.awt.Dimension(111, 25));
        deleteTagNameButton.setPreferredSize(new java.awt.Dimension(111, 25));
        deleteTagNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTagNameButtonActionPerformed(evt);
            }
        });

        editTagNameButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/images/edit-tag.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(editTagNameButton, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.editTagNameButton.text")); // NOI18N
        editTagNameButton.setMaximumSize(new java.awt.Dimension(111, 25));
        editTagNameButton.setMinimumSize(new java.awt.Dimension(111, 25));
        editTagNameButton.setPreferredSize(new java.awt.Dimension(111, 25));
        editTagNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTagNameButtonActionPerformed(evt);
            }
        });

        panelDescriptionTextArea.setEditable(false);
        panelDescriptionTextArea.setBackground(new java.awt.Color(240, 240, 240));
        panelDescriptionTextArea.setColumns(20);
        panelDescriptionTextArea.setLineWrap(true);
        panelDescriptionTextArea.setRows(3);
        panelDescriptionTextArea.setText(org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.panelDescriptionTextArea.text")); // NOI18N
        panelDescriptionTextArea.setWrapStyleWord(true);
        panelDescriptionTextArea.setFocusable(false);
        panelDescriptionTextArea.setOpaque(false);
        panelDescriptionScrollPane.setViewportView(panelDescriptionTextArea);

        javax.swing.GroupLayout modifyTagTypesListPanelLayout = new javax.swing.GroupLayout(modifyTagTypesListPanel);
        modifyTagTypesListPanel.setLayout(modifyTagTypesListPanelLayout);
        modifyTagTypesListPanelLayout.setHorizontalGroup(
            modifyTagTypesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modifyTagTypesListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modifyTagTypesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tagTypesListLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(modifyTagTypesListPanelLayout.createSequentialGroup()
                        .addComponent(newTagNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editTagNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteTagNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(TagNameScrollPane)
                    .addComponent(panelDescriptionScrollPane))
                .addContainerGap())
        );

        modifyTagTypesListPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {deleteTagNameButton, editTagNameButton, newTagNameButton});

        modifyTagTypesListPanelLayout.setVerticalGroup(
            modifyTagTypesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modifyTagTypesListPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(panelDescriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tagTypesListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TagNameScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(modifyTagTypesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newTagNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editTagNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteTagNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        modifyTagTypesListPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {deleteTagNameButton, editTagNameButton, newTagNameButton});

        jSplitPane1.setLeftComponent(modifyTagTypesListPanel);

        org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.descriptionLabel.text")); // NOI18N

        descriptionTextArea.setEditable(false);
        descriptionTextArea.setBackground(new java.awt.Color(240, 240, 240));
        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setFocusable(false);
        descriptionTextArea.setOpaque(false);
        descriptionScrollPane.setViewportView(descriptionTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(isNotableLabel, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.isNotableLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(notableYesOrNoLabel, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.notableYesOrNoLabel.text")); // NOI18N

        ingestRunningWarningLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/modules/filetypeid/warning16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(ingestRunningWarningLabel, org.openide.util.NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.ingestRunningWarningLabel.text")); // NOI18N

        javax.swing.GroupLayout tagTypesAdditionalPanelLayout = new javax.swing.GroupLayout(tagTypesAdditionalPanel);
        tagTypesAdditionalPanel.setLayout(tagTypesAdditionalPanelLayout);
        tagTypesAdditionalPanelLayout.setHorizontalGroup(
            tagTypesAdditionalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tagTypesAdditionalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tagTypesAdditionalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionScrollPane)
                    .addGroup(tagTypesAdditionalPanelLayout.createSequentialGroup()
                        .addGroup(tagTypesAdditionalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionLabel)
                            .addGroup(tagTypesAdditionalPanelLayout.createSequentialGroup()
                                .addComponent(isNotableLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(notableYesOrNoLabel))
                            .addComponent(ingestRunningWarningLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tagTypesAdditionalPanelLayout.setVerticalGroup(
            tagTypesAdditionalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tagTypesAdditionalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(descriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tagTypesAdditionalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isNotableLabel)
                    .addComponent(notableYesOrNoLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 292, Short.MAX_VALUE)
                .addComponent(ingestRunningWarningLabel)
                .addGap(31, 31, 31))
        );

        jSplitPane1.setRightComponent(tagTypesAdditionalPanel);

        jScrollPane2.setViewportView(jSplitPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Messages({"TagOptionsPanel.TagNameDialog.tagNameAlreadyExists.message=Tag name must be unique. A tag with this name already exists.",
        "TagOptionsPanel.TagNameDialog.tagNameAlreadyExists.title=Duplicate Tag Name"})

    private void newTagNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTagNameButtonActionPerformed
        TagNameDialog dialog = new TagNameDialog();
        TagNameDialog.BUTTON_PRESSED result = dialog.getResult();
        if (result == TagNameDialog.BUTTON_PRESSED.OK) {
            TskData.FileKnown status = dialog.isTagNotable() ? TskData.FileKnown.BAD : TskData.FileKnown.UNKNOWN;
            TagNameDefinition newTagType = new TagNameDefinition(dialog.getTagName(), dialog.getTagDesciption(), DEFAULT_COLOR, status);
            /*
             * If tag name already exists, don't add the tag name.
             */
            if (!tagTypes.contains(newTagType)) {
                tagTypes.add(newTagType);
                updateTagNamesListModel();
                tagNamesList.setSelectedValue(newTagType, true);
                updatePanel();
                firePropertyChange(OptionsPanelController.PROP_CHANGED, null, null);
            } else {
                JOptionPane.showMessageDialog(this,
                        NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.TagNameDialog.tagNameAlreadyExists.message"),
                        NbBundle.getMessage(TagOptionsPanel.class, "TagOptionsPanel.TagNameDialog.tagNameAlreadyExists.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_newTagNameButtonActionPerformed

    private void deleteTagNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTagNameButtonActionPerformed
        TagNameDefinition tagName = tagNamesList.getSelectedValue();
        tagTypes.remove(tagName);
        updateTagNamesListModel();
        updatePanel();
        firePropertyChange(OptionsPanelController.PROP_CHANGED, null, null);

    }//GEN-LAST:event_deleteTagNameButtonActionPerformed

    private void editTagNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTagNameButtonActionPerformed
        TagNameDefinition originalTagName = tagNamesList.getSelectedValue();
        TagNameDialog dialog = new TagNameDialog(originalTagName);
        TagNameDialog.BUTTON_PRESSED result = dialog.getResult();
        if (result == TagNameDialog.BUTTON_PRESSED.OK) {
            TskData.FileKnown status = dialog.isTagNotable() ? TskData.FileKnown.BAD : TskData.FileKnown.UNKNOWN;
            TagNameDefinition newTagType = new TagNameDefinition(dialog.getTagName(), dialog.getTagDesciption(), DEFAULT_COLOR, status);
            /*
             * If tag name already exists, don't add the tag name.
             */
            tagTypes.remove(originalTagName);
            tagTypes.add(newTagType);
            updateTagNamesListModel();
            tagNamesList.setSelectedValue(newTagType, true);
            updatePanel();
            firePropertyChange(OptionsPanelController.PROP_CHANGED, null, null);
            if (originalTagName.getKnownStatus() != newTagType.getKnownStatus() && Case.isCaseOpen()) {
                updatedStatusTags.add(newTagType.getDisplayName());
            }
        }
    }//GEN-LAST:event_editTagNameButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane TagNameScrollPane;
    private javax.swing.JButton deleteTagNameButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton editTagNameButton;
    private javax.swing.JLabel ingestRunningWarningLabel;
    private javax.swing.JLabel isNotableLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel modifyTagTypesListPanel;
    private javax.swing.JButton newTagNameButton;
    private javax.swing.JLabel notableYesOrNoLabel;
    private javax.swing.JScrollPane panelDescriptionScrollPane;
    private javax.swing.JTextArea panelDescriptionTextArea;
    private javax.swing.JList<org.sleuthkit.autopsy.casemodule.services.TagNameDefinition> tagNamesList;
    private javax.swing.JPanel tagTypesAdditionalPanel;
    private javax.swing.JLabel tagTypesListLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * Updates the tag names model for the tag names list component.
     */
    private void updateTagNamesListModel() {
        tagTypesListModel.clear();
        for (TagNameDefinition tagName : tagTypes) {
            tagTypesListModel.addElement(tagName);
        }
    }

    /**
     * Loads the stored custom tag types.
     */
    @Override
    public void load() {
        tagTypes = new TreeSet<>(TagNameDefinition.getTagNameDefinitions());
        updateTagNamesListModel();
        updatePanel();
    }

    /**
     * Stores the custom tag types.
     */
    @Override
    public void store() {
        TagNameDefinition.setTagNameDefinitions(tagTypes);
        sendStatusChangedEvents();
    }

    void cancelChanges() {
        updatedStatusTags.clear();
    }

    private void sendStatusChangedEvents() {
        for (String modifiedTagDisplayName : updatedStatusTags) {
            //if  user closes their case after options have been changed but before application of them is complete don't notify
            try {
                Case.getCurrentCaseThrows().notifyTagDefinitionChanged(modifiedTagDisplayName);
            } catch (NoCurrentCaseException ex) {
                Logger.getLogger(TagOptionsPanel.class.getName()).log(Level.SEVERE, "Exception while getting open case.", ex); //NON-NLS
            }
        }
        updatedStatusTags.clear();
    }

    /**
     * Enables the button components based on the state of the tag types list
     * component.
     */
    private void updatePanel() {
        boolean ingestIsRunning = IngestManager.getInstance().isIngestRunning();
        /*
         * Only enable the delete button when there is a tag type selected in
         * the tag types JList.
         */
        ingestRunningWarningLabel.setVisible(ingestIsRunning);
        boolean isSelected = tagNamesList.getSelectedIndex() != -1;
        boolean enableEdit = !ingestIsRunning && isSelected;
        editTagNameButton.setEnabled(enableEdit);
        boolean enableDelete = enableEdit && !TagNameDefinition.getStandardTagNames().contains(tagNamesList.getSelectedValue().getDisplayName());
        deleteTagNameButton.setEnabled(enableDelete);
        if (isSelected) {
            descriptionTextArea.setText(tagNamesList.getSelectedValue().getDescription());
            if (tagNamesList.getSelectedValue().getKnownStatus() == TskData.FileKnown.BAD) {
                notableYesOrNoLabel.setText("Yes");
            } else {
                notableYesOrNoLabel.setText("No");
            }
        } else {
            descriptionTextArea.setText("");
            notableYesOrNoLabel.setText("");
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        IngestManager.getInstance().removeIngestJobEventListener(ingestJobEventsListener);
        super.finalize();
    }

    /**
     * A property change listener that listens to ingest job events.
     */
    private class IngestJobEventPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updatePanel();
                }
            });
        }
    }
}
