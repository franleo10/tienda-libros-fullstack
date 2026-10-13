import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

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
}

export interface Pagina<T> {
  content: T[];
  totalPages: number;
  number: number;
}

@Injectable({
  providedIn: 'root',
})



export class Libro {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  obtenerLibros(pagina: number){
    return this.http.get<Pagina<LibroDato>>(`${this.apiUrl}/libro/listar/${pagina}`,
       { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } });
  }
  
}
