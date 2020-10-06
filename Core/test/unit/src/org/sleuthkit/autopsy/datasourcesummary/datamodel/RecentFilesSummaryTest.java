/*
 * Autopsy Forensic Browser
 *
 * Copyright 2020 Basis Technology Corp. Contact: carrier <at> sleuthkit <dot>
 * org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.sleuthkit.autopsy.datasourcesummary.datamodel;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sleuthkit.autopsy.datasourcesummary.datamodel.DataSourceSummaryMockUtils.getArtifactsTSKMock;
import org.sleuthkit.autopsy.datasourcesummary.datamodel.RecentFilesSummary.RecentAttachmentDetails;
import org.sleuthkit.autopsy.datasourcesummary.datamodel.RecentFilesSummary.RecentDownloadDetails;
import org.sleuthkit.autopsy.datasourcesummary.datamodel.RecentFilesSummary.RecentFileDetails;
import org.sleuthkit.autopsy.datasourcesummary.datamodel.SleuthkitCaseProvider.SleuthkitCaseProviderException;
import org.sleuthkit.autopsy.testutils.RandomizationUtils;
import org.sleuthkit.autopsy.testutils.TskMockUtils;
import org.sleuthkit.datamodel.Blackboard;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardArtifact.ARTIFACT_TYPE;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.BlackboardAttribute.ATTRIBUTE_TYPE;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.DataSource;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * Tests for RecentFilesSummaryTest
 */
public class RecentFilesSummaryTest {

    private interface RecentFilesMethod<T> {

        List<T> fetch(RecentFilesSummary recentFilesSummary, DataSource dataSource, int count)
                throws SleuthkitCaseProviderException, TskCoreException;
    }

    private static final RecentFilesMethod<RecentFileDetails> RECENT_DOCS_FUNCT
            = (summary, dataSource, count) -> summary.getRecentlyOpenedDocuments(dataSource, count);

    private static final RecentFilesMethod<RecentDownloadDetails> RECENT_DOWNLOAD_FUNCT
            = (summary, dataSource, count) -> summary.getRecentDownloads(dataSource, count);

    private static final RecentFilesMethod<RecentAttachmentDetails> RECENT_ATTACHMENT_FUNCT
            = (summary, dataSource, count) -> summary.getRecentAttachments(dataSource, count);

    private <T> void testNonPositiveCount_ThrowsError(RecentFilesMethod<T> method, String methodName)
            throws TskCoreException, SleuthkitCaseProviderException {
        Pair<SleuthkitCase, Blackboard> casePair = getArtifactsTSKMock(null);
        DataSource dataSource = TskMockUtils.getDataSource(1);
        RecentFilesSummary summary = new RecentFilesSummary(() -> casePair.getLeft());

        try {
            method.fetch(summary, dataSource, -1);
            fail("Expected method " + methodName + " to fail on negative count.");
        } catch (IllegalArgumentException ignored) {
            verify(casePair.getRight(),
                    never().description("Expected negative count for " + methodName + " to not call any methods in SleuthkitCase."))
                    .getArtifacts(anyInt(), anyLong());
        }
    }

    @Test
    public void getRecentlyOpenedDocuments_nonPositiveCount_ThrowsError() throws TskCoreException, SleuthkitCaseProviderException {
        testNonPositiveCount_ThrowsError(RECENT_DOCS_FUNCT, "getRecentlyOpenedDocuments");
    }

    @Test
    public void getRecentDownloads_nonPositiveCount_ThrowsError() throws TskCoreException, SleuthkitCaseProviderException {
        testNonPositiveCount_ThrowsError(RECENT_DOWNLOAD_FUNCT, "getRecentDownloads");
    }

    @Test
    public void getRecentAttachments_nonPositiveCount_ThrowsError() throws TskCoreException, SleuthkitCaseProviderException {
        testNonPositiveCount_ThrowsError(RECENT_ATTACHMENT_FUNCT, "getRecentAttachments");
    }

    private <T> void testNoDataSource_ReturnsEmptyList(RecentFilesMethod<T> recentFilesMethod, String methodName)
            throws SleuthkitCaseProviderException, TskCoreException {

        Pair<SleuthkitCase, Blackboard> casePair = getArtifactsTSKMock(null);
        RecentFilesSummary summary = new RecentFilesSummary(() -> casePair.getLeft());

        List<? extends T> items = recentFilesMethod.fetch(summary, null, 10);
        Assert.assertNotNull("Expected method " + methodName + " to return an empty list.", items);
        Assert.assertEquals("Expected method " + methodName + " to return an empty list.", 0, items.size());
        verify(casePair.getRight(),
                never().description("Expected null datasource for " + methodName + " to not call any methods in SleuthkitCase."))
                .getArtifacts(anyInt(), anyLong());
    }

    @Test
    public void getRecentlyOpenedDocuments_noDataSource_ReturnsEmptyList() throws TskCoreException, SleuthkitCaseProviderException {
        testNoDataSource_ReturnsEmptyList(RECENT_DOCS_FUNCT, "getRecentlyOpenedDocuments");
    }

    @Test
    public void getRecentDownloads_noDataSource_ReturnsEmptyList() throws TskCoreException, SleuthkitCaseProviderException {
        testNoDataSource_ReturnsEmptyList(RECENT_DOWNLOAD_FUNCT, "getRecentDownloads");
    }

    @Test
    public void getRecentAttachments_noDataSource_ReturnsEmptyList() throws TskCoreException, SleuthkitCaseProviderException {
        testNonPositiveCount_ThrowsError(RECENT_ATTACHMENT_FUNCT, "getRecentAttachments");
    }

    private <T> void testNoReturnedResults_ReturnsEmptyList(RecentFilesMethod<T> recentFilesMethod, String methodName)
            throws SleuthkitCaseProviderException, TskCoreException {

        Pair<SleuthkitCase, Blackboard> casePair = getArtifactsTSKMock(Collections.emptyList());
        RecentFilesSummary summary = new RecentFilesSummary(() -> casePair.getLeft());
        DataSource dataSource = TskMockUtils.getDataSource(1);
        List<? extends T> items = recentFilesMethod.fetch(summary, dataSource, 10);
        Assert.assertNotNull("Expected method " + methodName + " to return an empty list.", items);
        Assert.assertEquals("Expected method " + methodName + " to return an empty list.", 0, items.size());
        verify(casePair.getRight(),
                times(1).description("Expected " + methodName + " to call Blackboard once."))
                .getArtifacts(anyInt(), anyLong());
    }

    @Test
    public void getRecentlyOpenedDocuments_noReturnedResults_ReturnsEmptyList() throws TskCoreException, SleuthkitCaseProviderException {
        testNoReturnedResults_ReturnsEmptyList(RECENT_DOCS_FUNCT, "getRecentlyOpenedDocuments");
    }

    @Test
    public void getRecentDownloads_noReturnedResults_ReturnsEmptyList() throws TskCoreException, SleuthkitCaseProviderException {
        testNoReturnedResults_ReturnsEmptyList(RECENT_DOWNLOAD_FUNCT, "getRecentDownloads");
    }

    @Test
    public void getRecentAttachments_testNoDataSource_ReturnsEmptyList() throws TskCoreException, SleuthkitCaseProviderException {
        testNoReturnedResults_ReturnsEmptyList(RECENT_ATTACHMENT_FUNCT, "getRecentAttachments");
    }

    private static final long DAY_SECONDS = 24 * 60 * 60;

    /**
     * A means of creating a number representing seconds from epoch where the
     * lower the idx, the more recent the time.
     */
    private static final Function<Integer, Long> dateTimeRetriever = (idx) -> (365 - idx) * DAY_SECONDS + 1;

    private BlackboardArtifact getArtifact(DataSource ds, long artifactId, ARTIFACT_TYPE artType, List<Pair<ATTRIBUTE_TYPE, Object>> attributeArgs) {
        try {
            List<BlackboardAttribute> attributes = attributeArgs.stream()
                    .filter((arg) -> arg != null && arg.getLeft() != null && arg.getRight() != null)
                    .map((arg) -> {
                        return TskMockUtils.getAttribute(arg.getLeft(), arg.getRight());
                    })
                    .collect(Collectors.toList());

            return TskMockUtils.getArtifact(new BlackboardArtifact.Type(artType), artifactId, ds, attributes);
        } catch (TskCoreException ex) {
            fail("There was an error mocking an artifact.");
            return null;
        }
    }

    private BlackboardArtifact getRecentDocumentArtifact(DataSource ds, long artifactId, Long dateTime, String path) {
        return getArtifact(ds, artifactId, ARTIFACT_TYPE.TSK_RECENT_OBJECT, Arrays.asList(
                Pair.of(ATTRIBUTE_TYPE.TSK_DATETIME, dateTime),
                Pair.of(ATTRIBUTE_TYPE.TSK_PATH, path)
        ));
    }

    @Test
    public void getRecentlyOpenedDocuments_sortedByDateTimeAndLimited() throws SleuthkitCaseProviderException, TskCoreException {
        Function<Integer, String> pathRetriever = (idx) -> "C:\\path\\to\\downloads\\" + idx;
        // run through method
        DataSource dataSource = TskMockUtils.getDataSource(1);

        int countRequest = 10;
        for (int countToGenerate : new int[]{1, 9, 10, 11}) {
            // generate artifacts for each artifact
            List<BlackboardArtifact> artifacts = new ArrayList<>();
            for (int idx = 0; idx < countToGenerate; idx++) {
                BlackboardArtifact artifact = getRecentDocumentArtifact(dataSource,
                        1000 + idx, dateTimeRetriever.apply(idx), pathRetriever.apply(idx));
                artifacts.add(artifact);
            }

            Pair<SleuthkitCase, Blackboard> casePair = getArtifactsTSKMock(RandomizationUtils.getMixedUp(artifacts));
            RecentFilesSummary summary = new RecentFilesSummary(() -> casePair.getLeft());
            List<RecentFileDetails> results = summary.getRecentlyOpenedDocuments(dataSource, countRequest);

            // verify results
            int expectedCount = Math.min(countRequest, countToGenerate);
            Assert.assertNotNull(results);
            Assert.assertEquals(expectedCount, results.size());
            for (int i = 0; i < expectedCount; i++) {
                Assert.assertEquals(dateTimeRetriever.apply(i), results.get(i).getDateAsLong());
                Assert.assertEquals(pathRetriever.apply(i), results.get(i).getPath());
            }
        }
    }

    @Test
    public void getRecentlyOpenedDocuments_filtersMissingData() {

    }

    private BlackboardArtifact getRecentDownloadArtifact(DataSource ds, long artifactId, Long dateTime, String domain, String path) {
        return getArtifact(ds, artifactId, ARTIFACT_TYPE.TSK_WEB_DOWNLOAD, Arrays.asList(
                Pair.of(ATTRIBUTE_TYPE.TSK_DATETIME_ACCESSED, dateTime),
                Pair.of(ATTRIBUTE_TYPE.TSK_DOMAIN, domain),
                Pair.of(ATTRIBUTE_TYPE.TSK_PATH, path)
        ));
    }

    @Test
    public void getRecentDownloads_sortedByDateTimeAndLimited() throws SleuthkitCaseProviderException, TskCoreException {
        Function<Integer, String> domainRetriever = (idx) -> String.format("www.domain%d.com", idx);
        Function<Integer, String> pathRetriever = (idx) -> "C:\\path\\to\\downloads\\doc" + idx + ".pdf";

        // run through method
        DataSource dataSource = TskMockUtils.getDataSource(1);

        int countRequest = 10;
        for (int countToGenerate : new int[]{1, 9, 10, 11}) {
            // generate artifacts for each artifact
            List<BlackboardArtifact> artifacts = new ArrayList<>();
            for (int idx = 0; idx < countToGenerate; idx++) {
                BlackboardArtifact artifact = getRecentDownloadArtifact(dataSource,
                        1000 + idx, dateTimeRetriever.apply(idx), domainRetriever.apply(idx),
                        pathRetriever.apply(idx));

                artifacts.add(artifact);
            }

            Pair<SleuthkitCase, Blackboard> casePair = getArtifactsTSKMock(RandomizationUtils.getMixedUp(artifacts));
            RecentFilesSummary summary = new RecentFilesSummary(() -> casePair.getLeft());
            List<RecentDownloadDetails> results = summary.getRecentDownloads(dataSource, countRequest);

            // verify results
            int expectedCount = Math.min(countRequest, countToGenerate);
            Assert.assertNotNull(results);
            Assert.assertEquals(expectedCount, results.size());
            for (int i = 0; i < expectedCount; i++) {
                Assert.assertEquals(dateTimeRetriever.apply(i), results.get(i).getDateAsLong());
                Assert.assertEquals(pathRetriever.apply(i), results.get(i).getPath());
                Assert.assertEquals(domainRetriever.apply(i), results.get(i).getWebDomain());
            }
        }
    }

    @Test
    public void getRecentDownloads_filtersMissingData() {

    }

    private class AttachmentArtifactItem {

        private final Integer messageArtifactTypeId;
        private final boolean associatedAttrFormed;
        private final String emailFrom;
        private final Long messageTime;
        private final boolean hasParent;
        private final String fileParentPath;
        private final String fileName;

        public AttachmentArtifactItem(Integer messageArtifactTypeId, String emailFrom, Long messageTime,
                String fileParentPath, String fileName,
                boolean associatedAttrFormed, boolean hasParent) {

            this.messageArtifactTypeId = messageArtifactTypeId;
            this.associatedAttrFormed = associatedAttrFormed;
            this.emailFrom = emailFrom;
            this.messageTime = messageTime;
            this.hasParent = hasParent;
            this.fileParentPath = fileParentPath;
            this.fileName = fileName;
        }

        public AttachmentArtifactItem(Integer messageArtifactTypeId, String emailFrom, Long messageTime, String fileParentPath, String fileName) {
            this(messageArtifactTypeId, emailFrom, messageTime, fileParentPath, fileName, true, true);
        }

        boolean isAssociatedAttrFormed() {
            return associatedAttrFormed;
        }

        String getEmailFrom() {
            return emailFrom;
        }

        Long getMessageTime() {
            return messageTime;
        }

        boolean hasParent() {
            return hasParent;
        }

        String getFileParentPath() {
            return fileParentPath;
        }

        String getFileName() {
            return fileName;
        }

        Integer getMessageArtifactTypeId() {
            return messageArtifactTypeId;
        }
    }

    private Pair<SleuthkitCase, Blackboard> getRecentAttachmentArtifactCase(List<AttachmentArtifactItem> items) {
        SleuthkitCase skCase = mock(SleuthkitCase.class);
        Blackboard blackboard = mock(Blackboard.class);
        when(skCase.getBlackboard()).thenReturn(blackboard);

        DataSource dataSource = TskMockUtils.getDataSource(1);

        long objIdCounter = 100;
        Map<Long, BlackboardArtifact> artifacts = new HashMap<>();
        try {
            for (AttachmentArtifactItem item : items) {
                BlackboardAttribute associatedAttr = null;
                if (item.isAssociatedAttrFormed()) {
                    Long associatedId = ++objIdCounter;

                    associatedAttr = TskMockUtils.getAttribute(ATTRIBUTE_TYPE.TSK_ASSOCIATED_ARTIFACT, associatedId);

                    ARTIFACT_TYPE messageType = Stream.of(ARTIFACT_TYPE.values())
                            .filter((artType) -> artType.getTypeID() == item.getMessageArtifactTypeId())
                            .findFirst()
                            .orElse(null);

                    if (messageType != null) {
                        List<BlackboardAttribute> attributes = new ArrayList<>();
                        if (item.getEmailFrom() != null) {
                            attributes.add(TskMockUtils.getAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_FROM, item.getEmailFrom()));
                        }

                        if (item.getMessageTime() != null) {
                            attributes.add(TskMockUtils.getAttribute(ATTRIBUTE_TYPE.TSK_DATETIME_SENT, item.getMessageTime()));
                        }

                        artifacts.put(associatedId, TskMockUtils.getArtifact(
                                new BlackboardArtifact.Type(messageType), ++objIdCounter, dataSource, attributes));
                    }
                }

                Content parent = (item.hasParent())
                        ? TskMockUtils.getAbstractFile(++objIdCounter, item.getFileParentPath(), item.getFileName())
                        : null;

                Long associatedId = ++objIdCounter;
                artifacts.put(associatedId, TskMockUtils.getArtifact(
                        new BlackboardArtifact.Type(ARTIFACT_TYPE.TSK_ASSOCIATED_OBJECT),
                        parent, associatedId, dataSource, associatedAttr));
            }

            when(blackboard.getArtifacts(anyInt(), anyLong())).thenAnswer((inv) -> {
                Object[] args = inv.getArguments();
                int artifactType = (Integer) args[0];
                return artifacts.values().stream()
                        .filter(art -> art.getArtifactTypeID() == artifactType)
                        .collect(Collectors.toList());
            });

            when(skCase.getBlackboardArtifact(anyLong())).thenAnswer((inv2) -> {
                Object[] args2 = inv2.getArguments();
                long id = (Long) args2[0];
                return artifacts.get(id);
            });

            return Pair.of(skCase, blackboard);
        } catch (TskCoreException ex) {
            fail("There was an error while creating SleuthkitCase for getRecentAttachments");
            return null;
        }
    }

    @Test
    public void getRecentAttachments_sortedByDateTimeAndLimited() throws SleuthkitCaseProviderException, TskCoreException {
        // run through method
        DataSource dataSource = TskMockUtils.getDataSource(1);
        Function<Integer, String> emailFromRetriever = (idx) -> String.format("person%d@basistech.com", idx);
        Function<Integer, String> pathRetriever = (idx) -> "C:\\path\\to\\attachment\\" + idx;
        Function<Integer, String> fileNameRetriever = (idx) -> String.format("%d-filename.png", idx);

        int countRequest = 10;
        for (int countToGenerate : new int[]{1, 9, 10, 11}) {
            List<AttachmentArtifactItem> items = IntStream.range(0, countToGenerate)
                    .mapToObj((idx) -> new AttachmentArtifactItem(ARTIFACT_TYPE.TSK_MESSAGE.getTypeID(),
                    emailFromRetriever.apply(idx), dateTimeRetriever.apply(idx),
                    pathRetriever.apply(idx), fileNameRetriever.apply(idx)))
                    .collect(Collectors.toList());

            List<AttachmentArtifactItem> mixedUpItems = RandomizationUtils.getMixedUp(items);
            Pair<SleuthkitCase, Blackboard> casePair = getRecentAttachmentArtifactCase(mixedUpItems);
            RecentFilesSummary summary = new RecentFilesSummary(() -> casePair.getLeft());
            List<RecentAttachmentDetails> results = summary.getRecentAttachments(dataSource, countRequest);

            // verify results
            int expectedCount = Math.min(countRequest, countToGenerate);
            Assert.assertNotNull(results);
            Assert.assertEquals(expectedCount, results.size());

            for (int i = 0; i < expectedCount; i++) {
                RecentAttachmentDetails result = results.get(i);
                Assert.assertEquals(dateTimeRetriever.apply(i), result.getDateAsLong());
                Assert.assertTrue(emailFromRetriever.apply(i).equalsIgnoreCase(result.getSender()));
                Assert.assertTrue(Paths.get(pathRetriever.apply(i), fileNameRetriever.apply(i)).toString()
                        .equalsIgnoreCase(result.getPath()));;
            }
        }
    }

    @Test
    public void getRecentAttachments_filtersMissingData() {

    }

    @Test
    public void getRecentAttachments_handlesMalformed() {

    }

    @Test
    public void getRecentAttachments_onlyUsesMessageArtifacts() {

    }
}
