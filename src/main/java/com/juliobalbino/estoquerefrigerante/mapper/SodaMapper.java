package com.juliobalbino.estoquerefrigerante.mapper;

import com.juliobalbino.estoquerefrigerante.dto.SodaDTO;
import com.juliobalbino.estoquerefrigerante.entity.Soda;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SodaMapper {

    SodaMapper INSTANCE = Mappers.getMapper(SodaMapper.class);

    Soda toModel(SodaDTO SodaDTO);

    SodaDTO toDTO(Soda Soda);


}


