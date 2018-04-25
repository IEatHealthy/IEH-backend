package info.ieathealthy.restcontrollers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.*;
import com.mongodb.MongoException;
import info.ieathealthy.models.Recipe;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import info.ieathealthy.models.Review;
import info.ieathealthy.models.UserReview;
import info.ieathealthy.models.User;
import info.ieathealthy.models.UserRating;
import info.ieathealthy.models.FrontRecipe;

import info.ieathealthy.models.Rating;
import java.util.ArrayList;


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
    private final MongoDatabase _dbIEH;
    private final MongoCollection<Recipe> _recipeCollection;
    private final MongoCollection<Review> _reviewCollection;
    private final MongoCollection<User> _userCollection;
    private final CodecRegistry _modelCodecRegistry;
    private final Key _sigKey;
    private static final int REVIEW_WORD_COUNT_LIMIT = 150;
    private static final int REVIEW_CHAR_COUNT_LIMIT = 1200;


    public RecipeController(@Qualifier("mongoClient") MongoClient client, @Qualifier("mongoCodecRegistry") CodecRegistry modelCodecRegistry, @Qualifier("jwtKeyFactory") Key sigKey){
        this._client = client;
        this._modelCodecRegistry = modelCodecRegistry;
        this._db = _client.getDatabase("food-data");
        this._dbIEH = _client.getDatabase("i-eat-healthy");
        this._recipeCollection = _db.getCollection("recipes", Recipe.class).withCodecRegistry(_modelCodecRegistry);
        this._reviewCollection = _dbIEH.getCollection("reviews", Review.class).withCodecRegistry(_modelCodecRegistry);
        this._userCollection = _dbIEH.getCollection("users", User.class).withCodecRegistry(_modelCodecRegistry);
        this._sigKey = sigKey;
    }


    //Some cases not accounted for yet but most of it is done.
    @RequestMapping(value="/api/recipe", method=RequestMethod.POST)
    public ResponseEntity<?> addRecipe(@RequestBody FrontRecipe recipe){
        try {
            //Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            //At the least check these values to make sure that they're not blank.
            if (recipe.getName() == null || recipe.getIngredients() == null || recipe.getSteps() == null || recipe.getAuthor() == null ||
                    recipe.getServings() == 0 || recipe.getPrepTime() == 0 || recipe.getCookTime() == 0 || recipe.getReadyInTime() == 0){

                return new ResponseEntity<>("Error: Submitted values blank with exception of nutritional values.", HttpStatus.BAD_REQUEST);
            }

            //Create Recipe object because otherwise we would have to create another MongoCollection
            //with the type FrontRecipe.
            Recipe recipeToInsert = new Recipe(recipe.getName(),recipe.getTypeOfFood(),recipe.getDifficulty(),recipe.getServings(),recipe.getPrepTime(),
                                          recipe.getCookTime(),recipe.getReadyInTime(),recipe.getIngredients(),recipe.getSteps(),recipe.getToolsNeeded(),
                                          recipe.getDescription(),recipe.getAuthor(),recipe.getCalories(),recipe.getProtein(),recipe.getFat(),
                                          recipe.getCarbohydrate(),recipe.getFiber(),recipe.getSugar(),recipe.getCalcium(),recipe.getIron(),recipe.getPotassium(),
                                          recipe.getSodium(),recipe.getVitaminC(),recipe.getVitAiu(),recipe.getVitDiu(),recipe.getCholestrol(), recipe.getFoodImage());

            _recipeCollection.insertOne(recipeToInsert);

            return new ResponseEntity<>("Success: Recipe was created.", HttpStatus.OK);


        } catch (SignatureException | ExpiredJwtException  e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }
    /*
    @RequestMapping(value="/api/recipe/{name}", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeByName(@PathVariable String name, @RequestParam(value="token") String token){
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

        } catch (SignatureException | ExpiredJwtException e) {
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
                UpdateResult result = _recipeCollection.replaceOne(eq("_id", recipeId), editedRecipe);

                //If there was no exception thrown then the operation was successful. If the number of documents updated
                //is 0 then there were no matching documents with the id provided.
                if (result.getModifiedCount() == 0) {
                    return new ResponseEntity<>("Error: No recipe with provided id was found.", HttpStatus.NOT_FOUND);
                } else {
                    return new ResponseEntity<>("Successful update of recipe.", HttpStatus.OK);
                }

            } catch (MongoException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (io.jsonwebtoken.SignatureException | io.jsonwebtoken.ExpiredJwtException e) {
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

            } catch (MongoException e) {
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeReviewsById(@PathVariable String id, @RequestParam(value="token") String token) {

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId; //ObjectId to store the provided string id.

            //Convert the id to an ObjectId. IllegalArgumentException will be thrown
            //if the string id is not a valid hex string representation of an ObjectId.
            try {
                recipeId = new ObjectId(id);

            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }

            Review recipeReviews = _reviewCollection.find(eq("_id", recipeId)).first();

            if (recipeReviews == null) {
                return new ResponseEntity<>("Error: No recipe reviews with provided id were found.", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(recipeReviews.getReviews(), HttpStatus.OK);
            }

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.POST)
    public ResponseEntity<?> addRecipeReviewById(@PathVariable String id, @RequestParam(value="token") String token,
                                                 @RequestBody UserReview newReview){

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

            User user = _userCollection.find(eq("_id", newReview.getUserId())).first();
            Recipe recipe = _recipeCollection.find(eq("_id", recipeId)).first();
            Review recipeReviews = _reviewCollection.find(eq("_id", recipeId)).first();
            int wordCount = 0; //Word count of review.

            //Check how many words are in the review by counting the spaces.
            for (int i = 0; i < newReview.getUserReview().length(); i++) {
                if (newReview.getUserReview().charAt(i) == ' ') {
                    wordCount++;
                }
            }

            //Check if user and recipe exist. Also checks that the review has not exceeded the limits.
            if (user == null) {
                return new ResponseEntity<>("Error: No user exists with provided userId.", HttpStatus.NOT_FOUND);
            } else if (recipe == null) {
                return new ResponseEntity<>("Error: No recipe exists with provided id.", HttpStatus.NOT_FOUND);
            } else if (newReview.getUserReview().length() > REVIEW_CHAR_COUNT_LIMIT) {
                return new ResponseEntity<>("Error: Length of review has exceeded 1200 characters.", HttpStatus.BAD_REQUEST);
            } else if (wordCount > REVIEW_WORD_COUNT_LIMIT) {
                return new ResponseEntity<>("Error: 150 word count limit of review has been exceeded.", HttpStatus.BAD_REQUEST);
            }

            //If recipeReviews is null then there are no reviews for this specific recipe yet.
            //New Review object must be created with the _id field as recipeId.
            //Otherwise, add the new review to the list of reviews.
            if (recipeReviews == null) {
                ArrayList<UserReview> newUserReview = new ArrayList<>();
                newUserReview.add(newReview);
                Review review = new Review(recipeId, newUserReview);
                _reviewCollection.insertOne(review);
            } else {
                ArrayList<UserReview> reviews = recipeReviews.getReviews();

                for (int i = 0; i < reviews.size(); i++) {
                    //If any of the existing reviews have matching userIds with the newReview then the
                    //user has already written a review for the recipe.
                    if (reviews.get(i).getUserId().compareTo(newReview.getUserId()) == 0) {
                        return new ResponseEntity<>("Error: User has already written a review for this recipe.", HttpStatus.CONFLICT);
                    }
                }

                recipeReviews.getReviews().add(newReview);
                _reviewCollection.findOneAndReplace(eq("_id", recipeId), recipeReviews);
            }

            return new ResponseEntity<>("Recipe review successful.", HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.PUT)
    public ResponseEntity<?> updateReviewById(@PathVariable String id, @RequestParam(value="token") String token,
                                              @RequestBody UserReview updatedReview) {

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

            Review recipeReviews = _reviewCollection.find(eq("_id", recipeId)).first();
            int wordCount = 0; //Word count of review.

            //Check how many words are in the review by counting the spaces.
            for (int i = 0; i < updatedReview.getUserReview().length(); i++) {
                if (updatedReview.getUserReview().charAt(i) == ' ') {
                    wordCount++;
                }
            }

            //Also checks that the review has not exceeded the limits.
            if (recipeReviews == null) {
                return new ResponseEntity<>("Error: Recipe has no existing reviews.", HttpStatus.NOT_FOUND);
            } else if (updatedReview.getUserReview().length() > REVIEW_CHAR_COUNT_LIMIT) {
                return new ResponseEntity<>("Error: Length of review has exceeded 1200 characters.", HttpStatus.BAD_REQUEST);
            } else if (wordCount > REVIEW_WORD_COUNT_LIMIT) {
                return new ResponseEntity<>("Error: 150 word count limit of review has been exceeded.", HttpStatus.BAD_REQUEST);
            }

            //Get the reviews of the recipe.
            ArrayList<UserReview> reviews = recipeReviews.getReviews();

            //Iterate through the reviews until we find matching user. Then update the review.
            for (int i = 0; i < reviews.size(); i++) {
                //compareTo() returns 0 if the ObjectIds are equal and -1 if they are not equal.
                if (reviews.get(i).getUserId().compareTo(updatedReview.getUserId()) == 0) {
                    recipeReviews.getReviews().get(i).setUserReview(updatedReview.getUserReview());
                    _reviewCollection.findOneAndReplace(eq("_id", recipeId), recipeReviews);

                    return new ResponseEntity<>("Review has been updated successfully.", HttpStatus.OK);
                }
            }

            //If it gets to this point then it means that the provided userId did not match any of the userIds in the reviews.
            return new ResponseEntity<>("Error: User has yet to review recipe with provided id.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }


    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteRecipeReviewById(@PathVariable String id, @RequestParam(value="userId") String userId,
                                                    @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId; //ObjectId to store the provided string id.
            ObjectId actualUserId;

            //Convert the id to an ObjectId. IllegalArgumentException will be thrown
            //if the string id is not a valid hex string representation of an ObjectId.
            try{
                recipeId = new ObjectId(id);
                actualUserId = new ObjectId(userId);

            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }

            Review recipeReviews = _reviewCollection.find(eq("_id", recipeId)).first();

            //If recipeReviews is null then the recipe has no reviews yet so has not written a review for the recipe.
            if (recipeReviews == null) {
                return new ResponseEntity<>("Error: Recipe has no existing reviews.", HttpStatus.NOT_FOUND);
            } else {
                ArrayList<UserReview> reviews = recipeReviews.getReviews();

                //Find matching user id and delete the review associated with the user.
                for (int i = 0; i < reviews.size(); i++) {
                    if (reviews.get(i).getUserId().compareTo(actualUserId) == 0){
                        recipeReviews.getReviews().remove(i);
                        _reviewCollection.findOneAndReplace(eq("_id", recipeId), recipeReviews);

                        return new ResponseEntity<>("Review was removed successfully.", HttpStatus.OK);
                    }
                }

                //If it gets here then the user has not written a review for the recipe.
                return new ResponseEntity<>("Error: User has not written a review for recipe with the provided id.", HttpStatus.NOT_FOUND);
            }


        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }


    /*Just Starting on it.
    @RequestMapping(value="/api/recipe/{id}/rate", method=RequestMethod.POST)
    public ResponseEntity<?> rateRecipeById(@PathVariable String id, @RequestBody UserRating newRating){

        try {
            //@RequestParam(value="token") String token
            //Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId; //ObjectId to store the provided string id.

            //Convert the id to an ObjectId. IllegalArgumentException will be thrown
            //if the string id is not a valid hex string representation of an ObjectId.
            try{
                recipeId = new ObjectId(id);

            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }

            User user = _userCollection.find(eq("_id", newRating.getUserId())).first();
            Recipe recipe = _recipeCollection.find(eq("_id", recipeId)).first();

            //delete
            return new ResponseEntity<>("hello", HttpStatus.BAD_REQUEST);


        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }
    */

}
