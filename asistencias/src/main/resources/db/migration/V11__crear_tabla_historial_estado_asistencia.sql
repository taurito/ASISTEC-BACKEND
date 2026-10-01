CREATE TABLE historial_estado_asistencia (
id BIGINT NOT NULL AUTO_INCREMENT,

asistencia_id BIGINT NOT NULL,
estado_asistencia_id BIGINT NOT NULL,

fecha_cambio DATETIME NOT NULL,
comentario VARCHAR(255),

created_at DATETIME NOT NULL,

CONSTRAINT pk_historial_estado_asistencia PRIMARY KEY (id),

CONSTRAINT fk_historial_estado_asistencia_asistencia
    FOREIGN KEY (asistencia_id)
        REFERENCES asistencia(id),

CONSTRAINT fk_historial_estado_asistencia_estado
    FOREIGN KEY (estado_asistencia_id)
        REFERENCES estado_asistencia(id)
);