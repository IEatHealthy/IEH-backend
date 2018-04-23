package info.ieathealthy.restcontrollers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import info.ieathealthy.models.User;
import org.mindrot.jbcrypt.BCrypt;

//JWT imports
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

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
                return new ResponseEntity<>(userAccessToken, HttpStatus.FOUND);


            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
