import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

interface AuthResponse {
  token: string;
}

@Injectable({
  providedIn: 'root',
})



export class Auth {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  login(email: string, password: string) {
  return this.http.post<AuthResponse>(`${this.apiUrl}/auth`, {username: email, password}
  );
  }

}
