import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FacturaDato } from '../../../models/factura';
import { Factura as FacturaService } from '../../../services/factura';

@Component({
  selector: 'app-historial-compras',
  imports: [DatePipe, RouterLink],
  templateUrl: './historial-compras.html',
  styleUrl: './historial-compras.css',
})
export class HistorialCompras implements OnInit {
  private readonly facturaService = inject(FacturaService);

  protected readonly compras = signal<FacturaDato[]>([]);
  protected readonly paginaActual = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  private readonly formateadorPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });

  ngOnInit() {
    this.cargarCompras(0);
  }

  protected cambiarPagina(pagina: number) {
    this.cargarCompras(pagina);
  }

  protected formatearPrecio(precio: number) {
    return this.formateadorPrecio.format(precio);
  }

  // "Rayuela", "Rayuela y Ficciones", "Rayuela, Ficciones y 2 más"
  protected resumirLibros(titulos: string[]) {
    if (titulos.length <= 2) return titulos.join(' y ');
    const restantes = titulos.length - 2;
    return `${titulos[0]}, ${titulos[1]} y ${restantes} más`;
  }

  private cargarCompras(pagina: number) {
    this.cargando.set(true);
    this.mensajeError.set('');

    this.facturaService.listarMisCompras(pagina).subscribe({
      next: (resultado) => {
        this.compras.set(resultado.content);
        this.paginaActual.set(resultado.number);
        this.totalPaginas.set(resultado.totalPages);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para ver tus compras.'
            : 'No pudimos cargar tus compras. Intentá de nuevo.'
        );
      },
    });
  }
}
