package com.gelco.service;

import com.gelco.dto.ProductoResponse;
import com.gelco.model.Producto;
import com.gelco.repository.DetallePedidoRepository;
import com.gelco.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

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

    public List<ProductoResponse> buscarProductosActivosPorNombre(String nombre) {
        try {
            return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                    .stream()
                    .map(ProductoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos por nombre: " + e.getMessage());
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

    // MODIFICADO: Ahora acepta imagenUrl
    public ProductoResponse createProducto(String nombre, String descripcion, BigDecimal precio, Integer stock, String imagenUrl) {
        try {
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setImagenUrl(imagenUrl); // <--- AÑADIDO AQUÍ
            producto.setActivo(true);

            Producto savedProducto = productoRepository.save(producto);
            return ProductoResponse.fromEntity(savedProducto);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear producto: " + e.getMessage());
        }
    }

    // MODIFICADO: Ahora acepta imagenUrl
    public ProductoResponse updateProducto(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock, boolean activo, String imagenUrl) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (nombre != null) producto.setNombre(nombre);
            if (descripcion != null) producto.setDescripcion(descripcion);
            if (precio != null) producto.setPrecio(precio);
            if (stock != null) producto.setStock(stock);
            if (imagenUrl != null) producto.setImagenUrl(imagenUrl); // <--- AÑADIDO AQUÍ
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

    public static final int STOCK_UMBRAL_ALERTA = 5;

    public InventarioResumen getInventarioResumen() {
        try {
            long totalProductos = productoRepository.count();
            long productosActivos = productoRepository.countByActivoTrue();
            long productosAgotados = productoRepository.countByStockEquals(0);
            long productosStockBajo = productoRepository.countByStockLessThanEqual(STOCK_UMBRAL_ALERTA) - productosAgotados;

            return new InventarioResumen(
                    totalProductos,
                    productosActivos,
                    productosAgotados,
                    Math.max(0, productosStockBajo)
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener resumen de inventario: " + e.getMessage());
        }
    }

    public List<ProductoResponse> getProductosStockBajo() {
        try {
            return productoRepository.findByStockLessThanEqual(STOCK_UMBRAL_ALERTA)
                    .stream()
                    .map(ProductoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos con stock bajo: " + e.getMessage());
        }
    }

    public List<ProductoMasVendido> getProductosMasVendidos(Integer limit) {
        try {
            List<Object[]> resultados = detallePedidoRepository.findVentasAgrupadasPorProductoTodos();
            List<ProductoMasVendido> masVendidos = new ArrayList<>();

            for (Object[] row : resultados) {
                Long productoId = (Long) row[0];
                BigDecimal totalCantidad = (BigDecimal) row[1];
                productoRepository.findById(productoId).ifPresent(producto -> {
                    masVendidos.add(new ProductoMasVendido(
                            producto.getId(),
                            producto.getNombre(),
                            producto.getStock(),
                            totalCantidad.intValue(),
                            producto.getPrecio()
                    ));
                });
            }

            return masVendidos.size() > limit ? masVendidos.subList(0, limit) : masVendidos;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos mas vendidos: " + e.getMessage());
        }
    }

    public List<SugerenciaReposicion> getSugerenciasReposicion() {
        try {
            List<Object[]> resultados = detallePedidoRepository.findVentasAgrupadasPorProductoTodos();
            List<SugerenciaReposicion> sugerencias = new ArrayList<>();
            int stockMinimoReposicion = 20;

            for (Object[] row : resultados) {
                Long productoId = (Long) row[0];
                BigDecimal totalCantidad = (BigDecimal) row[1];
                int cantidadVendida = totalCantidad.intValue();

                productoRepository.findById(productoId).ifPresent(producto -> {
                    int stockActual = producto.getStock();
                    if (stockActual <= STOCK_UMBRAL_ALERTA) {
                        int cantidadSugerida = Math.max(stockMinimoReposicion - stockActual, cantidadVendida * 2);
                        sugerencias.add(new SugerenciaReposicion(
                                producto.getId(),
                                producto.getNombre(),
                                producto.getStock(),
                                cantidadVendida,
                                cantidadSugerida,
                                producto.getPrecio()
                        ));
                    }
                });
            }

            return sugerencias;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener sugerencias de reposicion: " + e.getMessage());
        }
    }

    public List<SugerenciaReposicion> getSugerenciasReposicionTodos() {
        try {
            List<Producto> productosStockBajo = productoRepository.findByStockLessThanEqual(STOCK_UMBRAL_ALERTA);
            List<Object[]> resultadosVentas = detallePedidoRepository.findVentasAgrupadasPorProductoTodos();

            java.util.Map<Long, Integer> ventasMap = new java.util.HashMap<>();
            for (Object[] row : resultadosVentas) {
                Long productoId = (Long) row[0];
                BigDecimal totalCantidad = (BigDecimal) row[1];
                ventasMap.put(productoId, totalCantidad.intValue());
            }

            List<SugerenciaReposicion> sugerencias = new ArrayList<>();
            int stockMinimoReposicion = 20;

            for (Producto producto : productosStockBajo) {
                int stockActual = producto.getStock();
                int cantidadVendida = ventasMap.getOrDefault(producto.getId(), 0);
                int cantidadSugerida = Math.max(stockMinimoReposicion - stockActual, cantidadVendida * 2);
                sugerencias.add(new SugerenciaReposicion(
                        producto.getId(),
                        producto.getNombre(),
                        stockActual,
                        cantidadVendida,
                        cantidadSugerida,
                        producto.getPrecio()
                ));
            }

            return sugerencias;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener sugerencias de reposicion: " + e.getMessage());
        }
    }

    public record InventarioResumen(
            long totalProductos,
            long productosActivos,
            long productosAgotados,
            long productosStockBajo
    ) {}

    public record ProductoMasVendido(
            Long productoId,
            String nombre,
            Integer stockActual,
            Integer cantidadVendida,
            BigDecimal precio
    ) {}

    public record SugerenciaReposicion(
            Long productoId,
            String nombre,
            Integer stockActual,
            Integer ventasTotales,
            Integer cantidadSugerida,
            BigDecimal precio
    ) {}
}