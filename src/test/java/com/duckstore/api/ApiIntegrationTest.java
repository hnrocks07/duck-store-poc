package com.duckstore.api;

import com.duckstore.duck.DuckRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ApiIntegrationTest {
 @Autowired MockMvc mvc; @Autowired DuckRepository ducks;
 @BeforeEach void clean(){ducks.deleteAll();}
 private static final String DUCK="{\"color\":\"Red\",\"size\":\"Large\",\"price\":10.00,\"quantity\":2}";
 @Test void adds_and_merges_through_http() throws Exception {mvc.perform(post("/api/ducks").contentType(MediaType.APPLICATION_JSON).content(DUCK)).andExpect(status().isCreated());mvc.perform(post("/api/ducks").contentType(MediaType.APPLICATION_JSON).content("{\"color\":\"Red\",\"size\":\"Large\",\"price\":10.00,\"quantity\":3}")).andExpect(status().isCreated());mvc.perform(get("/api/ducks")).andExpect(status().isOk()).andExpect(jsonPath("$[0].quantity").value(5)).andExpect(jsonPath("$.length()").value(1));}
 @Test void prices_existing_inventory() throws Exception {mvc.perform(post("/api/ducks").contentType(MediaType.APPLICATION_JSON).content(DUCK));mvc.perform(post("/api/orders/price").contentType(MediaType.APPLICATION_JSON).content("{\"color\":\"Red\",\"size\":\"Large\",\"quantity\":1,\"destinationCountry\":\"USA\",\"shippingMode\":\"Land\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.packageType").value("Wood")).andExpect(jsonPath("$.totalToPay").value(22.3));}
 @Test void returns_structured_validation_error() throws Exception {mvc.perform(post("/api/ducks").contentType(MediaType.APPLICATION_JSON).content("{\"color\":\"Red\",\"size\":\"Large\",\"price\":0,\"quantity\":0}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Validation failed")).andExpect(jsonPath("$.fields.price").exists()).andExpect(jsonPath("$.fields.quantity").exists());}
 @Test void rejects_invalid_enum_and_missing_field() throws Exception {mvc.perform(post("/api/ducks").contentType(MediaType.APPLICATION_JSON).content("{\"color\":\"Blue\",\"size\":\"Large\",\"price\":10,\"quantity\":1}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Validation failed"));mvc.perform(post("/api/ducks").contentType(MediaType.APPLICATION_JSON).content("{\"color\":\"Red\",\"size\":\"Large\",\"quantity\":1}")).andExpect(status().isBadRequest());}
 @Test void returns_404_for_missing_inventory() throws Exception {mvc.perform(post("/api/orders/price").contentType(MediaType.APPLICATION_JSON).content("{\"color\":\"Black\",\"size\":\"Small\",\"quantity\":1,\"destinationCountry\":\"USA\",\"shippingMode\":\"Air\"}")).andExpect(status().isNotFound());}
}
