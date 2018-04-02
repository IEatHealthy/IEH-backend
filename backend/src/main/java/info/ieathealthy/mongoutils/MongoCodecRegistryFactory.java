package info.ieathealthy.mongoutils;

import com.mongodb.MongoClient;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoCodecRegistryFactory {
    private static CodecRegistry _instance = null;

    public static CodecRegistry getInstance() {
        if(_instance == null){
            //configure codecs for POJO classes
            CodecProvider modelCodecProvider = PojoCodecProvider.builder().register("info.ieathealthy.models").build();
            _instance = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(modelCodecProvider));

        }

        return _instance;
    }

    private MongoCodecRegistryFactory() {
    }
}
