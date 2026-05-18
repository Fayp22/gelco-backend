package com.gelco.dto;

import com.gelco.model.Producto;
import com.gelco.model.Promocion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private boolean activo;
    private String imagenUrl;

    // Categoría
    private Long categoriaId;
    private String categoriaNombre;

    // Marca
    private Long marcaId;
    private String marcaNombre;

    // Línea
    private Long lineaId;
    private String lineaNombre;

    // Promoción
    private BigDecimal precioOferta;   // null si no tiene promoción activa
    private boolean enOferta;
    private boolean enCampania;

    private LocalDateTime updatedAt;

    public static ProductoResponse fromEntity(Producto p) {
        ProductoResponse r = new ProductoResponse();

        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setDescripcion(p.getDescripcion());
        r.setPrecio(p.getPrecio());
        r.setStock(p.getStock());
        r.setActivo(p.isActivo());
        r.setImagenUrl(p.getImagenUrl()); // <--- AÑADIDO AQUÍ
        r.setUpdatedAt(p.getUpdatedAt());

        if (p.getCategoria() != null) {
            r.setCategoriaId(p.getCategoria().getId());
            r.setCategoriaNombre(p.getCategoria().getNombre());
        }

        if (p.getMarca() != null) {
            r.setMarcaId(p.getMarca().getId());
            r.setMarcaNombre(p.getMarca().getNombre());
        }

        if (p.getLinea() != null) {
            r.setLineaId(p.getLinea().getId());
            r.setLineaNombre(p.getLinea().getNombre());
        }

        Promocion promo = p.getPromocion();
        if (promo != null && promo.isActivo()) {
            r.setPrecioOferta(promo.getPrecioOferta());
            r.setEnOferta(true);
            r.setEnCampania(promo.isEnCampania());
        } else {
            r.setEnOferta(false);
            r.setEnCampania(false);
        }

        return r;
    }
}