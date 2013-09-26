/*
 *  Propriedade intelectual de blueshift.com.br
 */
package br.com.bs.mongo.processor;


import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.io.InputStream;
import java.net.UnknownHostException;
import com.mongodb.gridfs.GridFS;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alancamillo
 */
public class MongoProcessor implements Processor{
    //LOG
    private static final org.slf4j.Logger log =
            LoggerFactory.getLogger(MongoProcessor.class);
    
    private MongoClient mongoClient;
    private final String login;
    private final String password;

    public MongoProcessor(
                            String mongoServer
                            , int mongoServerPort
                            , String login
                            , String password)
    {
        this.login = login;
        this.password = password;

        try {
            mongoClient = new MongoClient( mongoServer, mongoServerPort );
        } catch (UnknownHostException ex) {
            log.debug("ex = {}", ex);
        }

    }
    

    @Override
    public void process(Exchange exchange) throws Exception {

        // seta o nome do arquivo
        String fileName = 
                exchange.getIn().getHeader("CamelFileNameOnly", String.class);
        
        log.debug("fileName = {}", fileName);

        // connecta no mongo
        DB db = mongoClient.getDB("dicom_files");

        // usa autenticacao caso informada
        if (!this.login.isEmpty() & !this.password.isEmpty()) {
            db.authenticate(this.login, this.password.toCharArray());
        }

        GridFS dcmFile = new GridFS(db, "dicom_files_bucket");

        InputStream f = exchange.getIn().getBody(InputStream.class) ;

        dcmFile.createFile( f, fileName ).save();

    }
    
}
