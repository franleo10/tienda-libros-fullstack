import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { NotFound } from './pages/not-found/not-found';
import { Register } from './features/auth/register/register';
import { LibroDetalle } from './features/catalogo/libro-detalle/libro-detalle';
import { LibroListado } from './features/catalogo/libro-listado/libro-listado';
import { LibroTabla } from './features/admin-libros/libro-tabla/libro-tabla';
import { LibroFormulario } from './features/admin-libros/libro-formulario/libro-formulario';
import { Checkout } from './features/checkout/checkout/checkout';
import { FacturaListado } from './features/facturas/factura-listado/factura-listado';
import { FacturaDetalle } from './features/facturas/factura-detalle/factura-detalle';
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

export const routes: Routes = [
    {path:'', redirectTo: 'login', pathMatch:'full'},
    {path: 'login', component: Login},
    {path: 'registro', component: Register},
    {path: 'libros/:id', component: LibroDetalle},
    {path:'libros', component: LibroListado},
    {path:'admin/libros', component: LibroTabla},
    {path:'admin/libros/nuevo', component: LibroFormulario},
    {path:'admin/libros/:id/editar', component: LibroFormulario},
    {path:'checkout', component: Checkout},
    {path:'facturas', component: FacturaListado},
    {path:'facturas/:id', component: FacturaDetalle},
    {path:'autores', component: AutorListado},
    {path:'autores/:id', component: AutorDetalle},
    {path:'admin/autores/nuevo', component: AutorFormulario},
    {path:'admin/autores/:id/editar', component: AutorFormulario},
    {path:'carrito', component: Carrito},
    {path:'compra-confirmada', component: CompraConfirmada},
    {path:'compras', component: HistorialCompras},
    {path:'compras/:id', component: CompraDetalle},
    {path:'mi-biblioteca', component: MiBiblioteca},
    {path:'perfil', component: Perfil},
    {path:'perfil/editar', component: PerfilEditar},
    {path:'perfil/cambiar-password', component: CambiarPassword},
    {path:'admin/usuarios', component: UsuarioTabla},
    {path: '**', component: NotFound}
    
];
