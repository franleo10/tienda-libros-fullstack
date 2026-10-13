export interface PerfilUsuario {
  id: number;
  nombre: string;
  email: string;
  edad: number;
}

export interface ActualizarPerfil {
  nombre: string;
  email: string;
  edad: number;
}

export interface CambiarPassword {
  passwordActual: string;
  passwordNueva: string;
}

export interface RespuestaToken {
  token: string;
}
