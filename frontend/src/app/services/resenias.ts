import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ReseniaDato, ReseniaNueva } from '../models/resenia';
import { Pagina } from '../models/autor';

@Injectable({
  providedIn: 'root',
})
export class Resenias {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/resenias';

  crear(idLibro: number, resenia: ReseniaNueva) {
    return this.http.post<ReseniaDato>(`${this.apiUrl}/crear/libro/${idLibro}`, resenia);
  }

  listarPorLibro(idLibro: number, pagina: number){
    return this.http.get<Pagina<ReseniaDato>>(`${this.apiUrl}/listar/${idLibro}/pag/${pagina}`)
  }
}
