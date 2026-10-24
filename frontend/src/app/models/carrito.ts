export interface LibroEnCarrito {
  idLibro: number;
  titulo: string;
  precio: number;
  fechaLanzamiento: string;
  urlPortada?: string | null;
}

export interface CarritoDato {
  idCarrito: number;
  precio: number;
  libros: LibroEnCarrito[];
}
