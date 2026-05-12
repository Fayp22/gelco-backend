package com.gelco.service;

import com.gelco.dto.ProductoResponse;
import com.gelco.model.Producto;
import com.gelco.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<ProductoResponse> getAllProductos() {
        try {
            return productoRepository.findAll()
                    .stream()
                    .map(ProductoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos: " + e.getMessage());
        }
    }

    public List<ProductoResponse> getProductosActivos() {
        try {
            return productoRepository.findByActivoTrue()
                    .stream()
                    .map(ProductoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos activos: " + e.getMessage());
        }
    }

    public ProductoResponse getProductoById(Long id) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            return ProductoResponse.fromEntity(producto);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener producto: " + e.getMessage());
        }
    }

    public ProductoResponse createProducto(String nombre, String descripcion, BigDecimal precio, Integer stock) {
        try {
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setActivo(true);

            Producto savedProducto = productoRepository.save(producto);
            return ProductoResponse.fromEntity(savedProducto);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear producto: " + e.getMessage());
        }
    }

    public ProductoResponse updateProducto(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock, boolean activo) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (nombre != null) producto.setNombre(nombre);
            if (descripcion != null) producto.setDescripcion(descripcion);
            if (precio != null) producto.setPrecio(precio);
            if (stock != null) producto.setStock(stock);
            producto.setActivo(activo);

            Producto updatedProducto = productoRepository.save(producto);
            return ProductoResponse.fromEntity(updatedProducto);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar producto: " + e.getMessage());
        }
    }

    public void deleteProducto(Long id) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            productoRepository.delete(producto);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage());
        }
    }
}
