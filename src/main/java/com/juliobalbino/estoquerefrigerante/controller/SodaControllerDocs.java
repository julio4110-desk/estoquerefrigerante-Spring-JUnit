package com.juliobalbino.estoquerefrigerante.controller;

import com.juliobalbino.estoquerefrigerante.dto.SodaDTO;
import com.juliobalbino.estoquerefrigerante.exception.SodaAlreadyRegisteredException;
import com.juliobalbino.estoquerefrigerante.exception.SodaNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages Soda stock")
public interface SodaControllerDocs {

    @ApiOperation(value = "Soda creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success Soda creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    SodaDTO createSoda(SodaDTO SodaDTO) throws SodaAlreadyRegisteredException;

    @ApiOperation(value = "Returns Soda found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success Soda found in the system"),
            @ApiResponse(code = 404, message = "Soda with given name not found.")
    })
    SodaDTO findByName(@PathVariable String name) throws SodaNotFoundException;

    @ApiOperation(value = "Returns a list of all Sodas registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all Sodas registered in the system"),
    })
    List<SodaDTO> listSodas();

    @ApiOperation(value = "Delete a Soda found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success Soda deleted in the system"),
            @ApiResponse(code = 404, message = "Soda with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws SodaNotFoundException;
}
