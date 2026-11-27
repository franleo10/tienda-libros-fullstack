import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { NotFound } from './pages/not-found/not-found';
import { Register } from './features/auth/register/register';
import { LibroDetalle } from './features/catalogo/libro-detalle/libro-detalle';
import { LibroListado } from './features/catalogo/libro-listado/libro-listado';
import { LibroTabla } from './features/admin-libros/libro-tabla/libro-tabla';
import { LibroFormulario } from './features/admin-libros/libro-formulario/libro-formulario';
import { Checkout } from './features/checkout/checkout/checkout';
import { AutorListado } from './features/autores/autor-listado/autor-listado';
import { AutorDetalle } from './features/autores/autor-detalle/autor-detalle';
import { AutorFormulario } from './features/autores/autor-formulario/autor-formulario';
import { Carrito } from './features/carrito/carrito/carrito';
import { CompraConfirmada } from './features/compras/compra-confirmada/compra-confirmada';
import { HistorialCompras } from './features/compras/historial-compras/historial-compras';
import { CompraDetalle } from './features/compras/compra-detalle/compra-detalle';
import { MiBiblioteca } from './features/biblioteca/mi-biblioteca/mi-biblioteca';
import { Perfil } from './features/perfil/perfil/perfil';
import { PerfilEditar } from './features/perfil/perfil-editar/perfil-editar';
import { CambiarPassword } from './features/perfil/cambiar-password/cambiar-password';
import { UsuarioTabla } from './features/admin-usuarios/usuario-tabla/usuario-tabla';
import { permisoGuard } from './core/guards/permiso.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'registro', component: Register },
  { path: 'libros/:id', component: LibroDetalle, canActivate: [authGuard] },
  { path: 'libros', component: LibroListado, canActivate: [authGuard] },
  {
    path: 'admin/libros/nuevo',
    component: LibroFormulario,
    canActivate: [permisoGuard],
    data: { permiso: 'AGREGAR_LIBRO' },
  },
  {
    path: 'admin/libros/:id/editar',
    component: LibroFormulario,
    canActivate: [permisoGuard],
    data: { permiso: 'MODIFICAR_LIBRO' },
  },
  { path: 'autores', component: AutorListado, canActivate: [authGuard] },
  {
    path: 'autores/nuevo',
    component: AutorFormulario,
    canActivate: [permisoGuard],
    data: { permiso: 'AGREGAR_AUTOR' },
  },
  {
    path: 'autores/:id/editar',
    component: AutorFormulario,
    canActivate: [permisoGuard],
    data: { permiso: 'MODIFICAR_AUTOR' },
  },
  { path: 'autores/:id', component: AutorDetalle, canActivate: [authGuard] },
  {
    path: 'admin/autores/nuevo',
    redirectTo: 'autores/nuevo',
    pathMatch: 'full',
  },
  {
    path: 'admin/autores/:id/editar',
    redirectTo: 'autores/:id/editar',
    pathMatch: 'full',
  },
  {
    path: 'admin/libros',
    component: LibroTabla,
    canActivate: [permisoGuard],
    data: { permiso: 'ELIMINAR_LIBRO' },
  },
  { path: 'checkout', component: Checkout, canActivate: [authGuard] },
  { path: 'carrito', component: Carrito, canActivate: [authGuard] },
  { path: 'compra-confirmada', component: CompraConfirmada, canActivate: [authGuard] },
  { path: 'compras', component: HistorialCompras, canActivate: [authGuard] },
  { path: 'compras/:id', component: CompraDetalle, canActivate: [authGuard] },
  { path: 'mi-biblioteca', component: MiBiblioteca, canActivate: [authGuard] },
  { path: 'perfil', component: Perfil, canActivate: [authGuard] },
  { path: 'perfil/editar', component: PerfilEditar, canActivate: [authGuard] },
  { path: 'perfil/cambiar-password', component: CambiarPassword, canActivate: [authGuard] },
  {
    path: 'admin/usuarios',
    component: UsuarioTabla,
    canActivate: [permisoGuard],
    data: { permiso: 'ELIMINAR_USUARIOS' },
  },
  { path: '**', component: NotFound }

];
