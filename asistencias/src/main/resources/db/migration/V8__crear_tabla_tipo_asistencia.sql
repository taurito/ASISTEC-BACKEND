CREATE TABLE tipo_asistencia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_tipo_asistencia PRIMARY KEY (id),
    CONSTRAINT uk_tipo_asistencia_nombre UNIQUE (nombre)
);