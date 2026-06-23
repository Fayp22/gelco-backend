package com.gelco.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EfektividadCapacitacionResponse {
    private Long capacitacionId;
    private String capacitacionTitulo;
    private String capacitacionDescripcion;
    private LocalDateTime capacitacionFecha;
    private String capacitacionTipo;
    private Integer duracionMinutos;
    private Integer totalInscripciones;
    private Integer totalCompletadas;
    private BigDecimal tasaCompletacion;
    private BigDecimal puntajePromedio;
    private BigDecimal ventasPromedioAntes;
    private BigDecimal ventasPromedioDespues;
    private BigDecimal porcentajeMejoraPromedio;
    private List<EfektividadConsultoraItem> detalleConsultoras;

    @Getter
    @Setter
    public static class EfektividadConsultoraItem {
        private Long consultoraId;
        private String consultoraNombre;
        private String nivel;
        private Boolean completado;
        private BigDecimal puntaje;
        private BigDecimal ventasAntes;
        private BigDecimal ventasDespues;
        private BigDecimal porcentajeMejora;

        public EfektividadConsultoraItem() {}

        public EfektividadConsultoraItem(Long consultoraId, String consultoraNombre, String nivel,
                Boolean completado, BigDecimal puntaje, BigDecimal ventasAntes,
                BigDecimal ventasDespues, BigDecimal porcentajeMejora) {
            this.consultoraId = consultoraId;
            this.consultoraNombre = consultoraNombre;
            this.nivel = nivel;
            this.completado = completado;
            this.puntaje = puntaje;
            this.ventasAntes = ventasAntes;
            this.ventasDespues = ventasDespues;
            this.porcentajeMejora = porcentajeMejora;
        }
    }

    public EfektividadCapacitacionResponse() {}

    public EfektividadCapacitacionResponse(Long capacitacionId, String capacitacionTitulo,
            String capacitacionDescripcion, LocalDateTime capacitacionFecha, String capacitacionTipo,
            Integer duracionMinutos, Integer totalInscripciones, Integer totalCompletadas,
            BigDecimal tasaCompletacion, BigDecimal puntajePromedio, BigDecimal ventasPromedioAntes,
            BigDecimal ventasPromedioDespues, BigDecimal porcentajeMejoraPromedio,
            List<EfektividadConsultoraItem> detalleConsultoras) {
        this.capacitacionId = capacitacionId;
        this.capacitacionTitulo = capacitacionTitulo;
        this.capacitacionDescripcion = capacitacionDescripcion;
        this.capacitacionFecha = capacitacionFecha;
        this.capacitacionTipo = capacitacionTipo;
        this.duracionMinutos = duracionMinutos;
        this.totalInscripciones = totalInscripciones;
        this.totalCompletadas = totalCompletadas;
        this.tasaCompletacion = tasaCompletacion;
        this.puntajePromedio = puntajePromedio;
        this.ventasPromedioAntes = ventasPromedioAntes;
        this.ventasPromedioDespues = ventasPromedioDespues;
        this.porcentajeMejoraPromedio = porcentajeMejoraPromedio;
        this.detalleConsultoras = detalleConsultoras;
    }
}
