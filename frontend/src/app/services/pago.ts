import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { FacturaDato } from '../models/factura';

@Injectable({
  providedIn: 'root',
})
export class Pago {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/pagos';

  confirmar(idPago: string) {
    return this.http.post<FacturaDato>(`${this.apiUrl}/${idPago}/confirmar`, null);
  }
}
