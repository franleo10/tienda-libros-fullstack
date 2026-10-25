import { Component, computed, ElementRef, HostListener, inject, signal } from '@angular/core';
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
  private elemento = inject<ElementRef<HTMLElement>>(ElementRef);
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
  // El desplegable Admin aparece si el usuario tiene al menos una de las opciones
  protected readonly esAdmin = computed(
    () =>
      this.puedeAdministrarLibros() || this.puedeAgregarAutores() || this.puedeAdministrarUsuarios()
  );
  protected readonly adminAbierto = signal(false);

  salir() {
    this.cerrarMenus();
    this.auth.logout();
  }

  alternarMenu(){
    this.menuAbierto.update(valorActual => !valorActual);
  }

  alternarAdmin() {
    this.adminAbierto.update((abierto) => !abierto);
  }

  cerrarMenus() {
    this.adminAbierto.set(false);
    this.menuAbierto.set(false);
  }

  // Cierra el desplegable al hacer clic fuera de él o al apretar Escape
  @HostListener('document:click', ['$event'])
  alHacerClicAfuera(evento: MouseEvent) {
    if (!this.elemento.nativeElement.querySelector('.admin')?.contains(evento.target as Node)) {
      this.adminAbierto.set(false);
    }
  }

  @HostListener('document:keydown.escape')
  alApretarEscape() {
    this.adminAbierto.set(false);
  }
}
