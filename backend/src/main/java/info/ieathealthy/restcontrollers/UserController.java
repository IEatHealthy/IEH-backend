package info.ieathealthy.restcontrollers;

import info.ieathealthy.models.ClientUser;
import info.ieathealthy.models.ProtectedUser;
import info.ieathealthy.models.Badge;
import info.ieathealthy.models.Title;
import info.ieathealthy.models.FullyPopulatedUser;
import io.jsonwebtoken.ClaimJwtException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import info.ieathealthy.models.User;
import org.mindrot.jbcrypt.BCrypt;
import org.bson.types.ObjectId;

//JWT imports
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.security.*;
import java.util.ArrayList;

@RestController
public class UserController {
    //extra stuff stored already
    //even tho not yet used
    private final MongoClient _client;
    private final CodecRegistry _userCodec;
    private final MongoDatabase _userDb;
    private final MongoCollection<User> _userCollection;
    private final MongoCollection<Badge> _badgeCollection;
    private final MongoCollection<Title> _titleCollection;
    private final Key _sigKey;
//    private final Key jwtKey;

    public UserController(@Qualifier("mongoClient") final MongoClient client, @Qualifier("mongoCodecRegistry") final CodecRegistry registry, final Key sigKey){
        //initialize mongo stuff for use
        this._client = client;
        this._userCodec = registry;
        this._userDb = client.getDatabase("i-eat-healthy");
        this._userCollection = this._userDb.getCollection("users", User.class).withCodecRegistry(registry);
        this._badgeCollection = this._userDb.getCollection("badges", Badge.class).withCodecRegistry(registry);
        this._titleCollection = this._userDb.getCollection("titles", Title.class).withCodecRegistry(registry);
        this._sigKey = sigKey;
    }

    //TODO handle all edge cases
    //@Return: returns a JWT token on successful authentication; otherwise returns appropriate error
    @RequestMapping(value="/api/user/{email}/{password}", method=RequestMethod.GET)
    public ResponseEntity<?> getUserAccount (@PathVariable String email, @PathVariable String password){

        //in future will want to check to make sure only
        //one user account is associated with email

        User userInfo = _userCollection.find(eq("email", email)).first();
        if(userInfo != null) {
            boolean authorized = BCrypt.checkpw(password, userInfo.getHash());

            if (authorized) {
                //give tokens default expiration 2 weeks from
                //date of issue
                Calendar calendar = Calendar.getInstance();
                Date issueDate = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, 14);
                Date dateOfExpiration = calendar.getTime();

                //create a Web Token to return
                //Use Mac for now, switch to RsaProvider in future
//                Key key = MacProvider.generateKey();

                //more claims should be added to token as API evolves
                //build a JWT for i-eat-healthy application
                String userAccessToken = Jwts.builder()
                        .setSubject(email)
                        .setExpiration(dateOfExpiration)
                        .setIssuedAt(issueDate)
                        .setAudience("i-eat-healthy")
                        .claim("permission", "user")
                        .signWith(SignatureAlgorithm.HS512, _sigKey)
                        .compact();
                //return with the token and the appropriate http status
                return new ResponseEntity<>(userAccessToken, HttpStatus.OK);


            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    //endpoint handles changing a password, provided the appropriate JWT is supplied.
    @RequestMapping(value="/api/user/{email}", method=RequestMethod.PUT)
    public ResponseEntity<?> changeUserPassword(@PathVariable String email, @RequestParam(value = "token", required = true) String token, @RequestBody String newPassword){
        try {
            //Not the best way of doing it. Should really store JWT in db on creation
            //and check to make sure that both signature keys match
            //fancy manipulation may be able to get through this
            Jwts.parser().setSigningKey(this._sigKey).parseClaimsJws(token).getBody().getSubject().equals(email);

            try {
                //Token has been verified so we need to hash the new password.
                String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                _userCollection.updateOne(eq("email", email), set("hash", newHash));

                //if we reach here with no exceptions, the password has been successfully changed
                //and we can return 200
                return new ResponseEntity<>(HttpStatus.OK);

                //catch write exception, could be caused by a variety of things
                //but we will default to not found, aka email does not exist
                //in future will need to be more descriptive
            } catch (Exception e){
                return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
            }
            //catch exception with verifying signature of JWT
        } catch (ClaimJwtException e){
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value="/api/user/{email}", method=RequestMethod.POST)
    public ResponseEntity<?> insertUser(@PathVariable("email") String email, @RequestBody ClientUser cu){
        //first check if the user is unique in the database
        User dCheck1 = _userCollection.find(eq("email", cu.getEmail())).first();
        User dCheck2 = _userCollection.find(eq("username", cu.getUsername())).first();

        if(dCheck1 != null || dCheck2 != null){
            return new ResponseEntity<>("Email/username already exists.", HttpStatus.CONFLICT);
        } else {
            //now generate password hash, create User, and submit
            String hash = BCrypt.hashpw(cu.getPassword(), BCrypt.gensalt());
            User toSubmit = new User(cu, hash);

            try {
                _userCollection.insertOne(toSubmit);
            } catch (Exception e){
                return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //all has gone well, generate the token to access rest of API
            //give tokens default expiration 2 weeks from
            //date of issue
            Calendar calendar = Calendar.getInstance();
            Date issueDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            Date dateOfExpiration = calendar.getTime();

            //create a Web Token to return
            //Use Mac for now, switch to RsaProvider in future
            //Key key = MacProvider.generateKey();

            //more claims should be added to token as API evolves
            //build a JWT for i-eat-healthy application
            String userAccessToken = Jwts.builder()
                    .setSubject(email)
                    .setExpiration(dateOfExpiration)
                    .setIssuedAt(issueDate)
                    .setAudience("i-eat-healthy")
                    .claim("permission", "user")
                    .signWith(SignatureAlgorithm.HS512, _sigKey)
                    .compact();

            return new ResponseEntity<>(userAccessToken, HttpStatus.OK);
        }
    }

    @RequestMapping(value="/api/user/{username}", method=RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(@PathVariable String username, @RequestParam(value= "token") String token){
        //first check signing key
        String email = null;
        try {
            email = Jwts.parser().setSigningKey(this._sigKey).parseClaimsJws(token).getBody().getSubject();
        } catch(ClaimJwtException e){
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        }

        //next check if user still has an account
        User valid = _userCollection.find(eq("email", email)).first();
        if(valid == null){
            return new ResponseEntity<>("Token is not valid.", HttpStatus.UNAUTHORIZED);
        }

        //if we got this far, we can now try to fetch the user they are looking for
        User toReturn = null;
        try {
            toReturn = _userCollection.find(eq("username", username)).first();
        } catch(Exception e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        if(toReturn == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            //convert object to safe version that can be returned to client
            return new ResponseEntity<>(new ProtectedUser(toReturn), HttpStatus.OK);
        }
    }

    @RequestMapping(value="/api/user/{email}/{password}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable String email, @PathVariable String password){
        //first find the account
        try {
            User userInfo = _userCollection.find(eq("email", email)).first();
            if (userInfo == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            boolean authorized = BCrypt.checkpw(password, userInfo.getHash());
            if (authorized) {
                _userCollection.deleteOne(eq("email", email));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //endpoint returns user data to account owner for the client application
    @RequestMapping(value="/api/user/accountOwner/{email}", method=RequestMethod.GET)
    public ResponseEntity<?> getAccountOwnerUserData(@PathVariable String email, @RequestParam(value="token") String token){
        //first verify the token
        try {
            Jwts.parser().setSigningKey(this._sigKey).parseClaimsJws(token).getBody().getSubject().equals(email);
            User user = _userCollection.find(eq("email", email)).first();

            if(user == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                try {
                    //create the fully populated user to return to client
                    //some fields are references so we must retrieve them before returning
                    FullyPopulatedUser toReturn = new FullyPopulatedUser(user);
                    toReturn.setBadgesEarned(getBadges(user.getBadgesEarned()));
                    toReturn.setTitlesEarned(getTitles(user.getTitlesEarned()));
                    toReturn.setBadgeSelected(getBadgeSet(user.getBadgeSelected()));
                    toReturn.setTitleSelected(getTitleSet(user.getTitleSelected()));

                    return new ResponseEntity<>(toReturn, HttpStatus.OK);
                } catch(Exception e){
                    return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch(ClaimJwtException e) {
            return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
        }

    }

    private ArrayList<Badge> getBadges(ArrayList<ObjectId> badgeIds){
        ArrayList<Badge> badgesFound = new ArrayList<Badge>();
        for(ObjectId badgeId: badgeIds){
            Badge toInsert = null;
            toInsert = _badgeCollection.find(eq("_id", badgeId)).first();
            if(toInsert != null){
                badgesFound.add(toInsert);
            }
        }

        return badgesFound;
    }

    private ArrayList<Title> getTitles(ArrayList<ObjectId> titleIds){
        ArrayList<Title> titlesFound = new ArrayList<Title>();
        for(ObjectId titleId: titleIds){
            Title toInsert = null;
            toInsert = _titleCollection.find(eq("_id", titleId)).first();
            if(toInsert != null){
                titlesFound.add(toInsert);
            }
        }

        return titlesFound;
    }

    private Badge getBadgeSet(ObjectId badgeId){
        return _badgeCollection.find(eq("_id", badgeId)).first();
    }

    private Title getTitleSet(ObjectId titleId){
        return _titleCollection.find(eq("_id", titleId)).first();
    }
    //creates a test user account in the database
//    @RequestMapping(value="/user", method=RequestMethod.POST)
//    public ResponseEntity<?> createTestUserAccount(){
//        try {
//            //default log rounds for BCrypt perfectly fine
//            //for our purposes
//            String hash = BCrypt.hashpw("test123", BCrypt.gensalt());
//
//            User toSubmit = new User("test@ieathealthy.info", "John", "Doe", hash);
//            _userCollection.insertOne(toSubmit);
//
//            return new ResponseEntity<>(toSubmit, HttpStatus.ACCEPTED);
//
//        } catch (Exception e){
//            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}
