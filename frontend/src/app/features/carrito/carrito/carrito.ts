import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LibroEnCarrito } from '../../../models/carrito';
import { Carrito as CarritoService } from '../../../services/carrito';

@Component({
  selector: 'app-carrito',
  imports: [RouterLink],
  templateUrl: './carrito.html',
  styleUrl: './carrito.css',
})
export class Carrito implements OnInit {
  private readonly carritoService = inject(CarritoService);

  protected readonly libros = signal<LibroEnCarrito[]>([]);
  protected readonly total = computed(() =>
    this.libros().reduce((suma, libro) => suma + libro.precio, 0)
  );
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  protected readonly mensajeErrorQuitar = signal('');
  protected readonly libroQuitando = signal<number | null>(null);
  private readonly formateadorPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });

  ngOnInit() {
    this.carritoService.obtener().subscribe({
      next: (carrito) => {
        this.libros.set(carrito.libros);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para ver tu carrito.'
            : error.status === 404
              ? 'No encontramos un carrito asociado a tu cuenta.'
              : 'No se pudo cargar el carrito. Intentá de nuevo.'
        );
      },
    });
  }

  protected quitar(libro: LibroEnCarrito) {
    if (this.libroQuitando() !== null) {
      return;
    }

    this.libroQuitando.set(libro.idLibro);
    this.mensajeErrorQuitar.set('');
    this.carritoService.quitarLibro(libro.idLibro).subscribe({
      next: () => {
        this.libros.update((libros) => libros.filter((l) => l.idLibro !== libro.idLibro));
        this.libroQuitando.set(null);
      },
      error: (error: HttpErrorResponse) => {
        this.libroQuitando.set(null);
        if (error.status === 404) {
          // El libro ya no estaba en el carrito: sincronizamos la vista.
          this.libros.update((libros) => libros.filter((l) => l.idLibro !== libro.idLibro));
          return;
        }
        this.mensajeErrorQuitar.set(`No se pudo quitar "${libro.titulo}" del carrito. Intentá de nuevo.`);
      },
    });
  }

  protected formatearPrecio(precio: number) {
    return this.formateadorPrecio.format(precio);
  }
}
