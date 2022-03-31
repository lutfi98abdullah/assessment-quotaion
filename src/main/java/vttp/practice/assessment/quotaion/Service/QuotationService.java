package vttp.practice.assessment.quotaion.Service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.practice.assessment.quotaion.model.Quotation;

@Service
public class QuotationService {
    public static Optional<Quotation> getQuotations(List<String> items){
        
        JsonArrayBuilder itemsListJson = Json.createArrayBuilder();
        for(String item : items) {
            itemsListJson.add(item);
        }
        



        RestTemplate template = new RestTemplate();
        
        String QSys = UriComponentsBuilder.fromUriString("https://quotation.chuklee.com/quotation").toUriString();

        RequestEntity<String> req = RequestEntity
                        .post(QSys)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemsListJson.build().toString());

        
        try {    
            ResponseEntity<String> response = template.exchange(req, String.class);
            Quotation quote = new Quotation();
            Map<String, Float> itemQuote = new HashMap<>();

            JsonReader jsonReader = Json.createReader(new StringReader(response.getBody()));
            JsonObject qsys = jsonReader.readObject();
            JsonArray priceList = qsys.getJsonArray("quotations");

            for(int i = 0; i < priceList.size(); i++){
                JsonObject fruitPrice = priceList.getJsonObject(i);
                itemQuote.put(fruitPrice.getString("item"), 
                fruitPrice.getJsonNumber("unitPrice").bigDecimalValue().floatValue());
            }
            
            quote.setQuoteId(qsys.getString("quoteId"));
            quote.setQuotations(itemQuote);
            return Optional.of(quote);
        
    } catch (Exception e){
        System.out.println(e.getMessage());
    }    
        
        return Optional.empty();
    }
}