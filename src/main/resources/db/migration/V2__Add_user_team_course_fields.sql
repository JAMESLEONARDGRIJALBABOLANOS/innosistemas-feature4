-- Migración V2: Agregar campos team_id, course_id, first_name, last_name a tabla users
-- Autor: Fábrica-Escuela de Software UdeA
-- Fecha: 2025-10-21
-- Descripción: Agrega soporte para equipos, cursos y nombres de usuario según tasking

-- Agregar columna team_id (nullable porque no todos los usuarios tienen equipo)
ALTER TABLE users ADD COLUMN IF NOT EXISTS team_id BIGINT;

-- Agregar columna course_id (nullable porque no todos los usuarios tienen curso)
ALTER TABLE users ADD COLUMN IF NOT EXISTS course_id BIGINT;

-- Agregar columna first_name
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);

-- Agregar columna last_name
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);

-- Crear índice en team_id para búsquedas rápidas por equipo
CREATE INDEX IF NOT EXISTS idx_users_team_id ON users(team_id);

-- Crear índice en course_id para búsquedas rápidas por curso
CREATE INDEX IF NOT EXISTS idx_users_course_id ON users(course_id);

-- Comentarios en las columnas
COMMENT ON COLUMN users.team_id IS 'ID del equipo al que pertenece el usuario';
COMMENT ON COLUMN users.course_id IS 'ID del curso asociado al usuario (para profesores y estudiantes)';
COMMENT ON COLUMN users.first_name IS 'Primer nombre del usuario';
COMMENT ON COLUMN users.last_name IS 'Apellido del usuario';

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

