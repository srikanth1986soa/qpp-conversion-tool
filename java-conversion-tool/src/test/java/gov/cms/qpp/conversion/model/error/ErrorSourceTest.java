package gov.cms.qpp.conversion.model.error;

import gov.cms.qpp.conversion.model.ValidationError;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class ErrorSourceTest {
	@Test
	public void addValidationError() {
		ErrorSource objectUnderTest = new ErrorSource();

		assertThat("The validation errors should have been null at first", objectUnderTest.getValidationErrors(), is(nullValue()));

		objectUnderTest.addValidationError(new ValidationError("description", "path"));
		objectUnderTest.addValidationError(new ValidationError("description", "path"));

		assertThat("The validation errors should no longer be null", objectUnderTest.getValidationErrors(), is(not(nullValue())));
		assertThat("The list should be one", objectUnderTest.getValidationErrors(), hasSize(2));
	}

	@Test
	public void mutability() {
		ErrorSource objectUnderTest = new ErrorSource();

		objectUnderTest.setSourceIdentifier("meep");
		objectUnderTest.setValidationErrors(Collections.singletonList(new ValidationError("description", "path")));

		assertThat("The validation errors should no longer be null", objectUnderTest.getValidationErrors(), is(not(nullValue())));
		assertThat("The list should be one", objectUnderTest.getValidationErrors(), hasSize(1));
		assertThat("The source identifier should be set", objectUnderTest.getSourceIdentifier(), is("meep"));
	}
}