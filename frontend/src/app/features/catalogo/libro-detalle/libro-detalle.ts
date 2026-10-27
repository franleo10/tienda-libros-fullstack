import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Libro } from '../../../services/libro';
import { LibroDato } from '../../../models/libro';
import { Carrito } from '../../../services/carrito';
import { Biblioteca } from '../../../services/biblioteca';
import { RouterLink } from '@angular/router';
import { ReseniaFormulario } from '../../resenias/resenia-formulario/resenia-formulario';
import { ReseniaListado } from '../../resenias/resenia-listado/resenia-listado';

@Component({
  selector: 'app-libro-detalle',
  imports: [RouterLink, ReseniaFormulario, ReseniaListado],
  templateUrl: './libro-detalle.html',
  styleUrl: './libro-detalle.css',
})
export class LibroDetalle implements OnInit {

  private route = inject(ActivatedRoute);
  private carritoService = inject(Carrito);
  private readonly libroService = inject(Libro);
  private readonly bibliotecaService = inject(Biblioteca);
  protected readonly libro = signal<LibroDato | null>(null);
  protected readonly yaComprado = signal(false);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  protected readonly mensajeCarrito = signal('');
  protected readonly reseniaEnviada = signal(false);

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    // Si el libro ya está en la biblioteca del usuario, no se ofrece agregarlo al carrito.
    // Si falla la consulta se muestra el botón igual: el backend rechaza agregar un libro ya comprado.
    this.bibliotecaService.obtenerMiBiblioteca().subscribe({
      next: (biblioteca) => this.yaComprado.set(biblioteca.libros.some((l) => l.idLibro === id)),
      error: () => this.yaComprado.set(false),
    });

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
