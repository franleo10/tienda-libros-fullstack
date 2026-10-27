export interface ReseniaNueva {
  texto: string;
  calificacion: number;
}

export interface ReseniaDato {
  texto: string;
  calificacion: number;
  fecha: string;
  usuario: { nombre: string };
  libro: { titulo: string };
}