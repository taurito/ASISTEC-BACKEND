CREATE TABLE actividad_asistencia (
id BIGINT NOT NULL AUTO_INCREMENT,

asistencia_id BIGINT NOT NULL,

descripcion VARCHAR(255) NOT NULL,
resultado VARCHAR(255),
fecha_actividad DATETIME NOT NULL,
observacion VARCHAR(255),

created_at DATETIME NOT NULL,

CONSTRAINT pk_actividad_asistencia PRIMARY KEY (id),

CONSTRAINT fk_actividad_asistencia_asistencia
    FOREIGN KEY (asistencia_id)
        REFERENCES asistencia(id)
);