package info.ieathealthy.mongoutils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


//Note: Not thread safe
//Should only be used by Spring to get
//MongoClient instance for IoC container
public class MongoClientFactory {
    private static MongoClient _instance = null;

    public static MongoClient getInstance() {
        if(_instance == null){
            MongoClientURI uri = new MongoClientURI(
                    "mongodb+srv://IEatHealthy:srsnEu1KzpBKpSOt@ieathealthy-cluster0-0q8tc.mongodb.net/"
            );
            try {
                _instance = new MongoClient(uri);
            } catch(Exception e){
                System.out.print(e.toString());
            }
        }

        return _instance;
    }

    private MongoClientFactory() {
    }
}
