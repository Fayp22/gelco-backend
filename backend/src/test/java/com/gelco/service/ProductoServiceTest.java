package com.gelco.service;

import com.gelco.dto.ProductoResponse;
import com.gelco.model.Categoria;
import com.gelco.model.Producto;
import com.gelco.repository.CategoriaRepository;
import com.gelco.repository.DetallePedidoRepository;
import com.gelco.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService – Gestión de Inventario (HU13)")
class ProductoServiceTest {

    @Mock ProductoRepository productoRepository;
    @Mock CategoriaRepository categoriaRepository;
    @Mock DetallePedidoRepository detallePedidoRepository;

    @InjectMocks ProductoService service;

    // ── Helper: crea un Producto de prueba ───────────────────────────────────
    private Producto producto(Long id, String nombre, int stock, double precio) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setStock(stock);
        p.setPrecio(BigDecimal.valueOf(precio));
        p.setActivo(true);
        return p;
    }

    // ── Helper: crea List<Object[]> para mockear ventas ──────────────────────
    private List<Object[]> ventas(Object[]... filas) {
        List<Object[]> lista = new ArrayList<>();
        for (Object[] fila : filas) lista.add(fila);
        return lista;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. CREAR PRODUCTO
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("1. Crear Producto")
    class CrearProducto {

        @Test
        @DisplayName("CP-BE-CR-01 | Válido: todos los campos correctos")
        void crear_datosValidos_retornaResponse() {
            Producto saved = producto(1L, "Crema Hidratante", 10, 25.50);
            when(productoRepository.save(any())).thenReturn(saved);

            ProductoResponse result = service.createProducto(
                    "Crema Hidratante", "Desc", BigDecimal.valueOf(25.50), 10, null, null
            );

            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Crema Hidratante");
            assertThat(result.getStock()).isEqualTo(10);
            assertThat(result.getPrecio()).isEqualByComparingTo("25.50");
            assertThat(result.isActivo()).isTrue();
        }

        @Test
        @DisplayName("CP-BE-CR-02 | Válido: con categoría existente")
        void crear_conCategoriaExistente_asignaCategoria() {
            Categoria cat = new Categoria();
            cat.setId(5L);
            cat.setNombre("Skincare");

            Producto saved = producto(2L, "Sérum", 5, 45.00);
            saved.setCategoria(cat);

            when(categoriaRepository.findById(5L)).thenReturn(Optional.of(cat));
            when(productoRepository.save(any())).thenReturn(saved);

            ProductoResponse result = service.createProducto(
                    "Sérum", null, BigDecimal.valueOf(45.00), 5, 5L, null
            );

            assertThat(result.getCategoriaId()).isEqualTo(5L);
            assertThat(result.getCategoriaNombre()).isEqualTo("Skincare");
        }

        @Test
        @DisplayName("CP-BE-CR-03 | Límite: stock = 0 (agotado desde creación)")
        void crear_stockCero_productoAgotado() {
            Producto saved = producto(3L, "Perfume Agotado", 0, 99.00);
            when(productoRepository.save(any())).thenReturn(saved);

            ProductoResponse result = service.createProducto(
                    "Perfume Agotado", null, BigDecimal.valueOf(99.00), 0, null, null
            );

            assertThat(result.getStock()).isZero();
        }

        @Test
        @DisplayName("CP-BE-CR-04 | Límite: stock = 1 (valor mínimo no cero)")
        void crear_stockUno_valorLimiteMinimo() {
            Producto saved = producto(4L, "Labial", 1, 12.00);
            when(productoRepository.save(any())).thenReturn(saved);

            ProductoResponse result = service.createProducto(
                    "Labial", null, BigDecimal.valueOf(12.00), 1, null, null
            );

            assertThat(result.getStock()).isEqualTo(1);
        }

        @Test
        @DisplayName("CP-BE-CR-05 | Inválido: categoría inexistente lanza excepción")
        void crear_categoriaInexistente_lanzaExcepcion() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.createProducto("Producto", null, BigDecimal.ONE, 5, 99L, null)
            ).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Categoría no encontrada");
        }

        @Test
        @DisplayName("CP-BE-CR-06 | Inválido: fallo en repositorio lanza RuntimeException")
        void crear_falloRepositorio_lanzaRuntimeException() {
            when(productoRepository.save(any())).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() ->
                    service.createProducto("Producto", null, BigDecimal.ONE, 5, null, null)
            ).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error al crear producto");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. ACTUALIZAR STOCK (REPONER PRODUCTO)
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("2. Actualizar Stock – Reposición")
    class ActualizarStock {

        @Test
        @DisplayName("CP-BE-ST-01 | Válido: reponer stock normal")
        void actualizar_stockNuevo_guardaCorrectamente() {
            Producto existente = producto(1L, "Crema", 2, 25.00);
            Producto actualizado = producto(1L, "Crema", 20, 25.00);

            when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(productoRepository.save(any())).thenReturn(actualizado);

            ProductoResponse result = service.updateProducto(
                    1L, null, null, null, 20, true, null, null
            );

            assertThat(result.getStock()).isEqualTo(20);
        }

        @Test
        @DisplayName("CP-BE-ST-02 | Límite: actualizar stock a 0 (agotar producto)")
        void actualizar_stockACero_productoAgotado() {
            Producto existente = producto(1L, "Crema", 5, 25.00);
            Producto actualizado = producto(1L, "Crema", 0, 25.00);

            when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(productoRepository.save(any())).thenReturn(actualizado);

            ProductoResponse result = service.updateProducto(
                    1L, null, null, null, 0, true, null, null
            );

            assertThat(result.getStock()).isZero();
        }

        @Test
        @DisplayName("CP-BE-ST-03 | Límite: stock = 5 (umbral exacto)")
        void actualizar_stockIgualUmbral_stockBajo() {
            Producto existente = producto(1L, "Crema", 2, 25.00);
            Producto actualizado = producto(1L, "Crema", 5, 25.00);

            when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(productoRepository.save(any())).thenReturn(actualizado);

            ProductoResponse result = service.updateProducto(
                    1L, null, null, null, 5, true, null, null
            );

            assertThat(result.getStock()).isEqualTo(ProductoService.STOCK_UMBRAL_ALERTA);
        }

        @Test
        @DisplayName("CP-BE-ST-04 | Límite: stock = 6 (sobre el umbral)")
        void actualizar_stockSobreUmbral_normal() {
            Producto existente = producto(1L, "Crema", 2, 25.00);
            Producto actualizado = producto(1L, "Crema", 6, 25.00);

            when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(productoRepository.save(any())).thenReturn(actualizado);

            ProductoResponse result = service.updateProducto(
                    1L, null, null, null, 6, true, null, null
            );

            assertThat(result.getStock()).isGreaterThan(ProductoService.STOCK_UMBRAL_ALERTA);
        }

        @Test
        @DisplayName("CP-BE-ST-05 | Inválido: producto inexistente lanza excepción")
        void actualizar_productoInexistente_lanzaExcepcion() {
            when(productoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.updateProducto(999L, null, null, null, 10, true, null, null)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Producto no encontrado");
        }

        @Test
        @DisplayName("CP-BE-ST-06 | Válido: desactivar producto (activo = false)")
        void actualizar_desactivarProducto_activoFalso() {
            Producto existente = producto(1L, "Crema", 5, 25.00);
            Producto actualizado = producto(1L, "Crema", 5, 25.00);
            actualizado.setActivo(false);

            when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(productoRepository.save(any())).thenReturn(actualizado);

            ProductoResponse result = service.updateProducto(
                    1L, null, null, null, null, false, null, null
            );

            assertThat(result.isActivo()).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. SUGERENCIAS DE REPOSICIÓN
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("3. Sugerencias de Reposición")
    class SugerenciasReposicion {

        @Test
        @DisplayName("CP-BE-SG-01 | Válido: fórmula max(20-stock, ventas×2) – domina stock bajo")
        void sugerencias_stockMuyBajo_formulaDominaStockBajo() {
            // stock=2, ventas=6 → max(20-2, 6*2) = max(18,12) = 18
            Producto p = producto(1L, "Crema A", 2, 25.00);
            when(productoRepository.findByStockLessThanEqual(5)).thenReturn(List.of(p));
            when(detallePedidoRepository.findVentasAgrupadasPorProductoTodos())
                    .thenReturn(ventas(new Object[]{1L, 6L}));

            List<ProductoService.SugerenciaReposicion> result = service.getSugerenciasReposicionTodos();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).cantidadSugerida()).isEqualTo(18);
        }

        @Test
        @DisplayName("CP-BE-SG-02 | Válido: fórmula max(20-stock, ventas×2) – domina ventas altas")
        void sugerencias_ventasAltas_formulaDominaVentas() {
            // stock=4, ventas=15 → max(20-4, 15*2) = max(16,30) = 30
            Producto p = producto(2L, "Crema B", 4, 30.00);
            when(productoRepository.findByStockLessThanEqual(5)).thenReturn(List.of(p));
            when(detallePedidoRepository.findVentasAgrupadasPorProductoTodos())
                    .thenReturn(ventas(new Object[]{2L, 15L}));

            List<ProductoService.SugerenciaReposicion> result = service.getSugerenciasReposicionTodos();

            assertThat(result.get(0).cantidadSugerida()).isEqualTo(30);
        }

        @Test
        @DisplayName("CP-BE-SG-03 | Límite: stock = 5 (umbral exacto) aparece en sugerencias")
        void sugerencias_stockIgualUmbral_incluidoEnSugerencias() {
            // stock=5, ventas=3 → max(20-5, 3*2) = max(15,6) = 15
            Producto p = producto(3L, "Producto Umbral", 5, 20.00);
            when(productoRepository.findByStockLessThanEqual(5)).thenReturn(List.of(p));
            when(detallePedidoRepository.findVentasAgrupadasPorProductoTodos())
                    .thenReturn(ventas(new Object[]{3L, 3L}));

            List<ProductoService.SugerenciaReposicion> result = service.getSugerenciasReposicionTodos();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).cantidadSugerida()).isEqualTo(15);
        }

        @Test
        @DisplayName("CP-BE-SG-04 | Límite: stock = 0 (agotado) incluido con urgencia máxima")
        void sugerencias_stockCero_cantidadSugerida20() {
            // stock=0, ventas=2 → max(20-0, 2*2) = max(20,4) = 20
            Producto p = producto(4L, "Producto Agotado", 0, 15.00);
            when(productoRepository.findByStockLessThanEqual(5)).thenReturn(List.of(p));
            when(detallePedidoRepository.findVentasAgrupadasPorProductoTodos())
                    .thenReturn(ventas(new Object[]{4L, 2L}));

            List<ProductoService.SugerenciaReposicion> result = service.getSugerenciasReposicionTodos();

            assertThat(result.get(0).cantidadSugerida()).isEqualTo(20);
        }

        @Test
        @DisplayName("CP-BE-SG-05 | Especial: producto sin ventas recibe sugerencia base")
        void sugerencias_sinVentas_cantidadBasada20MinusStock() {
            // stock=3, ventas=0 → max(20-3, 0*2) = max(17,0) = 17
            Producto p = producto(5L, "Producto Sin Ventas", 3, 10.00);
            when(productoRepository.findByStockLessThanEqual(5)).thenReturn(List.of(p));
            when(detallePedidoRepository.findVentasAgrupadasPorProductoTodos())
                    .thenReturn(new ArrayList<>());

            List<ProductoService.SugerenciaReposicion> result = service.getSugerenciasReposicionTodos();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).ventasTotales()).isZero();
            assertThat(result.get(0).cantidadSugerida()).isEqualTo(17);
        }

        @Test
        @DisplayName("CP-BE-SG-06 | Válido: sin productos con stock bajo retorna lista vacía")
        void sugerencias_sinProductosCriticos_listaVacia() {
            when(productoRepository.findByStockLessThanEqual(5)).thenReturn(new ArrayList<>());
            when(detallePedidoRepository.findVentasAgrupadasPorProductoTodos()).thenReturn(new ArrayList<>());

            List<ProductoService.SugerenciaReposicion> result = service.getSugerenciasReposicionTodos();

            assertThat(result).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. RESUMEN KPIs DE INVENTARIO
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("4. Resumen KPIs del Inventario")
    class ResumenInventario {

        @Test
        @DisplayName("CP-BE-KPI-01 | Válido: resumen con todos los contadores correctos")
        void resumen_datosNormales_retornaKpisCorrectos() {
            when(productoRepository.count()).thenReturn(10L);
            when(productoRepository.countByActivoTrue()).thenReturn(8L);
            when(productoRepository.countByStockEquals(0)).thenReturn(1L);
            when(productoRepository.countByStockLessThanEqual(5)).thenReturn(4L);

            ProductoService.InventarioResumen resumen = service.getInventarioResumen();

            assertThat(resumen.totalProductos()).isEqualTo(10);
            assertThat(resumen.productosActivos()).isEqualTo(8);
            assertThat(resumen.productosAgotados()).isEqualTo(1);
            assertThat(resumen.productosStockBajo()).isEqualTo(3); // 4 - 1 agotado
        }

        @Test
        @DisplayName("CP-BE-KPI-02 | Límite: BD vacía retorna todos los contadores en 0")
        void resumen_bdVacia_todosContadoresCero() {
            when(productoRepository.count()).thenReturn(0L);
            when(productoRepository.countByActivoTrue()).thenReturn(0L);
            when(productoRepository.countByStockEquals(0)).thenReturn(0L);
            when(productoRepository.countByStockLessThanEqual(5)).thenReturn(0L);

            ProductoService.InventarioResumen resumen = service.getInventarioResumen();

            assertThat(resumen.totalProductos()).isZero();
            assertThat(resumen.productosActivos()).isZero();
            assertThat(resumen.productosAgotados()).isZero();
            assertThat(resumen.productosStockBajo()).isZero();
        }

        @Test
        @DisplayName("CP-BE-KPI-03 | Especial: stockBajo nunca es negativo (protección Math.max)")
        void resumen_agotadosMayorQueBajos_stockBajoNuncaNegativo() {
            when(productoRepository.count()).thenReturn(5L);
            when(productoRepository.countByActivoTrue()).thenReturn(5L);
            when(productoRepository.countByStockEquals(0)).thenReturn(3L);
            when(productoRepository.countByStockLessThanEqual(5)).thenReturn(2L);

            ProductoService.InventarioResumen resumen = service.getInventarioResumen();

            assertThat(resumen.productosStockBajo()).isGreaterThanOrEqualTo(0);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. ELIMINAR PRODUCTO
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("5. Eliminar Producto")
    class EliminarProducto {

        @Test
        @DisplayName("CP-BE-EL-01 | Válido: eliminar producto existente")
        void eliminar_productoExistente_ejecutaSinError() {
            Producto p = producto(1L, "Crema", 5, 20.00);
            when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

            assertThatCode(() -> service.deleteProducto(1L)).doesNotThrowAnyException();
            verify(productoRepository).delete(p);
        }

        @Test
        @DisplayName("CP-BE-EL-02 | Inválido: eliminar producto inexistente lanza excepción")
        void eliminar_productoInexistente_lanzaExcepcion() {
            when(productoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteProducto(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Producto no encontrado");
        }
    }
}