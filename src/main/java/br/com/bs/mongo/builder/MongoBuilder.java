/*
 *  Propriedade intelectual de blueshift.com.br
 */
package br.com.bs.mongo.builder;

import java.util.Properties;

import br.com.bs.mongo.processor.MongoProcessor;
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
    }
    
}
