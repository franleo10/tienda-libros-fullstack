import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AutorDetalle, AutorGuardado, AutorResumen, Pagina } from '../models/autor';

@Injectable({
  providedIn: 'root',
})
export class Autor {


  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/autores';

  listar(pagina = 0) {
    return this.http.get<Pagina<AutorResumen>>(
      `${this.apiUrl}/listar/${pagina}`
    );
  }

  listarInactivos(pagina = 0) {
    return this.http.get<Pagina<AutorResumen>>(`${this.apiUrl}/listar/inactivos/${pagina}`);
  }

  buscar(nombre: string, pagina = 0) {
    return this.http.get<Pagina<AutorResumen>>(`${this.apiUrl}/buscar`, {
      params: { nombre, pagina },
    });
  }

  buscarInactivos(nombre: string, pagina = 0) {
    return this.http.get<Pagina<AutorResumen>>(`${this.apiUrl}/buscar/inactivos`, {
      params: { nombre, pagina },
    });
  }

  detalle(id: number) {
    return this.http.get<AutorDetalle>(`${this.apiUrl}/${id}`);
  }

  crear(autor: AutorGuardado) {
    return this.http.post(`${this.apiUrl}/crear`, autor, { responseType: 'text' });
  }

  actualizar(id: number, autor: AutorGuardado) {
    return this.http.put(`${this.apiUrl}/actualizar-autor/${id}`, autor, { responseType: 'text' });
  }

  darDeBaja(id: number) {
    return this.http.put(`${this.apiUrl}/baja/${id}`, null, { responseType: 'text' });
  }

  darDeAlta(id: number) {
    return this.http.put(`${this.apiUrl}/alta/${id}`, null, { responseType: 'text' });
  }
}
