import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ReseniaDato, ReseniaNueva } from '../models/resenia';

@Injectable({
  providedIn: 'root',
})
export class Resenias {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/resenias';

  crear(idLibro: number, resenia: ReseniaNueva) {
    return this.http.post<ReseniaDato>(`${this.apiUrl}/crear/libro/${idLibro}`, resenia);
  }


}
