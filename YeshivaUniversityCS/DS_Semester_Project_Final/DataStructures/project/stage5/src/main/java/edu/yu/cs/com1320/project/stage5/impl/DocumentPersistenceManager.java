package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import jakarta.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private File baseDir;
    private Gson gson = new Gson();

    public DocumentPersistenceManager(File baseDir){
        if( baseDir == null ){
            this.baseDir = new File(System.getProperty("user.dir"));
        }else{
            this.baseDir = baseDir;
        }
       
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        if( uri == null || val == null ){
            throw new IllegalArgumentException("URI or Document val is null");
        }
        String newFilePathJson = newPath(uri);
        createDirectories(removeFileName(newFilePathJson));
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonSerializer<Document> serializer = jsonSerializer();
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, serializer);
        Gson customGson = gsonBuilder.create();  
        String customJSON = customGson.toJson(val);
        try(FileWriter newJsonFile = new FileWriter(newFilePathJson)){
            newJsonFile.write(customJSON);
            newJsonFile.flush();
        }catch(IOException e){
            e.printStackTrace();
        } 
    }

    


    @Override
    public Document deserialize(URI uri) throws IOException {
        File file = new File(newPath(uri));
        if( !file.exists() ){
            return null;
        }
        FileReader readJSON = new FileReader(newPath(uri));
        GsonBuilder gsonBuilder2 = new GsonBuilder();
        JsonDeserializer<Document> deserializer = jsonDeserializer();
        JsonElement parsedElement = JsonParser.parseReader(readJSON);
        gsonBuilder2.registerTypeAdapter(DocumentImpl.class, deserializer);
        Gson customGson2 = gsonBuilder2.create();
        Document returnedDoc = customGson2.fromJson(parsedElement, DocumentImpl.class);
        readJSON.close();
        this.delete(uri);
        return returnedDoc;
    }

    

    @Override
    public boolean delete(URI uri) throws IOException {
        File file = new File(newPath(uri));
        return file.delete();
    }

    /**
     * Utility methods
     */
    /**
     * combining the baseDir with the modified uri and the .json to create the full file path structure
     * @param uri
     * @return
     */
    private String newPath(URI uri){
        String shavedURI = uri.getRawSchemeSpecificPart().substring(2);
        //The combined filepath of our json using basedir and the uri
        return baseDir + File.separator + shavedURI + ".json";
    }

    /**
     * removing the filename.json from the path structure so that we can create the neccessary directories
     * @param newFilePathJson
     * @return
     */
    private String removeFileName(String newFilePathJson){
        File file = new File(newFilePathJson);
        String nameFile = File.separator + file.getName();
        return file.getPath().replace(nameFile, "");
    }

    /**
     * creating the neccessary directories here
     * @param directoryPath
     * @return
     */
    private boolean createDirectories(String directoryPath){
        File f = new File(directoryPath);
        if( !f.exists() ){
            return f.mkdirs();
        }
        return false;
    }
    
    /**
     * needed it here because of 30 line limit
     */
    private JsonSerializer<Document> jsonSerializer(){
        return new JsonSerializer<Document>() {  
            @Override
            public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonDocument = new JsonObject();
                jsonDocument.addProperty("uri", src.getKey().toString());
                if(src.getDocumentBinaryData() != null){
                    String b64Encoded = DatatypeConverter.printBase64Binary(src.getDocumentBinaryData());
                    jsonDocument.addProperty("binaryData", b64Encoded);
                }else{
                    jsonDocument.addProperty("txt", src.getDocumentTxt());
                }
                JsonElement jsonElement = gson.toJsonTree(src.getWordMap());
                jsonDocument.add("wordCounts", jsonElement);
                return jsonDocument;
            } 
        };
    }

    /**
     * needed it here because of 30 line limit
     */
    private JsonDeserializer<Document> jsonDeserializer(){
        return new JsonDeserializer<Document>() {  
            @Override
            public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                URI newUri = null;
                try {
                    newUri = new URI(jsonObject.get("uri").getAsString());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Document getDoc = null;
                if(jsonObject.has("txt")){
                    String txt = jsonObject.get("txt").getAsString();
                    getDoc = new DocumentImpl(newUri, txt, true);
                }else if(jsonObject.has("binaryData")){
                    String encoded = jsonObject.get("binaryData").getAsString();
                    byte[] b64Decoded = DatatypeConverter.parseBase64Binary(encoded); 
                    getDoc = new DocumentImpl(newUri, b64Decoded);
                }else{
                    throw new IllegalStateException("For some reason the document doesnt have binary or txt data for translation or the variable name isnt corect");
                } 
                JsonElement jsonElement = ((JsonObject) json).get("wordCounts");
                Type mapType = new TypeToken<HashMap<String, Integer>>() {}.getType();
                HashMap<String, Integer> data = gson.fromJson(jsonElement, mapType);
                getDoc.setWordMap(data);
                return getDoc;
            }
        };
    } 
}
