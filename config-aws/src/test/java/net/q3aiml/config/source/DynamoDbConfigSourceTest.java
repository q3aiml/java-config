package net.q3aiml.config.source;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.q3aiml.config.PollResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DynamoDbConfigSourceTest {
    AmazonDynamoDBClient client = mock(AmazonDynamoDBClient.class);
    String tableName = "testtable";
    DynamoDbConfigSource source = new DynamoDbConfigSource(client, tableName);
    @SuppressWarnings("unchecked")
    Map<String, AttributeValue> lastEvaluatedKey = mock(Map.class);

    @Test
    public void pollEmptyTest() {
        when(client.scan(new ScanRequest(tableName).withExclusiveStartKey(null)))
                .thenReturn(emptyResult());
        PollResult result = source.poll();
        assertTrue(result.result().isEmpty());
    }

    @Test
    public void pollResultsTest() {
        ScanResult firstResult = new ScanResult();
        firstResult.setItems(ImmutableList.<Map<String, AttributeValue>>of(ImmutableMap.of(
                "key", new AttributeValue().withS("item1"),
                "value", new AttributeValue().withS("value1")
        )));
        firstResult.setLastEvaluatedKey(lastEvaluatedKey);
        ScanResult secondResult = new ScanResult();
        secondResult.setItems(ImmutableList.<Map<String, AttributeValue>>of(ImmutableMap.of(
                "key", new AttributeValue().withS("item2"),
                "value", new AttributeValue().withS("value2"),
                "someotherattribute", new AttributeValue().withS("value")
        ), ImmutableMap.of(
                "key", new AttributeValue().withS("moitems"),
                "value", new AttributeValue().withS("movalues")
        )));
        when(client.scan(new ScanRequest(tableName).withExclusiveStartKey(null)))
                .thenReturn(firstResult);
        when(client.scan(new ScanRequest(tableName).withExclusiveStartKey(lastEvaluatedKey)))
                .thenReturn(secondResult);
        PollResult result = source.poll();
        assertEquals(ImmutableMap.of(
                "item1", "value1",
                "item2", "value2",
                "moitems", "movalues"
        ), result.result());
    }

    @Test
    public void pollResultsInvalidItemNoKeyTest() {
        ScanResult firstResult = new ScanResult();
        firstResult.setItems(ImmutableList.<Map<String, AttributeValue>>of(ImmutableMap.of(
                "name", new AttributeValue().withS("whoops, should have said 'key'"),
                "value", new AttributeValue().withS("thevalue")
        )));
        firstResult.setLastEvaluatedKey(lastEvaluatedKey);
        when(client.scan(new ScanRequest(tableName).withExclusiveStartKey(null)))
                .thenReturn(firstResult);
        try {
            source.poll();
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
            // expected
        }
    }

    @Test
    public void pollResultsInvalidItemNoValueTest() {
        ScanResult firstResult = new ScanResult();
        firstResult.setItems(ImmutableList.<Map<String, AttributeValue>>of(ImmutableMap.of(
                "key", new AttributeValue().withS("veryimportant"),
                "description", new AttributeValue().withS("should probably have a value")
        )));
        firstResult.setLastEvaluatedKey(lastEvaluatedKey);
        when(client.scan(new ScanRequest(tableName).withExclusiveStartKey(null)))
                .thenReturn(firstResult);
        try {
            source.poll();
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
            // expected
        }
    }

    private static ScanResult emptyResult() {
        ScanResult emptyResult = new ScanResult();
        emptyResult.setItems(Collections.<Map<String, AttributeValue>>emptyList());
        return emptyResult;
    }

    @Test
    public void toStringTest() throws Exception {
        Assert.assertThat(source.toString().toLowerCase(), containsString("dynamo"));
        Assert.assertThat(source.toString().toLowerCase(), containsString("testtable"));
    }
}