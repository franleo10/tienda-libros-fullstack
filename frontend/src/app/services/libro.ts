import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { LibroDato, LibroGuardado } from '../models/libro';
import { Pagina } from '../models/autor';


@Injectable({
  providedIn: 'root',
})
export class Libro {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  obtenerLibros(pagina: number) {
    return this.http.get<Pagina<LibroDato>>(`${this.apiUrl}/libro/listar/${pagina}`);
  }

  obtenerLibro(id: number) {
    return this.http.get<LibroDato>(`${this.apiUrl}/libro/buscar/id/${id}`);
  }

  darDeBaja(id: number){
    return this.http.put(`${this.apiUrl}/libro/baja/${id}`,null, {responseType: 'text'});
  }

  crear(idAutor: number, idGenero: number, libro: LibroGuardado){
    return this.http.post<LibroDato>(`${this.apiUrl}/libro/crear/${idAutor}/${idGenero}`, libro);
  }

  modificar(id: number, libro: LibroGuardado){
    return this.http.put<LibroDato>(`${this.apiUrl}/libro/modificar/${id}`, libro);
  }

}
