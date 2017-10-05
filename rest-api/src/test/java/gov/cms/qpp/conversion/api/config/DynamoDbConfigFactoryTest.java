package gov.cms.qpp.conversion.api.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;

public class DynamoDbConfigFactoryTest {
	@Test
	public void testConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<DynamoDbConfigFactory> constructor = DynamoDbConfigFactory.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testFactory() {
		DynamoDBMapper dynamoDBMapper = DynamoDbConfigFactory.createDynamoDbMapper(null, null, null);
		assertNotNull("The DynamoDB mapper must not be null.", dynamoDBMapper);
	}
}
