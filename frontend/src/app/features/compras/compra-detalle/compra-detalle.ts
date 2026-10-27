import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FacturaDato, nombreMetodoPago } from '../../../models/factura';
import { Factura as FacturaService } from '../../../services/factura';
import { PerfilService } from '../../../services/perfil';
import { PerfilUsuario } from '../../../models/perfil';

@Component({
  selector: 'app-compra-detalle',
  imports: [DatePipe, RouterLink],
  templateUrl: './compra-detalle.html',
  styleUrl: './compra-detalle.css',
})
export class CompraDetalle implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly facturaService = inject(FacturaService);

  protected readonly compra = signal<FacturaDato | null>(null);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  protected readonly nombreMetodoPago = nombreMetodoPago;
  private readonly formateadorPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });
  private readonly perfilService = inject(PerfilService);
  protected readonly comprador = signal<PerfilUsuario | null>(null);

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(id) || id <= 0) {
      this.cargando.set(false);
      this.mensajeError.set('No encontramos esa compra.');
      return;
    }

    this.facturaService.obtener(id).subscribe({
      next: (compra) => {
        this.compra.set(compra);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para ver tus compras.'
            : error.status === 404
              ? 'No encontramos esa compra.'
              : 'No pudimos cargar la compra. Intentá de nuevo.'
        );
      },
    });

    this.perfilService.obtenerPerfil().subscribe({
      next: (perfil) => this.comprador.set(perfil),
    });
  }

  protected formatearPrecio(precio: number) {
    return this.formateadorPrecio.format(precio);
  }

  protected imprimirFactura() {
    window.print();
  }
}
