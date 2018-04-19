package info.ieathealthy.restcontrollers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.*;
import info.ieathealthy.models.Recipe;
import io.jsonwebtoken.Jwts;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Key;
import java.util.Base64;

import static com.mongodb.client.model.Filters.eq;


@RestController
public class RecipeController {
    //mongo client
    //DI by constructor
    //used for database ops
    private final MongoClient _client;
    private final MongoDatabase _db;
    private final MongoCollection<Recipe> _recipeCollection;
    private final CodecRegistry _modelCodecRegistry;
    private final Key _sigKey;

    public RecipeController(@Qualifier("mongoClient") MongoClient client, @Qualifier("mongoCodecRegistry") CodecRegistry modelCodecRegistry, @Qualifier("jwtKeyFactory") Key sigKey){
        this._client = client;
        this._modelCodecRegistry = modelCodecRegistry;
        this._db = _client.getDatabase("food-data");
        this._recipeCollection = _db.getCollection("recipes", Recipe.class).withCodecRegistry(_modelCodecRegistry);
        this._sigKey = sigKey;
    }

    @RequestMapping(value="/api/recipe/{name}", method=RequestMethod.GET)
    public ResponseEntity<?> ingredient(@PathVariable String name, @RequestParam(value="token") String token){
        try {

            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            Recipe recipe = _recipeCollection.find(eq("name", name)).first();

            if (recipe == null) {
                return new ResponseEntity<>("Error: No recipe with provided name was found.", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(recipe, HttpStatus.OK);
            }

        } catch (io.jsonwebtoken.SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

    //Ambiguous paths - /api/recipe/{id} and /api/recipe/{name}
    /*
    @RequestMapping(value="/api/recipe/{id}", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeById(@PathVariable String id, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            Recipe recipe = _recipeCollection.find(eq("_id", new ObjectId(id))).first();

            if (recipe == null) {
                return new ResponseEntity<>("Error: No recipe with provided id was found.", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(recipe, HttpStatus.OK);
            }

        } catch (io.jsonwebtoken.SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }
    */


    //Not working!
    @RequestMapping(value="/api/recipe/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateRecipeById(@PathVariable String id, @RequestParam(value="token") String token, @RequestBody Recipe editedRecipe){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            //Perform search for recipe with provided id.
            Recipe recipeFound = _recipeCollection.find(eq("_id", new ObjectId(id))).first();

            //If recipe was not found then return 404 Not Found response.
            if (recipeFound == null)
            {
                return new ResponseEntity<>("Error: No recipe with provided was found.", HttpStatus.NOT_FOUND);
            }

            UpdateResult result = _recipeCollection.replaceOne(eq("_id", new ObjectId(id)), editedRecipe);

            return new ResponseEntity<>("Recipe update successful.", HttpStatus.OK);

            /*
            if (result.wasAcknowledged() == true){
                return new ResponseEntity<>("Recipe update successful.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error: Recipe update unsuccessful", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            */

        } catch (io.jsonwebtoken.SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

}
