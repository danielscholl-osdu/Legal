/**
* Copyright MongoDB, Inc or its affiliates. All Rights Reserved.
* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.opengroup.osdu.legal.aws.tags.dataaccess.mongodb.repository;

import org.opengroup.osdu.core.aws.mongodb.MongoDBMultiClusterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import java.util.HashSet;

import static org.opengroup.osdu.legal.aws.tags.dataaccess.mongodb.repository.LegalTagRepositoryMongoDBImpl.COLLECTION_PREFIX;
import static org.opengroup.osdu.legal.aws.tags.dataaccess.mongodb.repository.LegalTagRepositoryMongoDBImpl.IS_VALID;

@Component
@Lazy
public class IndexUpdater {

    private MongoDBMultiClusterFactory mongoDBMultiClusterFactory;
    private HashSet<String> indexedDataPartitions = new HashSet<>();

    @Autowired
    public IndexUpdater(MongoDBMultiClusterFactory mongoDBMultiClusterFactory) {
        this.mongoDBMultiClusterFactory = mongoDBMultiClusterFactory;
    }

    public void checkIndex(String dataPartitionId) {
        if (indexedDataPartitions.contains(dataPartitionId)) {
            return;
        }

        indexedDataPartitions.add(dataPartitionId);
        this.updateIndexes(dataPartitionId);
    }

    private void updateIndexes(String dataPartitionId) {
        String groupCollection = COLLECTION_PREFIX + dataPartitionId;
        mongoDBMultiClusterFactory.getHelper(dataPartitionId).ensureIndex(groupCollection, new Index().on(IS_VALID, Sort.Direction.ASC));
    }
}
