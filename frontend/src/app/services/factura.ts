import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Pagina } from '../models/autor';
import { FacturaDato } from '../models/factura';

@Injectable({
  providedIn: 'root',
})
export class Factura {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/factura';

  // Compras del usuario logueado, de la más reciente a la más antigua
  listarMisCompras(pagina: number) {
    return this.http.get<Pagina<FacturaDato>>(`${this.apiUrl}/usuario/pag/${pagina}`);
  }

  obtener(idFactura: number) {
    return this.http.get<FacturaDato>(`${this.apiUrl}/${idFactura}`);
  }
}
