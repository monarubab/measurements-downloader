package codes.mona.measurementsdownloader.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import codes.mona.measurementsdownloader.model.UiLogger;

/**
 * Importing files into MongoDB and executing queries.
 */
public class DatabaseManager {

	private static DatabaseManager instance;

	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager(new MongoClient("localhost", 27017));
		}
		return instance;
	}

	private final MongoClient client;

	public DatabaseManager(MongoClient client) {
		this.client = client;
	}

	// Database "Plants" must exist
	// Collection of the various plants must exist
	public void mongoDBImport(List<Document> documents, String plantName) throws IOException {
		// Access database
		MongoDatabase database = client.getDatabase("Plants");

		// Retrieve collection for dropping the collection
		// MongoCollection<org.bson.Document> collection =
		// database.getCollection("foo");
		// collection.drop();

		// Create collection
		// database.createCollection("foo");
		// UserInterface.UI_LOGGER.log("Collection created successfully");

		// Insert document
		MongoCollection<Document> collection = database.getCollection(plantName);

		for (Document document : documents) {

			FindIterable<Document> findIterable = collection.find(new BasicDBObject("Date", document.get("Date")));

			if (findIterable.first() == null) {

				UiLogger.getInstance().log("importing document");
				collection.insertOne(document);

			} else {

				UiLogger.getInstance().log("not importing document");
			}
		}
	}

	public double aggregate(String plantName, Date from, Date to, String aggregation, String columnName) {
		columnName = removeDots(columnName);
		MongoDatabase database = client.getDatabase("Plants");
		MongoCollection<Document> collection = database.getCollection(plantName);
		AggregateIterable<Document> result = collection.aggregate(Arrays.asList(

				new BasicDBObject("$match",
						new BasicDBObject("Date", new BasicDBObject("$gte", from).append("$lte", to))),
				new BasicDBObject("$group", new BasicDBObject("_id", "$null").append("result",
						new BasicDBObject("$" + aggregation, "$" + columnName)))));

		Document first = result.first();

		if (first != null) {
			return first.getDouble("result");
		}

		throw new NoSuchElementException();
	}

	private String removeDots(String string) {
		return string.replaceAll("\\.", "");
	}
}
