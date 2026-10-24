import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CarritoDato } from '../models/carrito';

@Injectable({
  providedIn: 'root',
})

export class Carrito {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';


  agregarLibro(idLibro: number){
    return this.http.post(`${this.apiUrl}/carrito/libro/${idLibro}`, null,{ responseType: 'text' });
  }

  obtener() {
    return this.http.get<CarritoDato>(`${this.apiUrl}/carrito/listar/usuario`);
  }

  quitarLibro(idLibro: number) {
    return this.http.put(`${this.apiUrl}/carrito/eliminar-libro/${idLibro}`, null, { responseType: 'text' });
  }
}
