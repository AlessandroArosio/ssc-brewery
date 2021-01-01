package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Test
    void deleteBeerBadCredentialsUrl() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/771dad59-ef58-49bc-83be-b7c2b8dbaf2e")
                        .header("apiKey", "spring")
                        .header("apiSecret", "invalid-pass"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerBadCredentials() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/771dad59-ef58-49bc-83be-b7c2b8dbaf2e")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "invalid-pass"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/771dad59-ef58-49bc-83be-b7c2b8dbaf2e")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/771dad59-ef58-49bc-83be-b7c2b8dbaf2e"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/771dad59-ef58-49bc-83be-b7c2b8dbaf2e"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0083783375213"))
                .andExpect(status().isOk());
    }
}
