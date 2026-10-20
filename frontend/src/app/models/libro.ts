export interface AutorDato {
  nombre: string;
  apellido: string;
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
}

export interface LibroGuardado {
  titulo: string;
  sinopsis: string;
  precio: number;
  fecha_lanzamiento: string;
}