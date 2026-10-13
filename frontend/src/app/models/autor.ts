export interface AutorResumen {
  idAutor: number;
  nombre: string;
  apellido: string;
  cantidadLibros: number;
}

export interface AutorGuardado {
  nombre: string;
  apellido: string;
}

export interface Pagina<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
}

export interface AutorDetalle extends Omit<AutorResumen, 'cantidadLibros'> {
  libros: LibroDeAutor[];
}

export interface LibroDeAutor {
  id: number;
  titulo: string;
  sinopsis: string;
  precio: number;
  fecha_lanzamiento: string;
  generos: { nombre: string }[];
  resenias: ReseniaDeLibro[];
}

export interface ReseniaDeLibro {
  id: number;
  texto: string;
  calificacion: number;
  nombreUsuario: string;
}