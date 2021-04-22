package com.juliobalbino.estoquerefrigerante.service;

import com.juliobalbino.estoquerefrigerante.builder.SodaDTOBuilder;
import com.juliobalbino.estoquerefrigerante.dto.SodaDTO;
import com.juliobalbino.estoquerefrigerante.entity.Soda;
import com.juliobalbino.estoquerefrigerante.exception.SodaAlreadyRegisteredException;
import com.juliobalbino.estoquerefrigerante.exception.SodaNotFoundException;
import com.juliobalbino.estoquerefrigerante.exception.SodaStockExceededException;
import com.juliobalbino.estoquerefrigerante.mapper.SodaMapper;
import com.juliobalbino.estoquerefrigerante.repository.SodaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @Test
    void whenListSodaIsCalledThenReturnAListOfSodas() {
        // given
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedFoundSoda = sodaMapper.toModel(expectedFoundSodaDTO);

        // when
        when(sodaRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundSoda));

        // then
        List<SodaDTO> foundListSodasDTO = sodaService.listAll();

        assertThat(foundListSodasDTO, is(not(empty())));
        assertThat(foundListSodasDTO.get(0), is(equalTo(expectedFoundSodaDTO)));
    }

    @Test
    void whenListSodaIsCalledThenReturnAnEmptyListOfSodas() {
        // when
        when(sodaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        // then
        List<SodaDTO> foundListSodasDTO = sodaService.listAll();

        assertThat(foundListSodasDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenASodaShouldBeDeleted() throws SodaNotFoundException {
        // given
        SodaDTO expectedDeletedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedDeletedSoda = sodaMapper.toModel(expectedDeletedSodaDTO);

        // when
        when(sodaRepository.findById(expectedDeletedSodaDTO.getId())).thenReturn(Optional.of(expectedDeletedSoda));
        doNothing().when(sodaRepository).deleteById(expectedDeletedSodaDTO.getId());

        // then
        sodaService.deleteById(expectedDeletedSodaDTO.getId());

        verify(sodaRepository, times(1)).findById(expectedDeletedSodaDTO.getId());
        verify(sodaRepository, times(1)).deleteById(expectedDeletedSodaDTO.getId());
    }

    @Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {
        when(sodaRepository.findById(INVALID_SODA_ID)).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> sodaService.deleteById(INVALID_SODA_ID));
    }

    @Test
    void whenIncrementIsCalledThenIncrementSodaStock() throws SodaNotFoundException, SodaStockExceededException {
        // given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        // when
        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));
        when(sodaRepository.save(expectedSoda)).thenReturn(expectedSoda);


        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedSodaDTO.getQuantity() + quantityToIncrement;

        // then
        SodaDTO incrementedSodaDTO = sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedSodaDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedSodaDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        // given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        // when
        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));


        int quantityToIncrement = 80;
        assertThrows(SodaStockExceededException.class, () -> sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        // given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        // when
        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));


        int quantityToIncrement = 45;
        assertThrows(SodaStockExceededException.class, () -> sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(sodaRepository.findById(INVALID_SODA_ID)).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> sodaService.increment(INVALID_SODA_ID, quantityToIncrement));
    }
}
