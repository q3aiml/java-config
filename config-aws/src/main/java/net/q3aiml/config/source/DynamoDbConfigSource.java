package net.q3aiml.config.source;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.collect.ImmutableMap;
import net.q3aiml.config.PollResult;

import java.util.List;
import java.util.Map;

/**
 * Polls dynamodb for config updates.
 */
public class DynamoDbConfigSource implements PollingConfigSource {
    protected AmazonDynamoDB dynamo;
    protected String tableName;
    protected String keyAttributeName = "key";
    protected String valueAttributeName = "value";

    public DynamoDbConfigSource(AmazonDynamoDB dynamo, String tableName) {
        this.dynamo = dynamo;
        this.tableName = tableName;
    }

    @Override
    public PollResult poll() {
        ImmutableMap.Builder<String, String> results = ImmutableMap.builder();
        Map<String, AttributeValue> lastEvaluatedKey = null;
        do {
            ScanRequest scanRequest = new ScanRequest(tableName)
                    .withExclusiveStartKey(lastEvaluatedKey);
            ScanResult scanResult = dynamo.scan(scanRequest);
            handleItems(scanResult.getItems(), results);
            lastEvaluatedKey = scanResult.getLastEvaluatedKey();
        } while (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());
        return PollResult.fullUpdate(results.build());
    }

    protected void handleItems(List<Map<String, AttributeValue>> items, ImmutableMap.Builder<String, String> results) {
        for (Map<String, AttributeValue> item : items) {
            AttributeValue keyAttribute = item.get(keyAttributeName);
            AttributeValue valueAttribute = item.get(valueAttributeName);
            if (keyAttribute == null) {
                throw new IllegalArgumentException("expected " + tableName + " item " + item
                        + " to have " + keyAttributeName + " attribute");
            }
            if (valueAttribute == null) {
                throw new IllegalArgumentException("expected " + tableName + " item " + item
                        + " to have " + valueAttributeName + " attribute");
            }
            results.put(keyAttribute.getS(), valueAttribute.getS());
        }
    }

    @Override
    public String toString() {
        return "DynamoDbConfigSource{" +
                "tableName='" + tableName + '\'' +
                ", keyAttributeName='" + keyAttributeName + '\'' +
                ", valueAttributeName='" + valueAttributeName + '\'' +
                ", dynamo=" + dynamo +
                '}';
    }
}
