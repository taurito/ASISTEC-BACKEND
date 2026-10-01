CREATE TABLE empleado (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          codigo_empleado VARCHAR(30) NOT NULL,
                          nombres VARCHAR(100) NOT NULL,
                          apellidos VARCHAR(100) NOT NULL,
                          documento VARCHAR(30) NOT NULL,
                          cargo VARCHAR(100),
                          unidad_id BIGINT NOT NULL,
                          telefono VARCHAR(30),
                          correo VARCHAR(150),
                          activo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME NOT NULL,

                          CONSTRAINT pk_empleado PRIMARY KEY (id),

                          CONSTRAINT uk_empleado_codigo UNIQUE (codigo_empleado),

                          CONSTRAINT uk_empleado_documento UNIQUE (documento),

                          CONSTRAINT fk_empleado_unidad
                              FOREIGN KEY (unidad_id)
                                  REFERENCES unidad(id)
);