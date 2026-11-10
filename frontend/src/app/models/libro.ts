export interface AutorDato {
  nombre: string;
  apellido: string;
}

export interface ReseniaDeLibro {
  id: number;
  texto: string;
  calificacion: number;
  nombreUsuario: string;
}

export interface LibroDato {
  id: number;
  titulo: string;
  sinopsis: string;
  precio: number;
  fecha_lanzamiento: string;
  autores: AutorDato[];
  urlPortada?: string | null;
  generos: { nombre: string }[];
  resenias: ReseniaDeLibro[];
}

export interface LibroGuardado {
  titulo: string;
  sinopsis: string;
  precio: number;
  fecha_lanzamiento: string;
}