import { Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, of, switchMap } from 'rxjs';
import { Auth } from '../../core/services/auth';
import { Permisos } from '../../core/services/permisos';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private auth = inject(Auth);
  private permisosService = inject(Permisos);
  protected readonly sesionIniciada = computed(() => this.auth.token() !== null);
  protected readonly menuAbierto = signal(false);

  // Se recargan cada vez que cambia el token (login, logout, cambio de email en el perfil)
  private readonly permisos = toSignal(
    toObservable(this.auth.token).pipe(
      switchMap((token) =>
        token ? this.permisosService.cargar().pipe(catchError(() => of<string[]>([]))) : of([])
      )
    ),
    { initialValue: [] as string[] }
  );

  // Cada link se muestra solo si el usuario tiene el permiso que pide la ruta
  protected readonly puedeAdministrarLibros = computed(() => this.permisos().includes('ELIMINAR_LIBRO'));
  protected readonly puedeAgregarAutores = computed(() => this.permisos().includes('AGREGAR_AUTOR'));
  protected readonly puedeAdministrarUsuarios = computed(() =>
    this.permisos().includes('ELIMINAR_USUARIOS')
  );

  salir() {
    this.auth.logout();
  }

  alternarMenu(){
    this.menuAbierto.update(valorActual => !valorActual);
  }
}
