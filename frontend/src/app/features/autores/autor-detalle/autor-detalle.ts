import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AutorDetalle as AutorDetalleData } from '../../../models/autor';
import { Autor } from '../../../services/autor';
import { Permisos } from '../../../core/services/permisos';

@Component({
  selector: 'app-autor-detalle',
  imports: [DatePipe, DecimalPipe, RouterLink],
  templateUrl: './autor-detalle.html',
  styleUrl: './autor-detalle.css',
})
export class AutorDetalle implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly autorService = inject(Autor);
  private readonly permisosService = inject(Permisos);

  protected readonly autor = signal<AutorDetalleData | null>(null);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  protected readonly puedeEditar = signal(false);
  protected readonly mensajePermisos = signal('');
  private readonly formateadorPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });

  ngOnInit() {
    this.verificarPermisos();
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (!Number.isInteger(id) || id <= 0) {
        this.autor.set(null);
        this.cargando.set(false);
        this.mensajeError.set('El identificador del autor no es válido.');
        return;
      }

      this.cargarAutor(id);
    });
  }

  private verificarPermisos() {
    this.permisosService.cargar().subscribe({
      next: (permisos) => this.puedeEditar.set(permisos.includes('MODIFICAR_AUTOR')),
      error: () => {
        this.puedeEditar.set(false);
        this.mensajePermisos.set(
          'No se pudo verificar tu permiso para editar autores. La opción de edición está oculta.'
        );
      },
    });
  }

  protected calificacionPromedio(resenias: AutorDetalleData['libros'][number]['resenias']) {
    if (resenias.length === 0) {
      return null;
    }

    return resenias.reduce((total, resenia) => total + resenia.calificacion, 0) / resenias.length;
  }

  protected formatearPrecio(precio: number) {
    return this.formateadorPrecio.format(precio);
  }

  private cargarAutor(id: number) {
    this.cargando.set(true);
    this.mensajeError.set('');

    this.autorService.detalle(id).subscribe({
      next: (autor) => {
        this.autor.set(autor);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.autor.set(null);
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para consultar el detalle del autor.'
            : error.status === 404
              ? 'No se encontró el autor solicitado.'
              : 'No se pudo cargar el detalle del autor. Intentá de nuevo.'
        );
      },
    });
  }
}
