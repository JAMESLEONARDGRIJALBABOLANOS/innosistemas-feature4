-- Migración V4: Crear tablas teams y notifications
-- Autor: Fábrica-Escuela de Software UdeA
-- Fecha: 2025-11-03
-- Descripción: Crea las tablas para equipos y notificaciones

-- ============================================
-- Crear tabla teams
-- ============================================
CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_limite TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    course_id BIGINT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    max_miembros INTEGER,
    CONSTRAINT chk_max_miembros CHECK (max_miembros IS NULL OR max_miembros > 0)
);

CREATE INDEX IF NOT EXISTS idx_teams_course_id ON teams(course_id);
CREATE INDEX IF NOT EXISTS idx_teams_activo ON teams(activo);

-- Trigger para teams
CREATE TRIGGER update_teams_updated_at
    BEFORE UPDATE ON teams
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE teams IS 'Tabla de equipos de trabajo';
COMMENT ON COLUMN teams.nombre IS 'Nombre del equipo';
COMMENT ON COLUMN teams.activo IS 'Indica si el equipo está activo';

-- ============================================
-- Crear tabla notifications
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mensaje TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_lectura TIMESTAMP WITH TIME ZONE,
    team_id BIGINT,
    curso_id BIGINT,
    prioridad VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    enlace VARCHAR(500),
    metadata TEXT,
    expira_en TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
    CONSTRAINT chk_prioridad CHECK (prioridad IN ('BAJA', 'NORMAL', 'ALTA', 'URGENTE'))
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_leida ON notifications(leida);
CREATE INDEX IF NOT EXISTS idx_notifications_tipo ON notifications(tipo);
CREATE INDEX IF NOT EXISTS idx_notifications_team_id ON notifications(team_id);

-- Trigger para notifications
CREATE TRIGGER update_notifications_updated_at
    BEFORE UPDATE ON notifications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE notifications IS 'Tabla de notificaciones del sistema';
COMMENT ON COLUMN notifications.leida IS 'Indica si la notificación ha sido leída';

