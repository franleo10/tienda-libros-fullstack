import { Component, inject, OnInit, signal } from '@angular/core';
import { Libro } from '../../../services/libro';
import { LibroDato } from '../../../models/libro';
import { RouterLink } from '@angular/router';
import { Portada } from '../../../shared/portada/portada';

@Component({
  selector: 'app-libro-tabla',
  imports: [RouterLink, Portada],
  templateUrl: './libro-tabla.html',
  styleUrl: './libro-tabla.css',
})
export class LibroTabla implements OnInit {

  private libroService = inject(Libro);
  protected readonly libros = signal<LibroDato[]>([]);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  protected readonly paginaActual = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly totalLibros = signal(0);

  private readonly formatoPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });

  ngOnInit(): void {
    this.cargarPagina(0);
  }

  protected cargarPagina(pagina: number) {
    this.cargando.set(true);
    this.libroService.obtenerLibros(pagina).subscribe({
      next: (respuesta) => {
        this.libros.set(respuesta.content);
        this.paginaActual.set(respuesta.number);
        this.totalPaginas.set(respuesta.totalPages);
        this.totalLibros.set(respuesta.totalElements);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.mensajeError.set('No se pudieron cargar los libros');
      }
    });
  }

  protected formatearPrecio(precio: number) {
    return this.formatoPrecio.format(precio);
  }

  eliminar(libro: LibroDato) {

    if (!confirm('¿Eliminar "' + libro.titulo + '"?')) return;
    this.mensajeError.set('');
    this.libroService.darDeBaja(libro.id).subscribe({
      next: () => {
        this.libros.update(lista => lista.filter(l => l.id !== libro.id));
        this.totalLibros.update(total => total - 1);
      },
      error: () => { this.mensajeError.set('No se pudo eliminar el libro') },
    })
  }

}
