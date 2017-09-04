/**
 * 
 */
package com.game.dao;

import org.bson.types.ObjectId;

import com.game.exception.GameDataAccesException;
import com.game.party.PartyBean;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
//import org.springframework.stereotype.Repository;

/**
 * @author taras
 * 
 */
//@Repository  
public class GameDao extends BasicDAO<PartyBean, ObjectId>{
	
    private Mongo mongo;
    private String dbName;
	
	public GameDao(Morphia morphia, Mongo mongo) {
        super(mongo, morphia, "cheat");
       this.mongo = mongo;
       this.dbName = "cheat";
    }
    
    public GameDao(Morphia morphia, Mongo mongo,String databaseName) {
        super(mongo, morphia, databaseName);
        this.mongo = mongo;
        this.dbName = databaseName;
    }
    
	public PartyBean getNotCompletedGameWithUser(String user) {
		Query<PartyBean> q = ds.find(PartyBean.class).field("isComplete").equal(new Boolean(false)).field("users").contains(user);
		return findOne(q);
	}
	
	public String getGameJson(String session) throws GameDataAccesException {
		DB db = mongo.getDB(dbName);
		DBCollection table = db.getCollection("Game");
		try {
			BasicDBObject obj = new BasicDBObject();        
			obj.append("_id", new ObjectId(session));    
			
			DBObject findOne = table.findOne();
			if(findOne != null)
				return findOne.toString();
			return null;
		} catch (MongoException e) {
			System.out.println("" + e);
			throw new GameDataAccesException("" + e);
		}
	}
	
	public void clearAll() {
		DB db = mongo.getDB(dbName);
		DBCollection table = db.getCollection("Game");
		try {
			BasicDBObject dbo = new BasicDBObject();
			table.remove(dbo);
		} catch (MongoException e) {
			System.out.println("" + e);
		}
	}

	public PartyBean getGameById(String sessionId) {
		return get(new ObjectId(sessionId));
		
	}

}
