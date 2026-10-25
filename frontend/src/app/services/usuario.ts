import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Pagina } from '../models/autor';
import { FiltrosUsuarios, UsuarioAdmin } from '../models/usuario';

@Injectable({
  providedIn: 'root',
})
export class Usuario {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/usuario';

  listar(pagina: number, filtros: FiltrosUsuarios) {
    let params = new HttpParams();
    if (filtros.busqueda.trim()) params = params.set('busqueda', filtros.busqueda.trim());
    if (filtros.rol) params = params.set('rol', filtros.rol);
    if (filtros.activo) params = params.set('activo', filtros.activo);

    return this.http.get<Pagina<UsuarioAdmin>>(`${this.apiUrl}/admin/listar/${pagina}`, { params });
  }

  darDeBaja(id: number) {
    return this.http.put(`${this.apiUrl}/baja/${id}`, null, { responseType: 'text' });
  }

  darDeAlta(id: number) {
    return this.http.put(`${this.apiUrl}/alta/${id}`, null, { responseType: 'text' });
  }
}
