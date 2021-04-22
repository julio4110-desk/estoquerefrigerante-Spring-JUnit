package com.juliobalbino.estoquerefrigerante.controller;

import com.juliobalbino.estoquerefrigerante.builder.SodaDTOBuilder;
import com.juliobalbino.estoquerefrigerante.dto.QuantityDTO;
import com.juliobalbino.estoquerefrigerante.dto.SodaDTO;
import com.juliobalbino.estoquerefrigerante.exception.SodaNotFoundException;
import com.juliobalbino.estoquerefrigerante.service.SodaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.juliobalbino.estoquerefrigerante.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SodaControllerTest {

    private static final String SODA_API_URL_PATH = "/api/v1/sodas";
    private static final long VALID_SODA_ID = 1L;
    private static final long INVALID_SODA_ID = 2L;
    private static final String SODA_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String SODA_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private SodaService sodaService;

    @InjectMocks
    private SodaController sodaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sodaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();

    }

    @Test
    void whenPOSTIsCalledThenASodaIsCreated() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        // when
        when(sodaService.createSoda(sodaDTO)).thenReturn(sodaDTO);

        // then
        mockMvc.perform(post(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sodaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(sodaDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setBrand(null);

        // then
        mockMvc.perform(post(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sodaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.findByName(sodaDTO.getName())).thenReturn(sodaDTO);

        // then
        mockMvc.perform(get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(sodaDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.findByName(sodaDTO.getName())).thenThrow(SodaNotFoundException.class);

        // then
        mockMvc.perform(get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithSodasIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.listAll()).thenReturn(Collections.singletonList(sodaDTO));

        // then
        mockMvc.perform(get(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(sodaDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutSodasIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.listAll()).thenReturn(Collections.singletonList(sodaDTO));

        // then
        mockMvc.perform(get(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        doNothing().when(sodaService).deleteById(sodaDTO.getId());

        // then
        mockMvc.perform(delete(SODA_API_URL_PATH + "/" + sodaDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(SodaNotFoundException.class).when(sodaService).deleteById(INVALID_SODA_ID);

        // then
        mockMvc.perform(delete(SODA_API_URL_PATH + "/" + INVALID_SODA_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());

        when(sodaService.increment(VALID_SODA_ID, quantityDTO.getQuantity())).thenReturn(sodaDTO);

        mockMvc.perform(patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(sodaDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(sodaDTO.getQuantity())));

    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());

        when(sodaService.increment(VALID_SODA_ID, quantityDTO.getQuantity())).thenReturn(sodaDTO);

        mockMvc.perform(patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());

    }

    @Test
    void whenPATCHIsCalledWithInvalidSodaIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

                when(sodaService.increment(INVALID_SODA_ID, quantityDTO.getQuantity())).thenThrow(SodaNotFoundException.class);
                mockMvc.perform(patch(SODA_API_URL_PATH + "/" + INVALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}
