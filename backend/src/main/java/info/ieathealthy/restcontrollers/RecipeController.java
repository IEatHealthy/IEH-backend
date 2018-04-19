package info.ieathealthy.restcontrollers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.*;
import com.mongodb.MongoWriteException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoException;
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

    /*
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
    */

    //Ambiguous paths - /api/recipe/{id} and /api/recipe/{name}
    @RequestMapping(value="/api/recipe/{id}", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeById(@PathVariable String id, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId; //ObjectId to store the provided string id.

            //Convert the id to an ObjectId. IllegalArgumentException will be thrown
            //if the string id is not a valid hex string representation of an ObjectId.
            try{
                recipeId = new ObjectId(id);

            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }

            Recipe recipe = _recipeCollection.find(eq("_id", recipeId)).first();

            //If the recipe is null then there were no matching recipes with the provided id. Otherwise the
            //recipe was found and is returned.
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


    //Not working! More complicated to test so leave for last.
    @RequestMapping(value="/api/recipe/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateRecipeById(@PathVariable String id, @RequestParam(value="token") String token, @RequestBody Recipe editedRecipe){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId; //ObjectId to store the provided string id.

            //Convert the id to an ObjectId. IllegalArgumentException will be thrown
            //if the string id is not a valid hex string representation of an ObjectId.
            try{
                recipeId = new ObjectId(id);

            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }

            try {
                //Gets the result of the update operation.
                UpdateResult result = _recipeCollection.replaceOne(eq("_id", new ObjectId(id)), editedRecipe);

                //If there was no exception thrown then the operation was successful. If the number of documents updated
                //is 0 then there were no matching documents with the id provided.
                if (result.getModifiedCount() == 0) {
                    return new ResponseEntity<>("Error: No recipe with provided id was found.", HttpStatus.NOT_FOUND);
                } else {
                    return new ResponseEntity<>("Successful update of recipe.", HttpStatus.OK);
                }

            } catch (MongoWriteException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (MongoWriteConcernException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (MongoException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (io.jsonwebtoken.SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value="/api/recipe/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteRecipeById(@PathVariable String id, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId; //ObjectId to store the provided string id.

            //Convert the id to an ObjectId. IllegalArgumentException will be thrown
            //if the string id is not a valid hex string representation of an ObjectId.
            try{
                recipeId = new ObjectId(id);

            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }

            try {
                //Gets the result of the delete operation.
                DeleteResult result = _recipeCollection.deleteOne(eq("_id", recipeId));

                //If there was no exception thrown then the operation was successful. If the number of documents deleted
                //is 0 then there were no matching documents with the id provided.
                if (result.getDeletedCount() == 0) {
                    return new ResponseEntity<>("Error: No recipe with provided id was found.", HttpStatus.NOT_FOUND);
                } else {
                    return new ResponseEntity<>("Successful deletion of recipe.", HttpStatus.OK);
                }

            } catch (MongoWriteException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (MongoWriteConcernException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (MongoException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (io.jsonwebtoken.SignatureException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }
}
