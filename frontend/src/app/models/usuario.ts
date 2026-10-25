export type RolUsuario = 'ADMINISTRADOR' | 'USUARIO';

// Fila de la tabla de usuarios del panel de admin (UsuarioAdminDTO del backend)
export interface UsuarioAdmin {
  id: number;
  nombre: string;
  email: string;
  rol: RolUsuario | null;
  activo: boolean;
}

export interface FiltrosUsuarios {
  busqueda: string;
  rol: RolUsuario | '';
  activo: 'true' | 'false' | '';
}
