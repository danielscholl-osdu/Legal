//  Copyright © Microsoft Corporation
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.opengroup.osdu.legal.azure.tags.dataaccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.azure.CosmosStore;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.legal.LegalTag;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LegalTagRepositoryImplTest {

    private static final String dataPartitionId = "data-partition-id";

    @Mock
	private CosmosStore cosmosStore;

    @Mock
	private DpsHeaders headers;

    @InjectMocks
    private LegalTagRepositoryImpl sut;

    @Before
    public void init() {
        lenient().doReturn(dataPartitionId).when(headers).getPartitionId();
    }

    @Test
    public void testGetLegalTagCollections_whenIdsIsNull() {
        long[] ids = null;
        List<LegalTag> output = (List<LegalTag>) sut.get(ids);
        assertEquals(output.size(), 0);
    }

    @Test
    public void testGetLegalTagCollections_whenIdsIsNotNull() {
        long[] ids = {0, 1};
        String[] strIds = {"0", "1"};
        Optional[] legalTagDocs = new Optional[2];
        legalTagDocs[0] = Optional.of(new LegalTagDoc(strIds[0], getLegalTagWithId(ids[0])));
        legalTagDocs[1] = Optional.of(new LegalTagDoc(strIds[0], getLegalTagWithId(ids[1])));

        doReturn(legalTagDocs[0]).when(cosmosStore).findItem(eq(dataPartitionId), any(), any(), eq(strIds[0]), eq(strIds[0]), any());
        doReturn(legalTagDocs[1]).when(cosmosStore).findItem(eq(dataPartitionId), any(), any(), eq(strIds[1]), eq(strIds[1]), any());

        List<LegalTag> output = (List<LegalTag>) sut.get(ids);
        assertEquals(output.size(), 2);
        assertEquals(output.get(0).getId().longValue(), ids[0]);
        assertEquals(output.get(1).getId().longValue(), ids[1]);
    }

    private LegalTag getLegalTagWithId(long id) {
        LegalTag legalTag = new LegalTag();
        legalTag.setId(id);
        return legalTag;
    }
}
