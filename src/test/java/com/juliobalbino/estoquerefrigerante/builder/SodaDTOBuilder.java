package com.juliobalbino.estoquerefrigerante.builder;

import com.juliobalbino.estoquerefrigerante.dto.SodaDTO;
import com.juliobalbino.estoquerefrigerante.enums.SodaType;
import lombok.Builder;

@Builder
public class SodaDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Pepsi";

    @Builder.Default
    private String brand = "Pepsico";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private SodaType type = SodaType.COCA;

    public SodaDTO toSodaDTO(){
        return new SodaDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
