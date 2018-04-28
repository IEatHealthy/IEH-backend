package info.ieathealthy.restcontrollers;

import info.ieathealthy.models.ClientUser;
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

//JWT imports
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.security.*;

@RestController
public class UserController {
    //extra stuff stored already
    //even tho not yet used
    private final MongoClient _client;
    private final CodecRegistry _userCodec;
    private final MongoDatabase _userDb;
    private final MongoCollection<User> _userCollection;
    private final Key _sigKey;
//    private final Key jwtKey;

    public UserController(@Qualifier("mongoClient") final MongoClient client, @Qualifier("mongoCodecRegistry") final CodecRegistry registry, final Key sigKey){
        //initialize mongo stuff for use
        this._client = client;
        this._userCodec = registry;
        this._userDb = client.getDatabase("i-eat-healthy");
        this._userCollection = this._userDb.getCollection("users", User.class).withCodecRegistry(registry);
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
    @RequestMapping(value="/api/user/{email}", method=RequestMethod.POST)
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

    @RequestMapping(value="/api/user/{email}", method=RequestMethod.PUT)
    public ResponseEntity<?> insertUser(@PathVariable("email") String email, @RequestBody ClientUser cu){
        //first check if the user is unique in the database
        User dCheck1 = _userCollection.find(eq("email", cu.getEmail())).first();
        User dCheck2 = _userCollection.find(eq("username", cu.getUsername())).first();

        if(dCheck1 == null && dCheck2 == null){
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

            return new ResponseEntity<>(userAccessToken, HttpStatus.OK);
        }
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
