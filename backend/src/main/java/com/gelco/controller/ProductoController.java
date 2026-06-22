package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.dto.ProductoResponse;
import com.gelco.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gelco.model.InventarioMovimiento;
import com.gelco.repository.InventarioMovimientoRepository;
import java.util.Map;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;
    private final InventarioMovimientoRepository inventarioMovimientoRepository;

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
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String imagenUrl) {
        try {
            System.out.println("[DEBUG] POST /productos - nombre=" + nombre + ", precio=" + precio + ", stock=" + stock + ", categoriaId=" + categoriaId);
            ProductoResponse producto = productoService.createProducto(nombre, descripcion, precio, stock, categoriaId, imagenUrl);
            System.out.println("[DEBUG] Producto creado: " + producto.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(producto);
        } catch (Exception e) {
            System.err.println("[ERROR] createProducto: " + e.getMessage());
            e.printStackTrace();
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
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String imagenUrl) {
        try {
            System.out.println("[DEBUG] PUT /productos/" + id + " - stock=" + stock + ", activo=" + activo + ", categoriaId=" + categoriaId);
            ProductoResponse producto = productoService.updateProducto(id, nombre, descripcion, precio, stock, activo, categoriaId, imagenUrl);
            System.out.println("[DEBUG] Producto actualizado: stock=" + producto.getStock());
            return ResponseEntity.ok(producto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Producto no encontrado", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
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

    @GetMapping("/inventario/resumen")
    public ResponseEntity<?> getInventarioResumen() {
        try {
            ProductoService.InventarioResumen resumen = productoService.getInventarioResumen();
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener resumen de inventario", e.getMessage()));
        }
    }

    @GetMapping("/inventario/alertas")
    public ResponseEntity<?> getProductosStockBajo() {
        try {
            List<ProductoResponse> productos = productoService.getProductosStockBajo();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener alertas de inventario", e.getMessage()));
        }
    }

    @GetMapping("/renovacion/mas-vendidos")
    public ResponseEntity<?> getProductosMasVendidos(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            var productos = productoService.getProductosMasVendidos(limit);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener productos mas vendidos", e.getMessage()));
        }
    }

    @GetMapping("/renovacion/sugerencias")
    public ResponseEntity<?> getSugerenciasReposicion() {
        try {
            var sugerencias = productoService.getSugerenciasReposicionTodos();
            return ResponseEntity.ok(sugerencias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener sugerencias de reposicion", e.getMessage()));
        }
    }

    @GetMapping("/inventario/movimientos")
    public ResponseEntity<?> getMovimientos() {
        try {
            List<Map<String, Object>> result = inventarioMovimientoRepository.findAllWithProducto()
                    .stream()
                    .map(m -> {
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("id",             m.getId());
                        map.put("productoNombre", m.getProducto().getNombre());
                        map.put("productoId",     m.getProducto().getId());
                        map.put("tipo",           m.getTipo());
                        map.put("cantidad",       m.getCantidad());
                        map.put("fecha",          m.getFecha());
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener movimientos", e.getMessage()));
        }
    }
}