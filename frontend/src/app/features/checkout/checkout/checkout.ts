import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { PerfilService } from '../../../services/perfil';
import { Carrito } from '../../../services/carrito';
import { Pago } from '../../../services/pago';
import { PerfilUsuario } from '../../../models/perfil';
import { LibroEnCarrito } from '../../../models/carrito';
import { forkJoin } from 'rxjs';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-checkout',
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css',
})
export class Checkout implements OnInit {
  private perfilService = inject(PerfilService);
  private carritoService = inject(Carrito);
  private pagoService = inject(Pago);

  protected readonly perfil = signal<PerfilUsuario | null>(null);
  protected readonly libros = signal<LibroEnCarrito[]>([]);
  protected readonly cargando = signal(true);
  protected readonly pagando = signal(false);
  protected readonly mensajeError = signal('');

  protected readonly total = computed(() =>
    this.libros().reduce((suma, libro) => suma + libro.precio, 0));

  ngOnInit(): void {
    forkJoin({
      perfil: this.perfilService.obtenerPerfil(),
      carrito: this.carritoService.obtener(),
    }).subscribe({
      next: (datos) => {
        this.perfil.set(datos.perfil);
        this.libros.set(datos.carrito.libros);
        this.cargando.set(false);
      },
      error: () => {
        this.mensajeError.set('No se pudieron cargar tus datos. Intentá de nuevo')
        this.cargando.set(false);
      },
    });
  }

  pagar() {
    const perfil = this.perfil();
    if (!perfil) {
      return;
    }

    this.pagando.set(true);
    this.mensajeError.set('');

    this.pagoService.obtenerLinkDePago(perfil.id).subscribe({
      next: (url) => {
        window.location.href = url;
      },
      error: () => {
        this.mensajeError.set('No se pudo iniciar el pago. Intentá de nuevo.');
        this.pagando.set(false);
      },
    });
  }


}
