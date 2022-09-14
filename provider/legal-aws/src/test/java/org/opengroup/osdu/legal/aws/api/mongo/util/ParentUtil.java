package org.opengroup.osdu.legal.aws.api.mongo.util;

import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.mockito.Mockito;
import org.opengroup.osdu.core.aws.partition.PartitionInfoAws;
import org.opengroup.osdu.core.aws.partition.PartitionServiceClientWithCache;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.partition.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.ArgumentMatchers.anyString;

public abstract class ParentUtil {

    public static final String DATA_PARTITION = "osdu_legal_data_partition";
    public static final String LEGAL_TAG_NAME = "LEGAL_TAG_NAME";

    public MongoTemplateHelper mongoTemplateHelper;

    @MockBean
    private DpsHeaders headers;
    @MockBean
    private PartitionServiceClientWithCache partitionServiceClient;
    @MockBean
    private JaxRsDpsLog log;

    @Rule
    public ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() {
            ParentUtil.this.mongoTemplateHelper.dropCollections();
            ParentUtil.this.setHeaderDataPartition(DATA_PARTITION);
            PartitionInfoAws partitionInfoAws = new PartitionInfoAws();
            Property tenantIdProperty = new Property();
            tenantIdProperty.setValue(DATA_PARTITION);
            partitionInfoAws.setTenantIdProperty(tenantIdProperty);
            Mockito.when(partitionServiceClient.getPartition(anyString())).thenReturn(partitionInfoAws);

        }

        @Override
        protected void after() {
            ParentUtil.this.mongoTemplateHelper.dropCollections();
        }
    };

    @Autowired
    public void set(MongoTemplate mongoTemplate) {
        this.mongoTemplateHelper = new MongoTemplateHelper(mongoTemplate);
    }


    public void setHeaderDataPartition(String dataPartition) {
        Mockito.when(this.headers.getPartitionId())
                .thenReturn(dataPartition);
    }

}