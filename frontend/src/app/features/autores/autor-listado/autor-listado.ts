import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AutorResumen } from '../../../models/autor';
import { Autor } from '../../../services/autor';
import { Permisos } from '../../../core/services/permisos';

@Component({
  selector: 'app-autor-listado',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './autor-listado.html',
  styleUrl: './autor-listado.css',
})
export class AutorListado implements OnInit {
  private autorService = inject(Autor);
  private readonly permisosService = inject(Permisos);
  private readonly route = inject(ActivatedRoute);

  protected readonly autores = signal<AutorResumen[]>([]);
  protected readonly paginaActual = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal('');
  protected readonly mensajeErrorEliminacion = signal('');
  protected readonly mensajePermisos = signal('');
  protected readonly mensajeResultado = signal('');
  protected readonly puedeAgregar = signal(false);
  protected readonly puedeEditar = signal(false);
  protected readonly puedeEliminar = signal(false);
  protected readonly viendoInactivos = signal(false);
  protected readonly autorEliminando = signal<number | null>(null);
  protected readonly busqueda = new FormControl('', { nonNullable: true });
  protected readonly formulario = new FormGroup({ busqueda: this.busqueda });

  ngOnInit() {
    this.verificarPermisos();
    this.route.queryParamMap.subscribe((params) => {
      const acceso = params.get('acceso');
      this.mensajePermisos.set(
        acceso === 'denegado'
          ? 'Tu cuenta no tiene permiso para administrar autores.'
          : acceso === 'verificacion'
            ? 'No se pudieron verificar tus permisos. Intentá nuevamente más tarde.'
            : ''
      );
      const resultado = params.get('resultado');
      this.mensajeResultado.set(
        resultado === 'creado'
          ? 'El autor se creó correctamente.'
          : resultado === 'actualizado'
            ? 'Los cambios del autor se guardaron correctamente.'
            : ''
      );
    });
    this.cargarAutores(0);
  }

  protected buscar() {
    this.cargarAutores(0);
  }

  protected limpiarBusqueda() {
    this.busqueda.setValue('');
    this.cargarAutores(0);
  }

  protected cambiarPagina(pagina: number) {
    this.cargarAutores(pagina);
  }

  protected alternarAutoresInactivos() {
    this.viendoInactivos.update((viendoInactivos) => !viendoInactivos);
    this.cargarAutores(0);
  }

  protected eliminarAutor(autor: AutorResumen) {
    const nombreCompleto = `${autor.nombre} ${autor.apellido}`;
    if (
      !window.confirm(
        `¿Dar de baja a ${nombreCompleto}? Dejará de aparecer en el catálogo, pero se conservarán sus datos y sus libros.`
      )
    ) {
      return;
    }

    this.autorEliminando.set(autor.idAutor);
    this.mensajeErrorEliminacion.set('');
    this.mensajeResultado.set('');

    this.autorService.darDeBaja(autor.idAutor).subscribe({
      next: () => {
        this.autorEliminando.set(null);
        this.mensajeResultado.set(`El autor ${nombreCompleto} se dio de baja correctamente.`);
        this.cargarAutores(this.paginaActual(), true);
      },
      error: (error: HttpErrorResponse) => {
        this.autorEliminando.set(null);
        this.mensajeErrorEliminacion.set(
          error.status === 401
            ? 'Iniciá sesión nuevamente para eliminar autores.'
            : error.status === 403
              ? 'Tu cuenta no tiene permiso para eliminar autores.'
              : 'No se pudo dar de baja el autor. Intentá de nuevo.'
        );
      },
    });
  }

  protected reactivarAutor(autor: AutorResumen) {
    const nombreCompleto = `${autor.nombre} ${autor.apellido}`;
    if (!window.confirm(`¿Querés volver a activar a ${nombreCompleto}?`)) {
      return;
    }

    this.autorEliminando.set(autor.idAutor);
    this.mensajeErrorEliminacion.set('');
    this.mensajeResultado.set('');

    this.autorService.darDeAlta(autor.idAutor).subscribe({
      next: () => {
        this.autorEliminando.set(null);
        this.mensajeResultado.set(`El autor ${nombreCompleto} volvió a estar activo.`);
        this.cargarAutores(this.paginaActual(), true);
      },
      error: (error: HttpErrorResponse) => {
        this.autorEliminando.set(null);
        this.mensajeErrorEliminacion.set(
          error.status === 401
            ? 'Iniciá sesión nuevamente para reactivar autores.'
            : error.status === 403
              ? 'Tu cuenta no tiene permiso para reactivar autores.'
              : 'No se pudo reactivar el autor. Intentá de nuevo.'
        );
      },
    });
  }

  private verificarPermisos() {
    this.permisosService.cargar().subscribe({
      next: (permisos) => {
        this.puedeAgregar.set(permisos.includes('AGREGAR_AUTOR'));
        this.puedeEditar.set(permisos.includes('MODIFICAR_AUTOR'));
        this.puedeEliminar.set(permisos.includes('ELIMINAR_AUTOR'));
      },
      error: () => {
        this.puedeAgregar.set(false);
        this.puedeEditar.set(false);
        this.puedeEliminar.set(false);
        this.mensajePermisos.set(
          'No se pudieron verificar tus permisos de administración. Las opciones de edición están ocultas.'
        );
      },
    });
  }

  private cargarAutores(pagina: number, volverPaginaSiQuedaVacia = false) {
    const termino = this.busqueda.value.trim();
    this.cargando.set(true);
    this.mensajeError.set('');

    const solicitud = this.viendoInactivos()
      ? termino
        ? this.autorService.buscarInactivos(termino, pagina)
        : this.autorService.listarInactivos(pagina)
      : termino
        ? this.autorService.buscar(termino, pagina)
        : this.autorService.listar(pagina);

    solicitud.subscribe({
      next: (resultado) => {
        if (volverPaginaSiQuedaVacia && pagina > 0 && resultado.content.length === 0) {
          this.cargarAutores(pagina - 1);
          return;
        }
        this.autores.set(resultado.content);
        this.paginaActual.set(resultado.number);
        this.totalPaginas.set(resultado.totalPages);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para consultar los autores.'
            : 'No se pudieron cargar los autores. Intentá de nuevo.'
        );
      },
    });
  }
}
