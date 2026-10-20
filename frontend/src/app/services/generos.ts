import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class Generos {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  listar(){
    return this.http.get<{ idGenero: number; nombre: string }[]>(`${this.apiUrl}/generos/listar`);
  }
}
