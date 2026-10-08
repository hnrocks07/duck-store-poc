package com.duckstore.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.duckstore.duck.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChecklistApiTest {
  @Autowired MockMvc mvc;
  @Autowired DuckRepository repository;
  @Autowired DuckService service;

  @BeforeEach
  void clean() {
    repository.deleteAll();
  }

  @Test
  void rejectsInvalidRequestsWithUsefulErrors() throws Exception {
    for (String body :
        new String[] {"{", "{\"color\":\"Blue\",\"size\":\"Large\",\"price\":1,\"quantity\":1}"}) {
      mvc.perform(post("/api/ducks").contentType("application/json").content(body))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error").isString());
    }
    for (String price : new String[] {"-1", "0", "1.123"}) {
      mvc.perform(
              post("/api/ducks")
                  .contentType("application/json")
                  .content(
                      "{\"color\":\"Red\",\"size\":\"Large\",\"price\":"
                          + price
                          + ",\"quantity\":-1}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.fields.price").exists())
          .andExpect(jsonPath("$.fields.quantity").exists());
    }
    mvc.perform(delete("/api/ducks/not-a-number"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists());
    mvc.perform(delete("/api/ducks/999999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").exists());
    mvc.perform(
            put("/api/ducks/999999")
                .contentType("application/json")
                .content("{\"price\":1,\"quantity\":0}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").exists());
    mvc.perform(
            post("/api/orders/price")
                .contentType("application/json")
                .content(
                    "{\"color\":\"Red\",\"size\":\"Large\",\"quantity\":1,\"destinationCountry\":\"USA\",\"shippingMode\":\"Space\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("shippingMode must be one of Land, Air, Sea"));
  }

  @Test
  void collisionsIncludeDeletedRowsAndZeroEditPreservesIdentity() throws Exception {
    var first = add("10.00");
    var second = add("20.00");
    String collision = "{\"price\":20,\"quantity\":1}";
    for (int i = 0; i < 2; i++) {
      mvc.perform(
              put("/api/ducks/" + first.id()).contentType("application/json").content(collision))
          .andExpect(status().isConflict())
          .andExpect(
              jsonPath("$.error").value("A duck with this color, size and price already exists."));
      if (i == 0) service.delete(second.id());
    }
    mvc.perform(
            put("/api/ducks/" + first.id())
                .contentType("application/json")
                .content("{\"price\":11,\"quantity\":0,\"color\":\"Black\",\"size\":\"Small\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantity").value(0))
        .andExpect(jsonPath("$.color").value("Red"))
        .andExpect(jsonPath("$.size").value("Large"));
  }

  @Test
  void cheapestPriceAndCompleteResponseWithoutStockReservation() throws Exception {
    add("20.00");
    var cheap = add("10.00");
    mvc.perform(
            post("/api/orders/price")
                .contentType("application/json")
                .content(
                    "{\"color\":\"Red\",\"size\":\"Large\",\"quantity\":1001,\"destinationCountry\":\"India\",\"shippingMode\":\"Air\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.packageType").value("Wood"))
        .andExpect(jsonPath("$.protections[0]").value("Polystyrene balls"))
        .andExpect(jsonPath("$.totalToPay").value(35935.90))
        .andExpect(jsonPath("$.breakdown.length()").value(6))
        .andExpect(jsonPath("$.breakdown[0].amount").value(10010))
        .andExpect(jsonPath("$.breakdown[4].label").value("Air shipping"))
        .andExpect(jsonPath("$.breakdown[4].amount").value(30030))
        .andExpect(jsonPath("$.breakdown[5].label").value("Air bulk discount (15%)"))
        .andExpect(jsonPath("$.breakdown[5].amount").value(-4504.50));
    Assertions.assertEquals(1, repository.findById(cheap.id()).orElseThrow().getQuantity());
  }

  private DuckResponse add(String price) {
    return service.add(new DuckRequest(Color.Red, DuckSize.Large, new BigDecimal(price), 1));
  }
}
