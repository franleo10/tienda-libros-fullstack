import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FacturaDato, nombreMetodoPago } from '../../../models/factura';
import { Pago } from '../../../services/pago';

type Estado = 'confirmando' | 'confirmada' | 'pendiente' | 'sin-pago' | 'error';

@Component({
  selector: 'app-compra-confirmada',
  imports: [DatePipe, RouterLink],
  templateUrl: './compra-confirmada.html',
  styleUrl: './compra-confirmada.css',
})
export class CompraConfirmada implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly pagoService = inject(Pago);

  protected readonly estado = signal<Estado>('confirmando');
  protected readonly factura = signal<FacturaDato | null>(null);
  protected readonly mensaje = signal('');
  private readonly formateadorPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });

  ngOnInit() {
    // MercadoPago vuelve con ?payment_id=...&status=... (o "null" si el usuario no llegó a pagar)
    const parametros = this.route.snapshot.queryParamMap;
    const idPago = parametros.get('payment_id') ?? parametros.get('collection_id');
    if (!idPago || idPago === 'null') {
      this.estado.set('sin-pago');
      return;
    }

    this.pagoService.confirmar(idPago).subscribe({
      next: (factura) => {
        this.factura.set(factura);
        this.estado.set('confirmada');
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 409) {
          this.estado.set('pendiente');
          this.mensaje.set(
            typeof error.error === 'string' && error.error
              ? error.error
              : 'Tu pago todavía no fue aprobado.'
          );
          return;
        }
        this.estado.set('error');
        this.mensaje.set(
          error.status === 401
            ? 'Iniciá sesión para confirmar tu compra.'
            : error.status === 403
              ? 'Esta compra pertenece a otra cuenta.'
              : error.status === 404
                ? 'No encontramos el pago indicado.'
                : 'No pudimos confirmar tu compra. Si el pago se acreditó, los libros van a aparecer en tu biblioteca en unos minutos.'
        );
      },
    });
  }

  protected formatearPrecio(precio: number) {
    return this.formateadorPrecio.format(precio);
  }

  protected readonly nombreMetodoPago = nombreMetodoPago;
}
