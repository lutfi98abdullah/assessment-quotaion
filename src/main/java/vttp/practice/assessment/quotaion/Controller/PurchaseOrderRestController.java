package vttp.practice.assessment.quotaion.Controller;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp.practice.assessment.quotaion.Service.QuotationService;
import vttp.practice.assessment.quotaion.model.Quotation;

@RestController
public class PurchaseOrderRestController {
    
    @PostMapping(path="/api/po")
    public ResponseEntity<String> orderData (@RequestBody String formData){
        


        
        
        JsonReader jsonReader = Json.createReader(new StringReader(formData));
        JsonObject jsonForm = jsonReader.readObject();
        JsonArray lineItems = jsonForm.getJsonArray("lineItems");

        List<String> items = new ArrayList<String>();

        for(int i = 0; i < lineItems.size();i++){
            JsonObject item = lineItems.getJsonObject(i);
            items.add(item.getString("item"));
        }
        

        Optional<Quotation> optQuote = QuotationService.getQuotations(items);
        Quotation quote = optQuote.get();


    try {
        String name = jsonForm.getString("name");
        String invoiceId = quote.getQuoteId();
        double total = 0.0;


        for(int i = 0; i< lineItems.size(); i++){
            JsonObject item = lineItems.getJsonObject(i);
            int itemQuantity = item.getInt("quantity");
            String fruit = item.getString("item");
            total += itemQuantity * quote.getQuotation(fruit);
        }

        JsonObjectBuilder orderInfo = Json.createObjectBuilder();
        orderInfo.add("invoiceId", invoiceId).add("name", name).add("total", total);

                return ResponseEntity.ok().body(orderInfo.build().toString());

            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


                return ResponseEntity.status(400).body("{}");
    }


}