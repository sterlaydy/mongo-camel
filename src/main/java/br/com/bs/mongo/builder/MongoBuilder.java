/*
 *  Propriedade intelectual de blueshift.com.br
 */
package br.com.bs.mongo.builder;

<<<<<<< HEAD
import java.util.Properties;

import br.com.bs.mongo.processor.MongoProcessor;
=======
import br.com.bs.mongo.processor.ExtractDicom;
import br.com.bs.mongo.processor.MongoProcessor;
import java.util.Properties;
>>>>>>> 12f289b640c74fd871f6db21c06d4aa9b19a1b2d
import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;

/**
 *
 * @author alancamillo
 */
public class MongoBuilder extends SpringRouteBuilder{

    private Properties properties;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    @Override
    public void configure() throws Exception {

        from("file:/Users/ster/Documents/estudos/tcc/camel/mongo/config/xxx-in")
<<<<<<< HEAD
                
                .onException(Exception.class)
                    .log("deu pau")
                    .log("e agora?")
                .end()

                //.convertBodyTo(String.class)
                //.to("log://br.com.bs.mongo?level=DEBUG")



                .to("seda:teste");
                
        
        
         from("seda:teste")
                 .log(LoggingLevel.DEBUG, "br.com.bs.mongo", "Testando ${file:onlyname}")
                 .process( new MongoProcessor(
                                properties.getProperty("mongoServer")
                                , Integer.parseInt(properties.getProperty("mongoServerPort"))
                                , properties.getProperty("mongoLogin")
                                , properties.getProperty("mongoPassword") ) )
                .to("file:/Users/ster/Documents/estudos/tcc/camel/mongo/config/xxx-out");
=======
            .onException(Exception.class)
                .log("deu pau")
            .end()

            .process( new ExtractDicom() )  ;


        /*    .to("seda:mongo-connect");

        from("seda:mongo-connect")
            .log(LoggingLevel.DEBUG, "br.com.bs.mongo", "Salva no mongo")

            .process( new MongoProcessor(
                        properties.getProperty("mongoServer")
                        , Integer.parseInt(properties.getProperty("mongoServerPort"))
                        , properties.getProperty("mongoLogin")
                        , properties.getProperty("mongoPassword") ) );
                                                                           */
>>>>>>> 12f289b640c74fd871f6db21c06d4aa9b19a1b2d
    }
    
}
