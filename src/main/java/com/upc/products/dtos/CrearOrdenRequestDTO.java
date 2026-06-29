package com.upc.products.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrearOrdenRequestDTO {

    private Long userID;
    private Long proveedorID;
    private List<DetalleOrdenDTO> detalles = new ArrayList<>();
}