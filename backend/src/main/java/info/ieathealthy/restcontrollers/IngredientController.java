package info.ieathealthy.restcontrollers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;

@RestController
public class IngredientController {

    //mongo client
    //DI by constructor
    //used for database ops
    private final MongoClient _client;

    public IngredientController(@Qualifier("mongoClient") MongoClient client){
        this._client = client;
    }

    @RequestMapping("/ingredient" )
    public Document ingredient(@RequestParam(value="name") String name){

        MongoDatabase ingredientdb = _client.getDatabase("food-data");
        MongoCollection<Document> collection = ingredientdb.getCollection("ingredients");

        return collection.find(regex("Shrt_Desc", name)).first();
    }

}
