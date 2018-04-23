package info.ieathealthy.restcontrollers;

import info.ieathealthy.cryptoutils.JWTKeyFactory;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import info.ieathealthy.models.Ingredient;
import java.security.Key;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.ArrayList;

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

    /* /api/ingredient/{name} and /api/ingredient/{id} are ambiguous
    @RequestMapping(value="/api/ingredient/{name}", method=RequestMethod.GET)
    public ResponseEntity<?> getIngredientByName(@PathVariable String name, @RequestParam(value="token") String token) {
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            Ingredient ing = _ingredientCollection.find(eq("shrtDesc", name)).first();

            //If ing is null then the ingredient was not found so return an error. Otherwise return the ingredient found.
            if (ing == null) {
                return new ResponseEntity<>("Error: The provided name does not match any records in the database.", HttpStatus.NOT_FOUND);
            }
            else {
                return new ResponseEntity<>(ing, HttpStatus.OK);
            }

        } catch (SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }
    */


    @RequestMapping(value="/api/ingredient/{id}", method=RequestMethod.GET)
    public ResponseEntity<?> getIngredientById(@PathVariable String id, @RequestParam(value="token") String token) {
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId ingId; //ObjectId to store the provided string id.

            //Convert the id to an ObjectId. IllegalArgumentException will be thrown
            //if the string id is not a valid hex string representation of an ObjectId.
            try{
                ingId = new ObjectId(id);

            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }

            Ingredient ing = _ingredientCollection.find(eq("_id", ingId)).first();

            //If ing is null then the ingredient was not found so return an error. Otherwise return the ingredient found.
            if (ing == null) {
                return new ResponseEntity<>("Error: The provided name does not match any records in the database.", HttpStatus.NOT_FOUND);
            }
            else {
                return new ResponseEntity<>(ing, HttpStatus.OK);
            }

        } catch (SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }


    @RequestMapping(value="/api/ingredient", method=RequestMethod.GET)
    public ResponseEntity<?> getAllIngredients(@RequestParam(value="token") String token) {
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ArrayList<Ingredient> ings = _ingredientCollection.find().into(new ArrayList<Ingredient>());

            //If ings is null then the collection is empty (very unlikely), otherwise return the entire collection.
            if (ings == null) {
                return new ResponseEntity<>("Error: No records in the 'ingredients' collection.", HttpStatus.NOT_FOUND);
            }
            else {
                return new ResponseEntity<>(ings, HttpStatus.OK);
            }

        } catch (SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }
}
