import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';

interface AuthResponse {
  token: string;
}

// Lo que nos interesa del contenido (payload) del JWT que arma el backend
interface DatosToken {
  sub: string; // email del usuario
  exp: number; // vencimiento, en segundos desde 1970
}

const CLAVE_TOKEN = 'token';

// Un JWT son tres partes separadas por puntos; la del medio es un JSON codificado en Base64
function leerDatosToken(token: string): DatosToken | null {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(payload)) as DatosToken;
  } catch {
    return null;
  }
}

// Si no se puede leer el vencimiento, decide el backend: responde 401 y el interceptor cierra la sesión
function tokenVencido(token: string): boolean {
  const exp = leerDatosToken(token)?.exp;
  return typeof exp === 'number' && exp * 1000 <= Date.now();
}

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  // Estado global de la sesión: todos los componentes leen estas señales
  readonly token = signal<string | null>(this.leerTokenGuardado());
  readonly sesionIniciada = computed(() => this.token() !== null);
  readonly usuario = computed(() => {
    const token = this.token();
    return token ? (leerDatosToken(token)?.sub ?? null) : null;
  });

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
    localStorage.removeItem(CLAVE_TOKEN);
    this.token.set(null);
  }

  actualizarToken(token: string) {
    localStorage.setItem(CLAVE_TOKEN, token);
    this.token.set(token);
  }

  // Si el token venció mientras la app estaba abierta, cierra la sesión
  sesionVigente(): boolean {
    const token = this.token();
    if (token && tokenVencido(token)) {
      this.logout();
    }
    return this.sesionIniciada();
  }

  private guardarToken(token: string) {
    this.actualizarToken(token);
  }

  // Al abrir la app se descarta el token guardado si ya venció
  private leerTokenGuardado(): string | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }
    const token = localStorage.getItem(CLAVE_TOKEN);
    if (token && tokenVencido(token)) {
      localStorage.removeItem(CLAVE_TOKEN);
      return null;
    }
    return token;
  }

}
