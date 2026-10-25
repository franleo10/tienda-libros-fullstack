import { AutorDato } from './libro';

export interface LibroEnBiblioteca {
  idLibro: number;
  titulo: string;
  precio: number;
  urlPdf?: string | null;
  urlPortada?: string | null;
  autores: AutorDato[];
  esFavorito: boolean;
}

export interface BibliotecaDato {
  idUsuario: number;
  nombreUsuario: string;
  libros: LibroEnBiblioteca[];
}
