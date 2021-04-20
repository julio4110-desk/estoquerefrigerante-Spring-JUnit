package com.juliobalbino.estoquerefrigerante.service;

import com.juliobalbino.estoquerefrigerante.dto.SodaDTO;
import com.juliobalbino.estoquerefrigerante.entity.Soda;
import com.juliobalbino.estoquerefrigerante.exception.SodaAlreadyRegisteredException;
import com.juliobalbino.estoquerefrigerante.exception.SodaNotFoundException;
import com.juliobalbino.estoquerefrigerante.exception.SodaStockExceededException;
import com.juliobalbino.estoquerefrigerante.mapper.SodaMapper;
import com.juliobalbino.estoquerefrigerante.repository.SodaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class SodaService {

    private final SodaRepository sodaRepository;

    private final SodaMapper sodaMapper = SodaMapper.INSTANCE;

    public SodaDTO createSoda(SodaDTO sodaDTO) throws SodaAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(sodaDTO.getName());
        Soda soda = sodaMapper.toModel(sodaDTO);
        Soda savedSoda = sodaRepository.save(soda);
        return sodaMapper.toDTO(savedSoda);
    }

    public SodaDTO findByName(String name) throws SodaNotFoundException {
        Soda foundSoda = sodaRepository.findByName(name)
                .orElseThrow(()-> new SodaNotFoundException(name));
        return sodaMapper.toDTO(foundSoda);
    }

    public List<SodaDTO> listAll() {
        return sodaRepository.findAll()
                .stream()
                .map(sodaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws SodaNotFoundException {
        verifyIfIsExists(id);
        sodaRepository.deleteById(id);
    }


    private void verifyIfIsAlreadyRegistered(String name) throws SodaAlreadyRegisteredException {
        Optional<Soda> optSavedSoda = sodaRepository.findByName(name);
        if (optSavedSoda.isPresent()) {
            throw new SodaAlreadyRegisteredException(name);
        }
    }

    private Soda verifyIfIsExists(Long id) throws SodaNotFoundException {
        return sodaRepository.findById(id)
                .orElseThrow(()-> new SodaNotFoundException(id));
    }

    public SodaDTO increment(Long id, int quantityToIncrement) throws SodaNotFoundException, SodaStockExceededException {
        Soda sodaToIncrementStock = verifyIfIsExists(id);
        int quantityAfterIncrement = quantityToIncrement + sodaToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= sodaToIncrementStock.getMax()) {
            sodaToIncrementStock.setQuantity(sodaToIncrementStock.getQuantity() + quantityToIncrement);
            Soda incrementedSodaStock = sodaRepository.save(sodaToIncrementStock);
            return sodaMapper.toDTO(incrementedSodaStock);
        }
        throw new SodaStockExceededException(id, quantityToIncrement);
    }
}
