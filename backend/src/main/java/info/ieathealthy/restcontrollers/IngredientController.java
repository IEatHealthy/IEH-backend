package info.ieathealthy.restcontrollers;

import info.ieathealthy.cryptoutils.JWTKeyFactory;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import info.ieathealthy.models.Ingredient;
import java.security.Key;
import java.security.SignatureException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    private final Key _sigKey;

    public IngredientController(@Qualifier("mongoClient") MongoClient client, @Qualifier("mongoCodecRegistry") CodecRegistry modelCodecRegistry, @Qualifier("jwtKeyFactory") Key sigKey){
        this._client = client;
        this._modelCodecRegistry = modelCodecRegistry;
        this._db = _client.getDatabase("food-data");
        this._ingredientCollection = _db.getCollection("ingredients", Ingredient.class).withCodecRegistry(_modelCodecRegistry);
        this._sigKey = sigKey;
    }

    //need to handle errors in retrieving ingredients
    @RequestMapping(value="/api/{token}/ingredient", method=RequestMethod.GET)
    public ResponseEntity<?> ingredient(@RequestParam(value="name") String name, @PathVariable String token){
        try {

            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            Ingredient ing = _ingredientCollection.find(regex("shrtDesc", name)).first();
            return new ResponseEntity<>(ing, HttpStatus.FOUND);

        } catch (io.jsonwebtoken.SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

}
