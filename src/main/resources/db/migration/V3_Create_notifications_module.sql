-- V3__create_notifications_module.sql

CREATE TABLE IF NOT EXISTS configuracion_notificacion (
  id_configuracion BIGINT AUTO_INCREMENT PRIMARY KEY,
  idUsuario BIGINT NOT NULL,
  recibir_correos BOOLEAN DEFAULT TRUE,
  notificaciones_tareas BOOLEAN DEFAULT TRUE,
  notificaciones_equipos BOOLEAN DEFAULT TRUE,
  notificaciones_sistema BOOLEAN DEFAULT TRUE,
  alertas_vencimiento BOOLEAN DEFAULT TRUE,
  frecuencia_correos VARCHAR(20) DEFAULT 'inmediato',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_conf_usuario FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notificacion (
  idNotificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
  tipo VARCHAR(50) NOT NULL,
  mensaje TEXT NOT NULL,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(30) DEFAULT 'no_leida',
  idUsuario BIGINT NULL,
  idReferencia BIGINT NULL,
  canal VARCHAR(20) DEFAULT 'plataforma',
  url_destino VARCHAR(255) NULL,
  expiracion DATETIME NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notif_usuario FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alerta (
  idAlerta BIGINT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(255) NOT NULL,
  tipo VARCHAR(50) NOT NULL,
  fecha_generacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_vencimiento DATETIME NULL,
  estado VARCHAR(30) DEFAULT 'activa',
  idTarea BIGINT NULL,
  idUsuario BIGINT NULL,
  idEquipo BIGINT NULL,
  leida BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS correos_masivos (
  idCorreo BIGINT AUTO_INCREMENT PRIMARY KEY,
  asunto VARCHAR(255) NOT NULL,
  mensaje TEXT NOT NULL,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(30) DEFAULT 'programado',
  idUsuario BIGINT NULL,
  tipo_destinatario VARCHAR(50) DEFAULT 'todos',
  idEquipo BIGINT NULL,
  programar_envio DATETIME NULL,
  total_destinatarios INT DEFAULT 0,
  exitosos INT DEFAULT 0,
  fallidos INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS destinatario_correo (
  idDestinatario BIGINT AUTO_INCREMENT PRIMARY KEY,
  idCorreo BIGINT NOT NULL,
  idUsuario BIGINT NULL,
  estado VARCHAR(30) DEFAULT 'pendiente',
  error_envio TEXT NULL,
  fecha_envio DATETIME NULL,
  fecha_lectura DATETIME NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (idCorreo) REFERENCES correos_masivos(idCorreo) ON DELETE CASCADE,
  FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS historial_comunicaciones (
  idHistorial BIGINT AUTO_INCREMENT PRIMARY KEY,
  tipo VARCHAR(50) NOT NULL,
  idReferencia BIGINT,
  titulo VARCHAR(255),
  descripcion TEXT,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  idUsuario_remitente BIGINT,
  idUsuario_destinatario BIGINT,
  idEquipo_destinatario BIGINT,
  canal VARCHAR(20) NOT NULL,
  metadata JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
