-- Tabla para preguntas de evaluación de capacitaciones
CREATE TABLE capacitacion_pregunta (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    capacitacion_id BIGINT NOT NULL,
    pregunta NVARCHAR(500) NOT NULL,
    orden INT NOT NULL,
    FOREIGN KEY (capacitacion_id) REFERENCES capacitaciones(id) ON DELETE CASCADE
);

CREATE INDEX idx_capacitacion_pregunta_capacitacion ON capacitacion_pregunta(capacitacion_id);
