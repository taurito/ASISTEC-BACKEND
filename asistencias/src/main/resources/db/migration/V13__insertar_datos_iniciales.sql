-- ============================================
-- TIPOS DE ACTIVO
-- ============================================

INSERT INTO tipo_activo (nombre, descripcion)
VALUES
    ('CPU', 'Computadora de escritorio'),
    ('Monitor', 'Monitor de computadora'),
    ('Impresora', 'Impresora'),
    ('Scanner', 'Escáner'),
    ('Telefono', 'Teléfono');


-- ============================================
-- ESTADOS DE ASISTENCIA
-- ============================================

INSERT INTO estado_asistencia (nombre, descripcion)
VALUES
    ('RECIBIDO', 'El equipo fue recibido por la unidad de informática'),
    ('EN DIAGNOSTICO', 'El equipo está siendo evaluado para determinar la causa del problema'),
    ('EN MANTENIMIENTO', 'El equipo se encuentra en proceso de mantenimiento o reparación'),
    ('ESPERANDO REPUESTO', 'El mantenimiento está detenido a la espera de un repuesto'),
    ('PARA ENTREGAR', 'El mantenimiento terminó y el equipo está listo para ser entregado'),
    ('ENTREGADO', 'El equipo fue entregado al empleado');


-- ============================================
-- ESTADOS DEL ACTIVO
-- ============================================

INSERT INTO estado_activo (nombre, descripcion)
VALUES
    ('ACTIVO', 'El equipo se encuentra en funcionamiento y en uso'),
    ('MANTENIMIENTO', 'El equipo se encuentra actualmente en mantenimiento'),
    ('SEGURO', 'El equipo está destinado a un proceso de seguro'),
    ('DESUSO', 'El equipo ya no se encuentra en uso'),
    ('BAJA', 'El equipo fue dado de baja');