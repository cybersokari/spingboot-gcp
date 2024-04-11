//package co.gatedaccess.web.config;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.connection.TransportSettings;
//import io.netty.handler.ssl.SslContext;
//import io.netty.handler.ssl.SslContextBuilder;
//import io.netty.handler.ssl.SslProvider;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//import javax.annotation.Nonnull;
//import javax.net.ssl.SSLException;
//
//@Configuration
//@EnableMongoRepositories
//public class MongoDbConfig extends AbstractMongoClientConfiguration {
//
//    @Override
//    @Nonnull
//    public MongoClient mongoClient() {
//        SslContext sslContext = null;
//        try {
//            sslContext = SslContextBuilder.forClient()
//                    .sslProvider(SslProvider.OPENSSL)
//                    .build();
//            MongoClientSettings settings = MongoClientSettings.builder()
//                    .applyToSslSettings(builder -> builder.enabled(true))
//                    .transportSettings(TransportSettings.nettyBuilder()
//                            .sslContext(sslContext)
//                            .build())
//                    .build();
//            return MongoClients.create(settings);
//        } catch (SSLException e) {
//            throw new RuntimeException(e);
//        }
//
////        final ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/dev");
////        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
////                .applyConnectionString(connectionString)
////                .build();
////        return MongoClients.create(mongoClientSettings);
//    }
//
//    @Override
//    @Nonnull
//    protected String getDatabaseName() {
//        return "dev";
//    }
//}
