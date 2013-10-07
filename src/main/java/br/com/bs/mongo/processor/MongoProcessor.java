/*
 *  Propriedade intelectual de blueshift.com.br
 */
package br.com.bs.mongo.processor;

import br.net.primetech.dcm4mongo.DcmReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import br.net.primetech.dcm4mongo.model.DcmObject;
import com.google.gson.Gson;


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
            , String password) {
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

        //recupera o nome do arquivo contido na msg
        String fileName = exchange.getIn().getHeader("CamelFileNameOnly", String.class);

        //recupera o arquivo que foi recebido na msg
        File dcmFile = exchange.getIn().getBody(File.class) ;

        //log.debug("O arquivo existe = {}", file.toString());

        //log.debug("Reader parse");
        // extraí os dados do arquivo DCM utilizando o DCM4MONGO
        DcmReader reader = new DcmReader();
        DcmObject dcmObject = reader.parse( dcmFile );

        File dcmJPEG = dcmObject.getFile();

        Map dcmMetaData  = dcmObject.getElements();

        //insere o jpeg nos metadados
        //dcmMetaData.put("jpeg", IOUtils.toByteArray(   new FileInputStream(dcmJPEG) ) );
        //ERRO: DBObject of size 39334641 is over Max BSON size 16777216


        DBObject objMetadata = new BasicDBObject();
        objMetadata.putAll(dcmMetaData);

        //log.debug("Saída dcm4mongo = {}", f.toString());

        InputStream is = new FileInputStream(dcmFile);

       // log.debug("O InputStream = {}", is);

        /***
         GRAVA DADOS EXTRAIDOS DO DICOM NO MONGODB
          ***/

        //conecta no mongoDB
        DB db = mongoClient.getDB("dicom_files");

        // usa autenticacao caso informada
        if (!this.login.isEmpty() & !this.password.isEmpty()) {
            db.authenticate(this.login, this.password.toCharArray());
        }

        //Instancia o GridFs e dá nome ao bucket onde as imagens serão armazenadas
        GridFS gridFS = new GridFS(db, "dicom_files_bucket");

        //cria um novo arquivo no GridFS
        GridFSInputFile gridFSInputFile = gridFS.createFile( is, fileName );

        //seta os metadados na collections files.metadata
        gridFSInputFile.setMetaData(objMetadata);

        //salva no arquivo no GRIDFS
        gridFSInputFile.save();

        log.debug("Gravou no GridFS arquivo - {}", fileName);

    }
    
}

/*  Erro ao tentar fazer o parse dos arquivos DCM do OSIRIX
*
* java.lang.RuntimeException: No Image Reader for format: jpeg2000 registered
	at org.dcm4che.imageio.codec.ImageReaderFactory.getImageReader(ImageReaderFactory.java:184)
	at org.dcm4che.imageio.plugins.dcm.DicomImageReader.readMetadata(DicomImageReader.java:480)
	at org.dcm4che.imageio.plugins.dcm.DicomImageReader.read(DicomImageReader.java:273)
	at br.net.primetech.dcm4mongo.Dcm2jpeg.readImage(Dcm2jpeg.java:41)
	at br.net.primetech.dcm4mongo.Dcm2jpeg.convert(Dcm2jpeg.java:30)
	at br.net.primetech.dcm4mongo.DcmReader.parse(DcmReader.java:41)
	at br.com.bs.mongo.processor.MongoProcessor.process(MongoProcessor.java:69)
	at org.apache.camel.util.AsyncProcessorConverterHelper$ProcessorToAsyncProcessorBridge.process(AsyncProcessorConverterHelper.java:61)
	at org.apache.camel.util.AsyncProcessorHelper.process(AsyncProcessorHelper.java:73)
	at org.apache.camel.processor.DelegateAsyncProcessor.processNext(DelegateAsyncProcessor.java:99)
	at org.apache.camel.processor.DelegateAsyncProcessor.process(DelegateAsyncProcessor.java:90)
* */