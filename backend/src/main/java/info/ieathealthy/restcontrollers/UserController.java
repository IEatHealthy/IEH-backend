package info.ieathealthy.restcontrollers;

import org.bson.types.Code;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import info.ieathealthy.models.Ingredient;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

@RestController
public class UserController {
    //extra stuff stored already
    //even tho not yet used
    private final MongoClient _client;
    private final CodecRegistry _userCodec;
    private final MongoDatabase _userDb;
    private final MongoCollection<Document> _userCollection;

    public UserController(@Qualifier("mongoClient") final MongoClient client, @Qualifier("mongoCodecRegistry") final CodecRegistry registry){
        //initialize mongo stuff for use
        this._client = client;
        this._userCodec = registry;
        this._userDb = client.getDatabase("i-eat-healthy");
        this._userCollection = this._userDb.getCollection("users");
    }

    @RequestMapping(value="/user", method=RequestMethod.GET)
    public Document getUserAccount (@RequestParam(value="email") String email){

        //in future will want to check to make sure only
        //one user account is associated with email

        return _userCollection.find(eq("email", email)).first();
    }

}
