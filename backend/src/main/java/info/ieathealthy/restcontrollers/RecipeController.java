package info.ieathealthy.restcontrollers;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.*;
import com.mongodb.client.FindIterable;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import info.ieathealthy.models.Recipe;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import info.ieathealthy.models.Review;
import info.ieathealthy.models.UserReview;
import info.ieathealthy.models.User;
import info.ieathealthy.models.UserRating;
import info.ieathealthy.models.FrontRecipe;
import info.ieathealthy.models.StarRating;
import info.ieathealthy.models.RecipeRating;
import info.ieathealthy.models.Token;


import java.util.ArrayList;


import java.security.Key;



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
    private final MongoCollection<RecipeRating> _ratingCollection;
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
        this._ratingCollection = _dbIEH.getCollection("ratings", RecipeRating.class).withCodecRegistry(_modelCodecRegistry);
        this._sigKey = sigKey;
    }


    @RequestMapping(value="/api/recipe", method=RequestMethod.POST)
    public ResponseEntity<?> addRecipe(@RequestBody FrontRecipe recipe, @RequestParam(value="userId") String userId, @RequestParam(value="token") String token){
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            User user = _userCollection.find(eq("_id", new ObjectId(userId))).first();

            //Check that the user exists and at the least check the important values to make sure that they're not null or 0.
            if (user == null) {
                return new ResponseEntity<>("Error: Could not find user with provider user id", HttpStatus.NOT_FOUND);

            } else if (recipe.getName() == null || recipe.getIngredients() == null || recipe.getSteps() == null || recipe.getAuthor() == null ||
                    recipe.getServings() == 0 || recipe.getPrepTime() == 0 || recipe.getCookTime() == 0 || recipe.getReadyInTime() == 0 ||
                    recipe.getName().length() == 0 || recipe.getAuthor().length() == 0 || recipe.getFoodImage() == null){

                return new ResponseEntity<>("Error: Submitted values blank with exception of nutritional values.", HttpStatus.BAD_REQUEST);
            }

            ObjectId recipeId = new ObjectId(); //_id to store with the recipe


            //Note: It is highly unlikely that we have two equivalent id's. There is probably some better way of doing this
            //but this is the easiest right now.

            //Will return a recipe if a recipe is found with the id that was just created for the new recipe.
            //_id's have to be unique.
            Recipe recipeWithSameId = _recipeCollection.find(eq("_id", recipeId)).first();

            //Keep creating an id until we get one that is not already taken.
            while (recipeWithSameId != null) {
                recipeId = new ObjectId();
                recipeWithSameId = _recipeCollection.find(eq("_id", recipeId)).first();
            }


            //TODO we'll somehow have to get the ingredient ids of the ingredients recipes. We'll probably have to do that manually

            //Create Recipe object because otherwise we would have to create another MongoCollection
            //with the type FrontRecipe.
            Recipe recipeToInsert = new Recipe(recipeId, recipe.getName(),recipe.getTypeOfFood(),recipe.getDifficulty(),recipe.getServings(),recipe.getPrepTime(),
                                          recipe.getCookTime(),recipe.getReadyInTime(),recipe.getIngredients(),recipe.getSteps(),recipe.getToolsNeeded(),
                                          recipe.getDescription(),recipe.getAuthor(),recipe.getCalories(),recipe.getProtein(),recipe.getFat(),
                                          recipe.getCarbohydrate(),recipe.getFiber(),recipe.getSugar(),recipe.getCalcium(),recipe.getIron(),recipe.getPotassium(),
                                          recipe.getSodium(),recipe.getVitaminC(),recipe.getVitAiu(),recipe.getVitDiu(),recipe.getCholestrol(), recipe.getFoodImage());

            //Add the ObjectId of the new recipe created to the

            if (user.getRecipesCreated() == null) {
                user.setRecipesCreated(new ArrayList<ObjectId>());
            }

            user.getRecipesCreated().add(recipeId);

            //Add the created recipe to the db.
            _recipeCollection.insertOne(recipeToInsert);

            //Update the user data so that the newly created recipe is added to their list of created recipes.
            UpdateResult result = _userCollection.replaceOne(eq("_id", user.getId()), user);

            if (result.getModifiedCount() == 0) {
                return new ResponseEntity<>("Error: Could not store identifier of newly created recipe in user's data.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>("Success: Recipe was created.", HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException  e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (MongoException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value="/api/recipe/name", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeByName(@RequestParam(value="name") String name, @RequestParam(value="token") String token){
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);


            Recipe recipe = _recipeCollection.find(eq("name", name)).first();

            if (recipe == null) {
                return new ResponseEntity<>("Error: Recipe with provided name was not found.", HttpStatus.NOT_FOUND);
            }


            FrontRecipe recipeToReturn = new FrontRecipe(recipe);

            return new ResponseEntity<>(recipeToReturn, HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }


    @RequestMapping(value="/api/recipe/id", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeById(@RequestParam(value="id") String id){

        try {

            //Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);


            Recipe recipe = _recipeCollection.find(eq("_id",  new ObjectId(id))).first();

            //If the recipe is null then there were no matching recipes with the provided id. Otherwise the
            //recipe was found and is returned.
            if (recipe == null) {
                return new ResponseEntity<>("Error: Recipe with provided id was not found.", HttpStatus.NOT_FOUND);
            }

            FrontRecipe recipeToReturn = new FrontRecipe(recipe);

            return new ResponseEntity<>(recipeToReturn, HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value="/api/recipe/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateRecipeById(@PathVariable String id, @RequestParam(value="userId") String userId,
                                              @RequestBody FrontRecipe editedRecipe, @RequestParam(value="token") String token) {

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId = new ObjectId(id);
            ObjectId actualUserId = new ObjectId(userId);
            User user = _userCollection.find(eq("_id", actualUserId)).first();
            Recipe recipe = _recipeCollection.find(eq("_id", recipeId)).first();
            ArrayList<ObjectId> recipesCreated = user.getRecipesCreated();

            //Check that these values are not blank or 0.
            if (recipe == null) {
                return new ResponseEntity<>("Error: Recipe with provided id not found.", HttpStatus.NOT_FOUND);

            } else if (user == null) {
                return new ResponseEntity<>("Error: User with provided id not found.", HttpStatus.NOT_FOUND);

            } else if (editedRecipe.getSteps() == null || editedRecipe.getIngredients() == null || editedRecipe.getAuthor() == null ||
                       editedRecipe.getServings() == 0 || editedRecipe.getReadyInTime() == 0 || editedRecipe.getCookTime() == 0 ||
                       editedRecipe.getPrepTime() == 0) {

                return new ResponseEntity<>("Error: Fields cannot be blank or 0.", HttpStatus.BAD_REQUEST);
            }

            //Search through the identifiers of the recipes that the user has created. If one of them matches the
            //provided recipe id then the user actually created this recipe so they are allowed to modify it.
            for (int i = 0; i < recipesCreated.size(); i++) {

                if (recipesCreated.get(i).compareTo(recipeId) == 0) {
                    //Update the recipe data. Does not take parameters for name or image because they can't be modified.
                    recipe.updateRecipe(editedRecipe.getTypeOfFood(), editedRecipe.getDifficulty(), editedRecipe.getServings(), editedRecipe.getPrepTime(),
                            editedRecipe.getCookTime(), editedRecipe.getReadyInTime(), editedRecipe.getIngredients(), editedRecipe.getSteps(),
                            editedRecipe.getToolsNeeded(), editedRecipe.getDescription(), editedRecipe.getAuthor());

                    UpdateResult result = _recipeCollection.replaceOne(eq("_id", recipeId), recipe);

                    if (result.getModifiedCount() == 0) {
                        return new ResponseEntity<>("Error: Recipe could not be updated.", HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    return new ResponseEntity<>("Success: Recipe was updated.", HttpStatus.OK);
                }
            }

            return new ResponseEntity<>("Error: User did not create this recipe.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (MongoException | IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value="/api/recipe/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteRecipeById(@PathVariable String id, @RequestParam(value="userId") String userId, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId = new ObjectId(id);

            User user = _userCollection.find(eq("_id", new ObjectId(userId))).first();
            ArrayList<ObjectId> recipesCreated = user.getRecipesCreated();


            //Iterates through the array of recipe identifiers of the recipes that the user created to check if the recipeId
            //provided matches one of the recipe identifiers. If there is a match with one of them then they created it so
            //we go ahead with the delete operation.
            for (int i = 0; i < recipesCreated.size(); i++) {

                if (recipesCreated.get(i).compareTo(recipeId) == 0) {
                    //Gets the result of the delete operation.
                    DeleteResult result = _recipeCollection.deleteOne(eq("_id", recipeId));

                    //If there was no exception thrown then the operation was successful. If the number of documents deleted
                    //is 0 then there were no matching documents with the id provided.
                    if (result.getDeletedCount() == 0) {
                        return new ResponseEntity<>("Error: Could not delete recipe.", HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    return new ResponseEntity<>("Success: Recipe was deleted.", HttpStatus.OK);
                }
            }

            //If it gets here, no matching recipeId was found in the recipesCreated array of the user. Thus the did
            //not create the recipe.
            return new ResponseEntity<>("Error: User with provided id did not create recipe.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (MongoException | IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //Gets all the reviews of the recipe. Should be sufficient for now.
    //Might change at a later time to only get a certain number at a time.
    @RequestMapping(value="/api/recipe/{id}/reviews", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeReviewsById(@PathVariable String id, @RequestParam(value="token") String token) {

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            //Note: We can check if the provided recipe id references a recipe in the database but I don't think that's necessary.

            Review recipeReviews = _reviewCollection.find(eq("_id", new ObjectId(id))).first();

            //Return an error reviews for the recipe were not found. Otherwise return the reviews.
            if (recipeReviews == null) {
                return new ResponseEntity<>("Error: No reviews with provided recipe id were found.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(recipeReviews.getReviews(), HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }


    //Gets the review of a specific user.
    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeReviewById(@PathVariable String id, @RequestParam(value="userId") String userId, @RequestParam(value="token") String token) {

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId = new ObjectId(id);         //Convert the recipe string id to an ObjectId.
            ObjectId actualUserId = new ObjectId(userId); //Convert the user string id to an ObjectId.

            //Note: We can check if the provided recipe id references a recipe in the database but I don't think that's necessary.

            Review recipeReviews = _reviewCollection.find(eq("_id", recipeId)).first();

            //Return an error reviews for the recipe were not found. Otherwise return the reviews.
            if (recipeReviews == null) {
                return new ResponseEntity<>("Error: No recipe reviews with provided id were found.", HttpStatus.NOT_FOUND);
            }

            ArrayList<UserReview> reviews = recipeReviews.getReviews();

            //Search for user with the provided id to find their review.
            for (int i = 0 ; i < reviews.size(); i++) {

                if (reviews.get(i).getUserId().compareTo(actualUserId) == 0) {
                    return new ResponseEntity<>(reviews.get(i).getUserReview(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>("Error: No review found with provided user id.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.POST)
    public ResponseEntity<?> addRecipeReviewById(@PathVariable String id, @RequestBody UserReview newReview, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            int wordCount = 0;                    //Word count of review.
            ObjectId recipeId = new ObjectId(id); //Convert the string recipe id to an ObjectId.
            User user = _userCollection.find(eq("_id", newReview.getUserId())).first();
            Recipe recipe = _recipeCollection.find(eq("_id", recipeId)).first();
            Review recipeReviews = _reviewCollection.find(eq("_id", recipeId)).first();

            //Check how many words are in the review by counting the spaces.
            for (int i = 0; i < newReview.getUserReview().length(); i++) {
                if (newReview.getUserReview().charAt(i) == ' ') {
                    wordCount++;
                }
            }

            //Check if user and recipe exist. Also checks that the review has not exceeded the limits.
            if (user == null) {
                return new ResponseEntity<>("Error: User with provided id does not exist.", HttpStatus.NOT_FOUND);
            } else if (recipe == null) {
                return new ResponseEntity<>("Error: Recipe with provided id does not exist.", HttpStatus.NOT_FOUND);
            } else if (newReview.getUserReview().length() > REVIEW_CHAR_COUNT_LIMIT) {
                return new ResponseEntity<>("Error: Length of review exceeds the 1200 characters limit.", HttpStatus.BAD_REQUEST);
            } else if (wordCount > REVIEW_WORD_COUNT_LIMIT) {
                return new ResponseEntity<>("Error: Word count of review exceeds the 150 word count limit..", HttpStatus.BAD_REQUEST);
            }

            //If recipeReviews is null then there are no reviews for this specific recipe yet.
            //New Review object must be created with the _id field as recipeId.
            //Otherwise, add the new review to the list of reviews.
            if (recipeReviews == null) {
                ArrayList<UserReview> newUserReview = new ArrayList<>();
                newUserReview.add(newReview);
                Review review = new Review(recipeId, newUserReview);
                _reviewCollection.insertOne(review);
                return new ResponseEntity<>("Success: Review was created.", HttpStatus.OK);
            }

            ArrayList<UserReview> reviews = recipeReviews.getReviews();

            //If any of the existing reviews have matching userIds with the newReview then the
            //user has already written a review for the recipe so just replace it with the new one.
            for (int i = 0; i < reviews.size(); i++) {

                if (reviews.get(i).getUserId().compareTo(newReview.getUserId()) == 0) {
                    //Update the review.
                    reviews.get(i).setUserReview(newReview.getUserReview());
                    UpdateResult result = _reviewCollection.replaceOne(eq("_id", recipeId), recipeReviews);

                    if (result.getModifiedCount() == 0) {
                        return new ResponseEntity<>("Error: Review could not be updated.", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return new ResponseEntity<>("Success: Review was updated.", HttpStatus.OK);
                }
            }

            //User has yet to rate the recipe so insert a UserReview object into the reviews.
            recipeReviews.getReviews().add(newReview);
            UpdateResult result = _reviewCollection.replaceOne(eq("_id", recipeId), recipeReviews);

            if (result.getModifiedCount() == 0) {
                return new ResponseEntity<>("Error: Review could not be created.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("Success: Review was created.", HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (MongoException | IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* was going to use this to update a review but the post endpoint can handle that.
    keeping in case it's needed

    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.PUT)
    public ResponseEntity<?> updateReviewById(@PathVariable String id, @RequestParam(value="token") String token,
                                              @RequestBody UserReview updatedReview) {

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            Review recipeReviews = _reviewCollection.find(eq("_id", new ObjectId(id))).first();
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
                    _reviewCollection.findOneAndReplace(eq("_id", new ObjectId(id)), recipeReviews);

                    return new ResponseEntity<>("Review has been updated successfully.", HttpStatus.OK);
                }
            }

            //If it gets to this point then it means that the provided userId did not match any of the userIds in the reviews.
            return new ResponseEntity<>("Error: User has yet to review recipe with provided id.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
    */



    @RequestMapping(value="/api/recipe/{id}/review", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteRecipeReviewById(@PathVariable String id, @RequestParam(value="userId") String userId,
                                                    @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId = new ObjectId(id);
            ObjectId actualUserId = new ObjectId(userId);
            Review recipeReviews = _reviewCollection.find(eq("_id", recipeId)).first();

            //If recipeReviews is null then the recipe has no reviews yet so user has not written a review for the recipe.
            //Or maybe the recipe id is invalid (we can go ahead and check if the recipe exists by searching for it. that
            //way we can provide better feedback.
            if (recipeReviews == null) {
                return new ResponseEntity<>("Error: Recipe with provided id has no existing reviews.", HttpStatus.NOT_FOUND);
            }

            ArrayList<UserReview> reviews = recipeReviews.getReviews();


            //Find matching user id and delete the review associated with the user.
            for (int i = 0; i < reviews.size(); i++) {

                if (reviews.get(i).getUserId().compareTo(actualUserId) == 0){
                    recipeReviews.getReviews().remove(i);
                    UpdateResult result = _reviewCollection.replaceOne(eq("_id", recipeId), recipeReviews);

                    if (result.getModifiedCount() == 0) {
                        return new ResponseEntity<>("Error: Review could not be deleted.", HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (reviews.isEmpty()) {
                        //Delete the document to be consistent. If it has no reviews we don't want it stored in the database.
                        _reviewCollection.deleteOne(eq("_id", recipeId));
                    }

                    return new ResponseEntity<>("Success: Review was removed.", HttpStatus.OK);
                }
            }

            //If it gets here then the user has not written a review for the recipe.
            return new ResponseEntity<>("Error: User has not written a review for recipe with the provided id.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (MongoException | IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Gets a specific rating for a user.
    @RequestMapping(value="/api/recipe/{id}/rating", method=RequestMethod.GET)
    public ResponseEntity<?> getRatingsById(@PathVariable String id, @RequestParam(value="userId") String userId, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);


            RecipeRating recipeRating = _ratingCollection.find(eq("_id", new ObjectId(id))).first();

            //If we want to be more precise with the error, we can search for a recipe with the given id and
            //if nothing is found then the recipe id is invalid. But this will probably do for now.

            //If recipeRating is null then either the recipe id is bad or rating for this recipe don't exist.
            if (recipeRating == null) {
                return new ResponseEntity<>("Error: Either the recipe id is invalid or ratings for the recipe don't exist yet.", HttpStatus.NOT_FOUND);
            }

            ArrayList<UserRating> ratings = recipeRating.getRatings();
            ObjectId actualUserId = new ObjectId(userId);

            //Look for the rating of the user and return it if found.
            for(int i = 0; i < ratings.size(); i++) {

                if (ratings.get(i).getUserId().compareTo(actualUserId) == 0) {
                    return new ResponseEntity<>(ratings.get(i).getUserRating(), HttpStatus.OK);
                }
            }

            //If it gets here, then a rating with the provided user id was not found.
            return new ResponseEntity<>("Error: Rating for the provided user id was not found.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    //Gets both the total rating and all the ratings.
    @RequestMapping(value="/api/recipe/{id}/ratings", method=RequestMethod.GET)
    public ResponseEntity<?> getRatingsById(@PathVariable String id, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            RecipeRating recipeRating = _ratingCollection.find(eq("_id", new ObjectId(id))).first();

            //If we want to be more precise with the error, we can search for a recipe with the given id and
            //if nothing is found then the recipe id is invalid. But this will probably do for now.

            //If recipeRating is null then either the recipe id is bad or rating for this recipe don't exist.
            if (recipeRating == null) {
                return new ResponseEntity<>("Error: Either the recipe id is invalid or ratings for the recipe don't exist", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(recipeRating, HttpStatus.OK);


        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value="/api/recipe/{id}/rate", method=RequestMethod.POST)
    public ResponseEntity<?> rateRecipeById(@PathVariable String id, @RequestBody UserRating newUserRating, @RequestParam(value="token") String token){

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId = new ObjectId(id);
            Boolean updatedRating = false; //Records whether the new rating is an update to a previous rating.
            User user = _userCollection.find(eq("_id", newUserRating.getUserId())).first();
            Recipe recipe = _recipeCollection.find(eq("_id", recipeId)).first();
            RecipeRating recipeRating = _ratingCollection.find(eq("_id", recipeId)).first();


            //Checks if user and recipe with provided ids exist. Also checks that the rating
            //is within the 1 - 5 range.
            if (user == null) {
                return new ResponseEntity<>("Error: User with provided id not found.", HttpStatus.NOT_FOUND);
            } else if (recipe == null) {
                return new ResponseEntity<>("Error: Recipe with provided id not found", HttpStatus.NOT_FOUND);
            } else if (newUserRating.getUserRating() == StarRating.INVALID.getStarRating()) {
                return new ResponseEntity<>("Error: Invalid rating for recipe. Not within range of 1 - 5", HttpStatus.BAD_REQUEST);
            }

            //If the recipeRating is null then the recipe has not been rated yet so we need to create
            //an element in the collection that contains the ratings of the recipe.
            if (recipeRating == null) {
                ArrayList<UserRating> userRatingList = new ArrayList<>();
                userRatingList.add(newUserRating);
                RecipeRating newRecipeRating = new RecipeRating(recipeId, userRatingList, newUserRating.getUserRating());
                _ratingCollection.insertOne(newRecipeRating);

                return new ResponseEntity<>("Success: Recipe was rated.", HttpStatus.OK);

            }

            //Total sum of the ratings.
            double sumOfRatings = 0;

            //If the user has already rated this recipe then update their rating.
            for (UserRating u : recipeRating.getRatings()) {

                if(u.getUserId().compareTo(newUserRating.getUserId()) == 0) {
                    u.setUserRating(newUserRating.getUserRating());
                    updatedRating = true;
                }
            }

            //If no rating was updated then the user has not previously rated the recipe so add their rating.
            if (!updatedRating) {
                recipeRating.getRatings().add(newUserRating);
            }

            //Sum the ratings.
            for (UserRating u: recipeRating.getRatings()) {
                sumOfRatings += u.getUserRating();
            }

            //Set the new total rating by dividing the sum of the ratings by the number of ratings.
            recipeRating.setTotalRating(sumOfRatings / recipeRating.getRatings().size());
            UpdateResult result = _ratingCollection.replaceOne(eq("_id", recipeId), recipeRating);

            //If the number of modified documents is 0 then something went wrong.
            if (result.getModifiedCount() == 0) {
                return new ResponseEntity<>("Error: Could not rate recipe.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("Success: Recipe was rated.", HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException | MongoException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/api/recipe/{id}/rate", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteRatingById(@PathVariable String id, @RequestParam(value="userId") String userId, @RequestParam(value="token") String token) {

        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId recipeId = new ObjectId(id);
            double sumOfRatings = 0; //Sum of the ratings


            RecipeRating recipeRating = _ratingCollection.find(eq("_id", recipeId)).first();

            //To make the error unambiguous we can check if the recipe id actually references a recipe.
            //Then we would know if the recipe id was actually valid or not.
            //We can also search for the user and return an error if the user is not found.

            //If recipeRating is null then either the recipe id is invalid or no ratings exist for the recipe.
            //Thus a rating cannot be deleted.
            //Otherwise, find the rating and delete it.
            if (recipeRating == null) {
                return new ResponseEntity<>("Error: Either an invalid recipe id was provided or recipe has not yet been rated." , HttpStatus.NOT_FOUND);
            }

            ArrayList<UserRating> ratingsHolder = recipeRating.getRatings();

            //Iterate through ratings to find matching user and remove their rating.
            for (int i = 0; i < ratingsHolder.size(); i++) {

                sumOfRatings += ratingsHolder.get(i).getUserRating();

                if (ratingsHolder.get(i).getUserId().compareTo(new ObjectId(userId)) == 0) {
                    System.out.println("\n\nIn here\n\n");
                    sumOfRatings -= ratingsHolder.get(i).getUserRating();
                    recipeRating.getRatings().remove(i);
                    i--;
                }
            }

            //Update the total rating. If sum is 0 then there are no more ratings and we don't want to divide by 0.
            //If it is 0, then remove the document from the collection.
            if (sumOfRatings != 0) {
                recipeRating.setTotalRating(sumOfRatings / recipeRating.getRatings().size());
            } else {

                //Here were are not only removing the rating but we are also removing the document from the database because
                //it doesn't have anymore ratings.

                //Attempt to update the result.
                UpdateResult result = _ratingCollection.replaceOne(eq("_id", recipeId), recipeRating);
                System.out.println("in else: " + recipeRating.getRatings().size() + "\n\n");

                //If a recipe has no ratings then remove the document from the database.
                _ratingCollection.deleteOne(eq("_id", recipeId));

                //If 0 documents were modified then something went wrong.
                if (result.getModifiedCount() == 0 ) {
                    return new ResponseEntity<>("Error: Could not delete rating.", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity<>("Success: Rating removed.", HttpStatus.OK);
            }

            //Here we are only removing the rating from the document.

            //Update element in collection.
            UpdateResult result = _ratingCollection.replaceOne(eq("_id", recipeId), recipeRating);

            //Should not execute unless something bad has happened.
            if (result.getModifiedCount() == 0) {
                return new ResponseEntity<>("Error: Rating could not be removed.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("Success: Rating removed.", HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        } catch (MongoException | IllegalArgumentException e) {
            return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/api/recipe/{search}", method=RequestMethod.GET)
    public ResponseEntity<?> getRecipeBySearch(@PathVariable(value="search") String search, @RequestParam(value="token") String token){
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            //List to store the matching recipes.
            ArrayList<FrontRecipe> matchingRecipes = new ArrayList<>();

            //Simple search that searches a recipe name for whatever the user is searching for.
            FindIterable<Recipe> recipesFound = _recipeCollection.find(regex("name", search, "i"));

            //Puts the recipes found into the ArrayList so they can be returned.
            for (Recipe rec: recipesFound) {
                FrontRecipe convertedRec = new FrontRecipe(rec);
                matchingRecipes.add(convertedRec);
            }

            if (matchingRecipes.size() != 0) {
                return new ResponseEntity<>(matchingRecipes, HttpStatus.OK);
            }

            return new ResponseEntity<>("Error: Could not find recipes matching input.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }

    //Used to display 20 recipes that the user can see. They are like featured/recommended recipes. The recipes displayed
    //will depend on the user's cooking skill and the top rated recipes. Because there are no rated recipes right now,
    //I won't be implementing it. It'll just get the first 20 recipes found based on the user's cooking skill.
    @RequestMapping(value="/api/recipe/recommended/{email}", method=RequestMethod.GET)
    public ResponseEntity<?> getRecommendedRecipes(@PathVariable(value="email") String email, @RequestParam(value = "token") String token){
        try {

            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            User user = _userCollection.find(eq("email", email)).first();
            ArrayList<FrontRecipe> recipesToReturn = new ArrayList<>();

            if (user == null) {
                return new ResponseEntity<>("Error: User not found.", HttpStatus.NOT_FOUND);
            }

            //Search for recipes based on their skill level and only get the first 20.
            FindIterable<Recipe> recipesFound = _recipeCollection.find(eq("difficulty", user.getSkillLevel())).limit(20);

            //Puts the recipes found into the ArrayList so they can be returned.
            for (Recipe rec: recipesFound) {
                FrontRecipe convertedRec = new FrontRecipe(rec);
                recipesToReturn.add(convertedRec);
            }

            //Shouldn't happen.
            if (recipesToReturn.size() == 0) {
                return new ResponseEntity<>("Error: No recipes found for some reason.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(recipesToReturn, HttpStatus.OK);


        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }


    @RequestMapping(value="/api/recipe/bookmark", method=RequestMethod.POST)
    public ResponseEntity<?> addBookmark(@RequestParam(value="recipeId") String recipeId, @RequestParam(value="userId") String userId, @RequestParam(value="token") String token){
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId actualRecipeId = new ObjectId(recipeId);

            Recipe recipe = _recipeCollection.find(eq("_id", actualRecipeId)).first();
            User user = _userCollection.find(eq("_id", new ObjectId(userId))).first();

            if (recipe == null) {
                return new ResponseEntity<>("Error: Recipe with provided id does not exist.", HttpStatus.NOT_FOUND);

            } else if (user == null) {
                return new ResponseEntity<>("Error: User with provided id does not exist.", HttpStatus.NOT_FOUND);
            }

            ArrayList<ObjectId> bookmarkedRecipes = user.getBookmarkedRecipes();

            if (bookmarkedRecipes == null) {
                bookmarkedRecipes = new ArrayList<ObjectId>();
            }

            //Make sure that user has not already bookmarked recipe.
            for (int i = 0; i < bookmarkedRecipes.size(); i++) {

                if (bookmarkedRecipes.get(i).compareTo(actualRecipeId) == 0) {
                    return new ResponseEntity<>("Error: User has already bookmarked recipe.", HttpStatus.CONFLICT);
                }
            }

            bookmarkedRecipes.add(actualRecipeId);
            user.setBookmarkedRecipes(bookmarkedRecipes);

            UpdateResult result = _userCollection.replaceOne(eq("_id", new ObjectId(userId)), user);

            if (result.getModifiedCount() == 0) {
                return new ResponseEntity<>("Error: Could not add bookmark.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("Success: Bookmark was added.", HttpStatus.OK);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (MongoException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/api/recipe/bookmark", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteBookmark(@RequestParam(value="recipeId") String recipeId, @RequestParam(value="userId") String userId, @RequestParam(value="token") String token){
        try {
            Jwts.parser().setSigningKey(_sigKey).parseClaimsJws(token);

            ObjectId actualUserId = new ObjectId(userId);
            ObjectId actualRecipeId = new ObjectId(recipeId);

            User user = _userCollection.find(eq("_id", actualUserId)).first();
            ArrayList<ObjectId> bookmarkedRecipes = user.getBookmarkedRecipes();


            if (user == null) {
                return new ResponseEntity<>("Error: User with provided id does not exist.", HttpStatus.NOT_FOUND);
            } else if (bookmarkedRecipes == null || bookmarkedRecipes.size() == 0) {
                return new ResponseEntity<>("Error: User has no bookmarked recipes.", HttpStatus.NOT_FOUND);
            }


            //Iterates through the bookmarked recipes to find the one to be deleted.
            for (int i = 0; i < bookmarkedRecipes.size(); i++) {

                if (bookmarkedRecipes.get(i).compareTo(actualRecipeId) == 0) {
                    user.getBookmarkedRecipes().remove(i);

                    UpdateResult result = _userCollection.replaceOne(eq("_id", actualUserId), user);

                    if (result.getModifiedCount() == 0) {
                        return new ResponseEntity<> ("Error: Could not remove bookmarked recipe.", HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    return new ResponseEntity<>("Success: Removed bookmarked recipe.", HttpStatus.OK);
                }
            }

            //If it gets here then there was no matching bookmarked recipe id matching the one provided.
            return new ResponseEntity<>("Error: Provided recipe is not bookmarked for user.", HttpStatus.NOT_FOUND);

        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        } catch (MongoException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

