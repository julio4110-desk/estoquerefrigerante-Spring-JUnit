package com.juliobalbino.estoquerefrigerante.service;

import com.juliobalbino.estoquerefrigerante.builder.SodaDTOBuilder;
import com.juliobalbino.estoquerefrigerante.dto.SodaDTO;
import com.juliobalbino.estoquerefrigerante.entity.Soda;
import com.juliobalbino.estoquerefrigerante.exception.SodaAlreadyRegisteredException;
import com.juliobalbino.estoquerefrigerante.exception.SodaNotFoundException;
import com.juliobalbino.estoquerefrigerante.mapper.SodaMapper;
import com.juliobalbino.estoquerefrigerante.repository.SodaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SodaServiceTest {

    private static final long INVALID_SODA_ID = 1L;

    @Mock
    private SodaRepository sodaRepository;

    private SodaMapper sodaMapper = SodaMapper.INSTANCE;

    @InjectMocks
    private SodaService sodaService;

    @Test
    void whenSodaInformedThenItShouldBeCreated() throws SodaAlreadyRegisteredException {
        // given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSavedSoda = sodaMapper.toModel(expectedSodaDTO);

        // when
        when(sodaRepository.findByName(expectedSodaDTO.getName())).thenReturn(Optional.empty());
        when(sodaRepository.save(expectedSavedSoda)).thenReturn(expectedSavedSoda);

        // then
        SodaDTO createdSodaDTO = sodaService.createSoda(expectedSodaDTO);

        assertThat(createdSodaDTO.getId(), is(equalTo(expectedSodaDTO.getId())));
        assertThat(createdSodaDTO.getName(), is(equalTo(expectedSodaDTO.getName())));
        assertThat(createdSodaDTO.getQuantity(), is(equalTo(expectedSodaDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredSodaInformedThenAnExceptionShouldBeThrown() {
        // given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda duplicatedSoda = sodaMapper.toModel(expectedSodaDTO);

        // when
        when(sodaRepository.findByName(expectedSodaDTO.getName())).thenReturn(Optional.of(duplicatedSoda));

        // then
        assertThrows(SodaAlreadyRegisteredException.class, () -> sodaService.createSoda(expectedSodaDTO));
    }

    @Test
    void whenValidSodaNameIsGivenThenReturnASoda() throws SodaNotFoundException {
        // given
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedFoundSoda = sodaMapper.toModel(expectedFoundSodaDTO);

        // when
        when(sodaRepository.findByName(expectedFoundSoda.getName())).thenReturn(Optional.of(expectedFoundSoda));

        // then
        SodaDTO foundSodaDTO = sodaService.findByName(expectedFoundSodaDTO.getName());

        assertThat(foundSodaDTO, is(equalTo(expectedFoundSodaDTO)));
    }

    @Test
    void whenNoRegisteredSodaNameIsGivenThenThrowException() {
        // given
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();


        // when
        when(sodaRepository.findByName(expectedFoundSodaDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(SodaNotFoundException.class, ()->sodaService.findByName(expectedFoundSodaDTO.getName()));
    }
}
