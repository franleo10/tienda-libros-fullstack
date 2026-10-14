import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Libro } from '../../../services/libro';
import { LibroDato } from '../../../models/libro';
import { Carrito } from '../../../services/carrito';

@Component({
  selector: 'app-libro-detalle',
  imports: [],
  templateUrl: './libro-detalle.html',
  styleUrl: './libro-detalle.css',
})
export class LibroDetalle implements OnInit {

  private route = inject(ActivatedRoute);
  private carritoService = inject(Carrito);
  private readonly libroService = inject(Libro);
  protected readonly libro = signal<LibroDato | null>(null);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  protected readonly mensajeCarrito = signal('');

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.libroService.obtenerLibro(id).subscribe({
      next: (libro) => {
        this.libro.set(libro);
        this.cargando.set(false);
      },
      error: (error) => {
        this.cargando.set(false);
        if (error.status === 404) {
          this.mensajeError.set('El libro no existe');
        }
        else {
          this.mensajeError.set('Error al cargar el libro');
        }
      },
    });
  }

  agregarAlCarrito() {
    const libro = this.libro();
    if (!libro) return;

    this.carritoService.agregarLibro(libro.id).subscribe({
      next: (respuesta) => { this.mensajeCarrito.set(respuesta) },
      error: (error) => {
        if (error.status === 409) {
          this.mensajeCarrito.set(error.error);
        } else if (error.status === 404) {
          this.mensajeCarrito.set('El libro no existe');
        } else {
          this.mensajeCarrito.set('No se pudo agregar al carrito');
        }
      },
    });

  }


}
