CREATE TABLE asistencia (
id BIGINT NOT NULL AUTO_INCREMENT,

numero_asistencia VARCHAR(30) NOT NULL,

activo_id BIGINT NOT NULL,
empleado_id BIGINT NOT NULL,
tecnico_id BIGINT NOT NULL,

tipo_asistencia_id BIGINT NOT NULL,
estado_asistencia_id BIGINT NOT NULL,

fecha_ingreso DATETIME NOT NULL,
fecha_cierre DATETIME,

problema_reportado TEXT,
diagnostico TEXT,
trabajo_realizado TEXT,
observaciones TEXT,

created_at DATETIME NOT NULL,
updated_at DATETIME NOT NULL,

CONSTRAINT pk_asistencia PRIMARY KEY (id),

CONSTRAINT uk_asistencia_numero
    UNIQUE (numero_asistencia),

CONSTRAINT fk_asistencia_activo
    FOREIGN KEY (activo_id)
        REFERENCES activo(id),

CONSTRAINT fk_asistencia_empleado
    FOREIGN KEY (empleado_id)
        REFERENCES empleado(id),

CONSTRAINT fk_asistencia_tecnico
    FOREIGN KEY (tecnico_id)
        REFERENCES empleado(id),

CONSTRAINT fk_asistencia_tipo
    FOREIGN KEY (tipo_asistencia_id)
        REFERENCES tipo_asistencia(id),

CONSTRAINT fk_asistencia_estado
    FOREIGN KEY (estado_asistencia_id)
        REFERENCES estado_asistencia(id)
);