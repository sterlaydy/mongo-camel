/*
 *  Propriedade intelectual de blueshift.com.br
 */
package br.com.bs.mongo.builder;

import br.com.bs.mongo.processor.ExtractDicom;
import br.com.bs.mongo.processor.MongoProcessor;
import java.util.Properties;
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
    }
    
}
