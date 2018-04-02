package info.ieathealthy.restcontrollers;

import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import info.ieathealthy.models.Ingredient;
import static com.mongodb.client.model.Filters.*;

@RestController
public class IngredientController {

    //mongo client
    //DI by constructor
    //used for database ops
    private final MongoClient _client;
    private final MongoDatabase _db;
    private final MongoCollection<Ingredient> _ingredientCollection;
    private final CodecRegistry _modelCodecRegistry;

    public IngredientController(@Qualifier("mongoClient") MongoClient client, @Qualifier("mongoCodecRegistry") CodecRegistry modelCodecRegistry){
        this._client = client;
        this._modelCodecRegistry = modelCodecRegistry;
        this._db = _client.getDatabase("food-data");
        this._ingredientCollection = _db.getCollection("ingredients", Ingredient.class).withCodecRegistry(_modelCodecRegistry);
    }

    //need to handle errors in retrieving ingredients
    @RequestMapping(value="/ingredient", method=RequestMethod.GET)
    public Ingredient ingredient(@RequestParam(value="name") String name){

        Ingredient ing = _ingredientCollection.find(regex("shrtDesc", name)).first();

        return ing;
    }

}
