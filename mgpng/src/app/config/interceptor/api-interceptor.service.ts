import { MensajeService } from './../utils/mensaje.service';
import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../service/auth.service';
import { OAuthService } from 'angular-oauth2-oidc';

@Injectable({
    providedIn: 'root',
})
export class ApiInterceptorService implements HttpInterceptor {
    constructor(
        private mensajeService: MensajeService,
        private authService: AuthService,
        private oAuthService: OAuthService
    ) {}

    intercept(
        req: HttpRequest<any>,
        next: HttpHandler
    ): Observable<HttpEvent<any>> {
        let token = this.oAuthService.getAccessToken();
        if (req.headers.get('skip')) {
            return next.handle(req);
        }
        if (token !== '') {
            const authReq = req.clone({
                headers: req.headers.set('Authorization', 'Bearer ' + token),
            });
            return next.handle(authReq).pipe(
                catchError((error) => {
                    if (error.status === 400) {
                        this.mensajeService.alertasSweet2Simple(
                            'error',
                            'SOLICITUD INCORRECTA',
                            error.error.message
                        );
                    } else if (error.status === 401) {
                        this.mensajeService.alertasSweetKeyRecoverConfirmationReturnLogin(
                            'error',
                            ' NO AUTORIZADO',
                            'El usuario no tiene acceso al sistema.'
                        );
                    } else if (error.status === 403) {
                        this.mensajeService.alertasSweet2Simple(
                            'error',
                            'SOLICITUD PROHIBIDA',
                            error.error.message
                        );
                    } else if (error.status === 404) {
                        console.log(error);
                        this.mensajeService.alertasSweet2Simple(
                            'warning',
                            'NO ENCONTRADO',
                            error.error.message
                        );
                    } else if (error.status === 409) {
                        this.mensajeService.alertasSweet2Simple(
                            'error',
                            'CONFLICTO',
                            error.error.message
                        );
                    } else if (error.status === 500) {
                        this.mensajeService.alertasSweet2Simple(
                            'error',
                            'ERROR INTERNO DEL SERVIDOR',
                            error.error.message
                        );
                    }
                    return throwError(error);
                })
            );
        }
        return next.handle(req);
    }
}
