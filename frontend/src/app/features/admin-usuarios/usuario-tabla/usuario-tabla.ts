import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Auth } from '../../../core/services/auth';
import { FiltrosUsuarios, RolUsuario, UsuarioAdmin } from '../../../models/usuario';
import { Usuario as UsuarioService } from '../../../services/usuario';
import { Confirmacion } from '../../../shared/confirmacion/confirmacion';

// Acción que el admin eligió y está esperando confirmación en el modal
interface AccionPendiente {
  tipo: 'banear' | 'reactivar' | 'cambiarRol';
  usuario: UsuarioAdmin;
  rolNuevo?: RolUsuario;
}

const NOMBRE_ROL: Record<RolUsuario, string> = {
  ADMINISTRADOR: 'Administrador',
  USUARIO: 'Usuario',
};

@Component({
  selector: 'app-usuario-tabla',
  imports: [ReactiveFormsModule, Confirmacion],
  templateUrl: './usuario-tabla.html',
  styleUrl: './usuario-tabla.css',
})
export class UsuarioTabla implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  private readonly auth = inject(Auth);

  protected readonly usuarios = signal<UsuarioAdmin[]>([]);
  protected readonly paginaActual = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly totalUsuarios = signal(0);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal('');
  protected readonly mensajeAccionError = signal('');
  protected readonly mensajeResultado = signal('');
  protected readonly usuarioProcesando = signal<number | null>(null);
  protected readonly nombreRol = NOMBRE_ROL;

  // null = modal cerrado
  protected readonly accionPendiente = signal<AccionPendiente | null>(null);

  protected readonly tituloConfirmacion = computed(() => {
    switch (this.accionPendiente()?.tipo) {
      case 'banear':
        return '¿Banear usuario?';
      case 'reactivar':
        return '¿Reactivar usuario?';
      default:
        return '¿Cambiar rol?';
    }
  });

  protected readonly mensajeConfirmacion = computed(() => {
    const accion = this.accionPendiente();
    if (!accion) {
      return '';
    }
    const nombre = accion.usuario.nombre;
    switch (accion.tipo) {
      case 'banear':
        return `${nombre} no va a poder iniciar sesión hasta que lo reactives.`;
      case 'reactivar':
        return `${nombre} va a poder volver a iniciar sesión.`;
      default:
        return accion.rolNuevo === 'ADMINISTRADOR'
          ? `${nombre} pasará a ser Administrador y va a tener acceso a todo el panel de administración.`
          : `${nombre} pasará a ser Usuario y perderá el acceso al panel de administración.`;
    }
  });

  protected readonly textoConfirmar = computed(() => {
    const accion = this.accionPendiente();
    if (accion?.tipo === 'banear') return 'Banear';
    if (accion?.tipo === 'reactivar') return 'Reactivar';
    return accion?.rolNuevo ? `Hacer ${NOMBRE_ROL[accion.rolNuevo]}` : 'Cambiar rol';
  });

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

  // El admin logueado no puede banearse ni cambiarse el rol (el backend también lo impide)
  protected esUsuarioActual(usuario: UsuarioAdmin) {
    return usuario.email === this.auth.usuario();
  }

  protected banear(usuario: UsuarioAdmin) {
    this.accionPendiente.set({ tipo: 'banear', usuario });
  }

  protected reactivar(usuario: UsuarioAdmin) {
    this.accionPendiente.set({ tipo: 'reactivar', usuario });
  }

  protected elegirRol(usuario: UsuarioAdmin, evento: Event) {
    const selector = evento.target as HTMLSelectElement;
    const rolNuevo = selector.value as RolUsuario;
    // El selector sigue mostrando el rol actual hasta que se confirme y se recargue la tabla
    selector.value = usuario.rol ?? '';
    if (rolNuevo !== usuario.rol) {
      this.accionPendiente.set({ tipo: 'cambiarRol', usuario, rolNuevo });
    }
  }

  protected cancelarAccion() {
    this.accionPendiente.set(null);
  }

  protected confirmarAccion() {
    const accion = this.accionPendiente();
    if (!accion) {
      return;
    }
    const { usuario } = accion;

    let solicitud;
    let mensajeExito: string;
    if (accion.tipo === 'banear') {
      solicitud = this.usuarioService.darDeBaja(usuario.id);
      mensajeExito = `${usuario.nombre} fue baneado.`;
    } else if (accion.tipo === 'reactivar') {
      solicitud = this.usuarioService.darDeAlta(usuario.id);
      mensajeExito = `${usuario.nombre} volvió a estar activo.`;
    } else {
      solicitud = this.usuarioService.cambiarRol(usuario.id, accion.rolNuevo!);
      mensajeExito = `${usuario.nombre} ahora es ${NOMBRE_ROL[accion.rolNuevo!]}.`;
    }

    this.usuarioProcesando.set(usuario.id);
    this.mensajeAccionError.set('');
    this.mensajeResultado.set('');

    solicitud.subscribe({
      next: () => {
        this.usuarioProcesando.set(null);
        this.accionPendiente.set(null);
        this.mensajeResultado.set(mensajeExito);
        this.cargarUsuarios(this.paginaActual(), true);
      },
      error: (error: HttpErrorResponse) => {
        this.usuarioProcesando.set(null);
        this.accionPendiente.set(null);
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
