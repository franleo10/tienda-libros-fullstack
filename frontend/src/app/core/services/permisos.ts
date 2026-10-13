import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Permisos {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/me/permisos';

  cargar(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
}
