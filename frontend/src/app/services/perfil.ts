import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ActualizarPerfil, CambiarPassword, PerfilUsuario, RespuestaToken } from '../models/perfil';

@Injectable({
  providedIn: 'root',
})
export class PerfilService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/me';

  obtenerPerfil() {
    return this.http.get<PerfilUsuario>(`${this.apiUrl}/perfil`);
  }

  actualizarPerfil(datos: ActualizarPerfil) {
    return this.http.put<RespuestaToken>(`${this.apiUrl}/perfil`, datos);
  }

  cambiarPassword(datos: CambiarPassword) {
    return this.http.put<RespuestaToken>(`${this.apiUrl}/password`, datos);
  }
}
