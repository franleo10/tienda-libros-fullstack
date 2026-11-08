import { Component, inject, OnInit, signal } from '@angular/core';
import { Libro } from '../../../services/libro';
import { LibroDato } from '../../../models/libro';
import { RouterLink } from '@angular/router';
import { Portada } from '../../../shared/portada/portada';

@Component({
  selector: 'app-libro-listado',
  imports: [RouterLink, Portada],
  templateUrl: './libro-listado.html',
  styleUrl: './libro-listado.css',
})

export class LibroListado implements OnInit{

  ngOnInit(){
    this.cargarPagina(0);
  }

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

  protected cargarPagina(pagina: number) {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.libroService.obtenerLibros(pagina).subscribe({
      next: (resultado) => {
        this.libros.set(resultado.content);
        this.paginaActual.set(resultado.number);
        this.totalPaginas.set(resultado.totalPages);
        this.totalLibros.set(resultado.totalElements);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.mensajeError.set('No se pudieron cargar los libros. Intentá de nuevo.');
      },
    });
  }

  protected formatearPrecio(precio: number) {
    return this.formatoPrecio.format(precio);
  }

  protected nombreAutor(libro: LibroDato) {
    const autor = libro.autores[0];
    return autor ? `${autor.nombre} ${autor.apellido}` : 'Autor desconocido';
  }
}
