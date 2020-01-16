package codes.mona.measurementsdownloader.core;

import static com.google.common.truth.Truth.assertThat;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import codes.mona.measurementsdownloader.core.DatabaseManager;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DatabaseManagerTest {

	@Mock
	MongoClient mongoClient;

	@Mock
	MongoDatabase mongoDatabase;

	@Mock
	MongoCollection mongoCollection;

	@Mock
	FindIterable nonemptyFindIterable;

	@Mock
	FindIterable emptyFindIterable;

	@Mock
	AggregateIterable<Document> aggregateIterable;

	@InjectMocks
	DatabaseManager databaseManager;

	@Before
	public void setUp() {
		when(mongoClient.getDatabase(any())).thenReturn(mongoDatabase);
		when(mongoDatabase.getCollection(any())).thenReturn(mongoCollection);
	}

	@Test
	public void databaseManagementInsertsOnlyNewDocuments() throws IOException {
		
		// Set up the Mocks so that the first document is found, the second isn't.
		when(nonemptyFindIterable.first()).thenReturn(new Object());
		when(emptyFindIterable.first()).thenReturn(null);
		when(mongoCollection.find(any(BasicDBObject.class))).thenReturn(nonemptyFindIterable).thenReturn(emptyFindIterable);

		List<Document> documents = new ArrayList<>();
		Document document1 = new Document();
		Document document2 = new Document();
		documents.add(document1);
		documents.add(document2);

		databaseManager.mongoDBImport(documents, "plantName");

		verify(mongoCollection).insertOne(same(document2));
	}

	@Test
	public void aggregate() throws ParseException {
		when(mongoCollection.aggregate(any())).thenReturn(aggregateIterable);
		when(aggregateIterable.first()).thenReturn(new Document("result", 1.2));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		// Input
		String plantName = "plant";
		Date from = dateFormat.parse("2019-10-26 06");
		Date to = dateFormat.parse("2019-10-26 09");
		String aggregation = "aggregation";
		String column = "column";

		double result = databaseManager.aggregate(plantName, from, to, aggregation, column);

		assertThat(result).isEqualTo(1.2);

		verify(mongoCollection).aggregate(eq(
				Arrays.asList(
						new BasicDBObject("$match", new BasicDBObject("Date", new BasicDBObject("$gte", from).append("$lte", to))),
						new BasicDBObject("$group", new BasicDBObject("_id", "$null").append("result", new BasicDBObject("$" + aggregation, "$" + column)))
				)
		));
	}
}
