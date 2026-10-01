CREATE TABLE asignacion_activo (
id BIGINT NOT NULL AUTO_INCREMENT,

activo_id BIGINT NOT NULL,
empleado_id BIGINT NOT NULL,

fecha_asignacion DATETIME NOT NULL,
fecha_devolucion DATETIME,

observacion VARCHAR(255),

vigente BOOLEAN NOT NULL DEFAULT TRUE,

created_at DATETIME NOT NULL,
updated_at DATETIME NOT NULL,

CONSTRAINT pk_asignacion_activo PRIMARY KEY (id),

CONSTRAINT fk_asignacion_activo_activo
    FOREIGN KEY (activo_id)
        REFERENCES activo(id),

CONSTRAINT fk_asignacion_activo_empleado
    FOREIGN KEY (empleado_id)
        REFERENCES empleado(id)
);