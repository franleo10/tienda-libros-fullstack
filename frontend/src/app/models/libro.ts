export interface AutorDato {
  nombre: string;
  apellido: string;
}

export interface LibroDato {
  id: number;
  titulo: string;
  sinopsis: string;
  precio: number;
  autores: AutorDato[];
  urlPortada?: string | null;
  generos: { nombre: string }[];
}
