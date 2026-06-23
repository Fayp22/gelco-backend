package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.dto.ProductoResponse;
import com.gelco.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<?> getAllProductos() {
        try {
            List<ProductoResponse> productos = productoService.getAllProductos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener productos", e.getMessage()));
        }
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('CONSULTORA')")
    public ResponseEntity<?> getProductosActivos() {
        try {
            List<ProductoResponse> productos = productoService.getProductosActivos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener productos activos", e.getMessage()));
        }
    }

    @GetMapping("/stock-bajo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DESPACHO')")
    public ResponseEntity<?> getProductosConStockBajo() {
        try {
            List<ProductoResponse> productos = productoService.getProductosConStockBajo();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener productos con stock bajo", e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('CONSULTORA')")
    public ResponseEntity<?> buscarProductos(@RequestParam String nombre) {
        try {
            List<ProductoResponse> productos = productoService.buscarProductosActivosPorNombre(nombre);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al buscar productos", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoById(@PathVariable Long id) {
        try {
            ProductoResponse producto = productoService.getProductoById(id);
            return ResponseEntity.ok(producto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Producto no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener producto", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createProducto(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam(defaultValue = "0") Integer stock,
            @RequestParam(required = false) String imagenUrl) { // <--- AÑADIDO AQUÍ
        try {
            ProductoResponse producto = productoService.createProducto(nombre, descripcion, precio, stock, imagenUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al crear producto", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(
            @PathVariable Long id,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) BigDecimal precio,
            @RequestParam(required = false) Integer stock,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(required = false) String imagenUrl) { // <--- AÑADIDO AQUÍ
        try {
            ProductoResponse producto = productoService.updateProducto(id, nombre, descripcion, precio, stock, activo, imagenUrl);
            return ResponseEntity.ok(producto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Producto no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al actualizar producto", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        try {
            productoService.deleteProducto(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Producto no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al eliminar producto", e.getMessage()));
        }
    }
}