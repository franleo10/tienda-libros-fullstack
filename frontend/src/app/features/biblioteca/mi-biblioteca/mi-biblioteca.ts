import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LibroEnBiblioteca } from '../../../models/biblioteca';
import { Biblioteca } from '../../../services/biblioteca';

@Component({
  selector: 'app-mi-biblioteca',
  imports: [RouterLink],
  templateUrl: './mi-biblioteca.html',
  styleUrl: './mi-biblioteca.css',
})
export class MiBiblioteca implements OnInit {
  private readonly bibliotecaService = inject(Biblioteca);

  protected readonly libros = signal<LibroEnBiblioteca[]>([]);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');

  ngOnInit() {
    this.bibliotecaService.obtenerMiBiblioteca().subscribe({
      next: (biblioteca) => {
        const libros = [...biblioteca.libros].sort((a, b) => a.titulo.localeCompare(b.titulo, 'es'));
        this.libros.set(libros);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para ver tu biblioteca.'
            : error.status === 404
              ? 'No encontramos una biblioteca asociada a tu cuenta.'
              : 'No se pudo cargar tu biblioteca. Intentá de nuevo.'
        );
      },
    });
  }

  protected nombreAutores(libro: LibroEnBiblioteca) {
    return libro.autores.map((autor) => `${autor.nombre} ${autor.apellido}`.trim()).join(', ');
  }
}
