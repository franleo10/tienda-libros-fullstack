import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { FiltrosUsuarios, UsuarioAdmin } from '../../../models/usuario';
import { Usuario as UsuarioService } from '../../../services/usuario';

@Component({
  selector: 'app-usuario-tabla',
  imports: [ReactiveFormsModule],
  templateUrl: './usuario-tabla.html',
  styleUrl: './usuario-tabla.css',
})
export class UsuarioTabla implements OnInit {
  private readonly usuarioService = inject(UsuarioService);

  protected readonly usuarios = signal<UsuarioAdmin[]>([]);
  protected readonly paginaActual = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly totalUsuarios = signal(0);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal('');
  protected readonly mensajeAccionError = signal('');
  protected readonly mensajeResultado = signal('');
  protected readonly usuarioProcesando = signal<number | null>(null);

  protected readonly filtros = new FormGroup({
    busqueda: new FormControl('', { nonNullable: true }),
    rol: new FormControl<FiltrosUsuarios['rol']>('', { nonNullable: true }),
    activo: new FormControl<FiltrosUsuarios['activo']>('', { nonNullable: true }),
  });

  ngOnInit() {
    this.cargarUsuarios(0);
  }

  protected buscar() {
    this.cargarUsuarios(0);
  }

  protected limpiarFiltros() {
    this.filtros.reset();
    this.cargarUsuarios(0);
  }

  protected hayFiltros() {
    const { busqueda, rol, activo } = this.filtros.getRawValue();
    return !!(busqueda || rol || activo);
  }

  protected cambiarPagina(pagina: number) {
    this.cargarUsuarios(pagina);
  }

  protected banear(usuario: UsuarioAdmin) {
    if (
      !window.confirm(
        `¿Banear a ${usuario.nombre}? No va a poder iniciar sesión hasta que lo reactives.`
      )
    ) {
      return;
    }
    this.cambiarEstado(usuario, false);
  }

  protected reactivar(usuario: UsuarioAdmin) {
    if (!window.confirm(`¿Reactivar a ${usuario.nombre}? Va a poder volver a iniciar sesión.`)) {
      return;
    }
    this.cambiarEstado(usuario, true);
  }

  private cambiarEstado(usuario: UsuarioAdmin, activar: boolean) {
    this.usuarioProcesando.set(usuario.id);
    this.mensajeAccionError.set('');
    this.mensajeResultado.set('');

    const solicitud = activar
      ? this.usuarioService.darDeAlta(usuario.id)
      : this.usuarioService.darDeBaja(usuario.id);

    solicitud.subscribe({
      next: () => {
        this.usuarioProcesando.set(null);
        this.mensajeResultado.set(
          activar ? `${usuario.nombre} volvió a estar activo.` : `${usuario.nombre} fue baneado.`
        );
        this.cargarUsuarios(this.paginaActual(), true);
      },
      error: (error: HttpErrorResponse) => {
        this.usuarioProcesando.set(null);
        this.mensajeAccionError.set(
          error.status === 401
            ? 'Iniciá sesión nuevamente para administrar usuarios.'
            : error.status === 403
              ? 'Tu cuenta no tiene permiso para administrar usuarios.'
              : typeof error.error === 'string' && error.error
                ? error.error
                : 'No se pudo actualizar el usuario. Intentá de nuevo.'
        );
      },
    });
  }

  // Si al banear se vacía la página (por ejemplo, filtrando solo activos), vuelve a la anterior
  private cargarUsuarios(pagina: number, volverPaginaSiQuedaVacia = false) {
    this.cargando.set(true);
    this.mensajeError.set('');

    this.usuarioService.listar(pagina, this.filtros.getRawValue()).subscribe({
      next: (resultado) => {
        if (volverPaginaSiQuedaVacia && pagina > 0 && resultado.content.length === 0) {
          this.cargarUsuarios(pagina - 1);
          return;
        }
        this.usuarios.set(resultado.content);
        this.paginaActual.set(resultado.number);
        this.totalPaginas.set(resultado.totalPages);
        this.totalUsuarios.set(resultado.totalElements);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para administrar usuarios.'
            : error.status === 403
              ? 'Tu cuenta no tiene permiso para administrar usuarios.'
              : 'No se pudieron cargar los usuarios. Intentá de nuevo.'
        );
      },
    });
  }
}
