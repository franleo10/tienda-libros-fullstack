import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})

export class Carrito {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';


  agregarLibro(idLibro: number){
    return this.http.post(`${this.apiUrl}/carrito/libro/${idLibro}`, null,{ responseType: 'text' });
  }
}
