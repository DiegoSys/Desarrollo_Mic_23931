import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import { AuthService } from '../service/auth.service';

@Injectable({
    providedIn: 'root',
})
export class MensajeService {
    constructor(private router: Router, private authService: AuthService) {}

    /**
     * Mensaje simple
     * @param icono Icono de la alerta: success,error,warning,info,question
     * @param titulo Titulo de la alerta
     * @param texto Texto a mostrar al cliente.
     */
    alertasSweet2Simple(icono: any, titulo: any, texto: any) {
        Swal.fire({
            position: 'center',
            icon: icono,
            title: titulo,
            text: texto,
            showConfirmButton: false,
            timer: 4000,
        });
    }

    /**
     * Mensaje cuando no tiene permisos de acceso, redirige al login
     * @param icono  Icono de la alerta: success,error,warning,info,question
     * @param titulo Titulo de la alerta
     * @param texto Texto a mostrar al cliente.
     */
    alertasSweetKeyRecoverConfirmationReturnLogin(
        icono: any,
        titulo: any,
        texto: any
    ) {
        Swal.fire({
            position: 'center',
            icon: icono,
            title: titulo,
            text: texto,
            showConfirmButton: true,
            allowOutsideClick: false,
            confirmButtonText: 'Listo',
            confirmButtonColor: '#0063a9',
        }).then(() => {
            this.router.navigate(['/login']);
            this.authService.logout();
        });
    }

    alertasSweetAutoCloseReturnLogin(icono: any, titulo: any, texto: any) {
        let timerInterval;
        Swal.fire({
            title: titulo,
            text: texto,
            html: 'Cerrando <b></b> milisegundos.',
            timer: 500,
            timerProgressBar: true,
            didOpen: () => {
                Swal.showLoading();
                const b = Swal.getHtmlContainer().querySelector('b');
                timerInterval = setInterval(() => {
                    b.textContent = Swal.getTimerLeft() + '';
                }, 100);
            },
            willClose: () => {
                clearInterval(timerInterval);
            },
        }).then((_) => {
            this.router.navigate(['/login']);
            this.authService.logout();
        });
    }

    /**
     *Mensaje con doble confirmación para eliminar o habilitar y deshabilitar registros
     * @param titulo
     * @param texto
     * @returns
     */
    alertasSweetConfirmacionRegistros(titulo: any, texto: any): any {
        let valor = Swal.fire({
            // retornando lo que retorne la función Swal.fire(); (retorna una Promise)
            title: titulo,
            text: texto,
            icon: 'warning',
            showCancelButton: true,
            cancelButtonColor: 'red',
            confirmButtonColor: '#3e6f41',
            confirmButtonText: 'Si',
            showLoaderOnConfirm: true,
            preConfirm: (isConfirm) => {
                // Si el usuario dió click en 'sí', se retornará true.
                return isConfirm;
            },
        }).then((response) => {
            Swal.close();
            // En caso de haberse clickeado cancel, retornará 'undefined' que es lo mismo que false en cuanto a interpretación booleana.
            return response.value;
        });
        return valor;
    }
}
