import { Component, inject, OnInit, signal } from '@angular/core';
import { Libro } from '../../../services/libro';
import { toSignal } from '@angular/core/rxjs-interop';
import { LibroDato } from '../../../models/libro';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-libro-tabla',
  imports: [RouterLink],
  templateUrl: './libro-tabla.html',
  styleUrl: './libro-tabla.css',
})
export class LibroTabla implements OnInit {

  private libroService = inject(Libro);
  protected readonly libros = signal<LibroDato[]>([]);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');

  ngOnInit(): void {
    this.libroService.obtenerLibros(0).subscribe({
      next: (respuesta) => {
        this.libros.set(respuesta.content);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.mensajeError.set('No se pudieron cargar los libros');
      }
    });
  }

  eliminar(libro: LibroDato) {

    if (!confirm('¿Eliminar "' + libro.titulo + '"?')) return;
    this.mensajeError.set('');
    this.libroService.darDeBaja(libro.id).subscribe({
      next: (respuesta) => { this.libros.update(lista => lista.filter(l => l.id !== libro.id)) },
      error: (error) => { this.mensajeError.set('No se pudo eliminar el libro') },
    })
  }

}
