package ar.edu.unp.madryn.livremarket.common.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MongoProvider implements DataProvider {
    private String ip;

    private int port;

    private String databaseName;

    private MongoDatabase mongoDatabase;

    public MongoProvider(String ip, int port, String databaseName) {
        this.ip = ip;
        this.port = port;
        this.databaseName = databaseName;
    }

    @Override
    public boolean connect() {
        if (StringUtils.isEmpty(this.ip) || this.port < 0 || StringUtils.isEmpty(this.databaseName)) {
            return false;
        }

        MongoClient mongoClient = new MongoClient(this.ip, this.port);
        this.mongoDatabase = mongoClient.getDatabase(this.databaseName);

        return true;
    }

    @Override
    public boolean insertElement(Object elementToInsert, String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        Gson gson = new Gson();

        collection.insertOne(Document.parse(gson.toJson(elementToInsert)));
        return true;
    }

    @Override
    public boolean updateElement(String id, Object elementToUpdate, String collectionName) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);

        Gson gson = new Gson();

        Document document = new Document();

        ObjectId objectId = new ObjectId(id);

        document.put(DataProvider.DEFAULT_ID_FIELD, objectId);

        Document toUpdate = Document.parse(gson.toJson(elementToUpdate));

        UpdateResult updateResult = collection.updateOne(document, toUpdate);

        return updateResult.wasAcknowledged();
    }

    @Override
    public <T> Collection<T> getCollection(String collectionName, Class<T> elementsType) {
        MongoCollection<Document> databaseCollection = this.mongoDatabase.getCollection(collectionName);

        Gson gson = new Gson();

        Collection<T> result = new ArrayList<>();

        FindIterable<Document> findIterable = databaseCollection.find();

        for (Document document : findIterable) {
            T element = gson.fromJson(document.toJson(), elementsType);
            if (element != null) {
                result.add(element);
            }
        }

        return result;
    }

    @Override
    public Map<String, String> getDataFromCollectionByField(String collectionName, String fieldName, String value) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);

        Gson gson = new Gson();

        Document document = new Document();

        document.put(fieldName, value);

        FindIterable<Document> findIterable = collection.find(document).limit(1);

        Document found = findIterable.first();

        if(found == null){
            return new HashMap<>();
        }

        return gson.fromJson(found.toJson(), new TypeToken<Map<String,String>>(){}.getType());
    }
}
