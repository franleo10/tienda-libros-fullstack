import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';

interface AuthResponse {
  token: string;
}

@Injectable({
  providedIn: 'root',
})



export class Auth {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';
  readonly token = signal<string | null>(
    typeof localStorage === 'undefined' ? null : localStorage.getItem('token')
  );

  login(email: string, password: string) {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/auth`, { username: email, password })
      .pipe(tap((respuesta) => this.guardarToken(respuesta.token)));
  }

  register(nombre: string, edad: number, email: string, password: string){
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/auth/register`, {nombre,edad,email,password})
      .pipe(tap((respuesta) => this.guardarToken(respuesta.token)));
  }

  logout() {
    localStorage.removeItem('token');
    this.token.set(null);
  }

  actualizarToken(token: string) {
    localStorage.setItem('token', token);
    this.token.set(token);
  }

  private guardarToken(token: string) {
    this.actualizarToken(token);
  }

}
