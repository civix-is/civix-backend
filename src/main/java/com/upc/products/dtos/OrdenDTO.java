package com.upc.products.dtos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.upc.products.entities.Cliente;
import com.upc.products.entities.DetalleOrden;
import com.upc.products.entities.Proveedor;
import com.upc.products.security.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDTO {
    private Long ordenId;
    private LocalDate fecha;
    private User user;
    private Proveedor proveedor;
    private List<DetalleOrden> detalles = new ArrayList<>();
}