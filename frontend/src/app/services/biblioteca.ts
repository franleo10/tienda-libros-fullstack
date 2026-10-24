import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BibliotecaDato } from '../models/biblioteca';

@Injectable({
  providedIn: 'root',
})
export class Biblioteca {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/biblioteca';

  obtenerMiBiblioteca() {
    return this.http.get<BibliotecaDato>(`${this.apiUrl}/usuario`);
  }
}
