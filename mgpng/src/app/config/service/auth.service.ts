import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from 'src/app/config/oauth.config';
import { TokenClaim } from 'src/app/config/token-claim.types';
import { GlobalUserService } from './global-user-.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { GlobalUser } from 'src/app/config/user.types';
import { GlobalUserApi } from 'src/app/componentes/profile/interfaces/GlobalUserApi.interface';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private globalUserSubject: BehaviorSubject<GlobalUserApi> =
        new BehaviorSubject<GlobalUserApi>(null);
    globalUser$: Observable<GlobalUserApi> = this.globalUserSubject.asObservable();

    constructor(
        private oAuthService: OAuthService,
        private globalUserService: GlobalUserService
    ) {
        this.configureOauthService();
    }

    get tokenClaims(): TokenClaim {
        return {
            familyName: this.oAuthService?.getIdentityClaims()?.['family_name'],
            email: this.oAuthService?.getIdentityClaims()?.['email'],
            givenName: this.oAuthService?.getIdentityClaims()?.['given_name'],
            name: this.oAuthService?.getIdentityClaims()?.['name'],
            username: this.oAuthService?.getIdentityClaims()?.['sub'],
            preferredUsername:
                this.oAuthService?.getIdentityClaims()?.['preferred_username'],
        };
    }

    get getId(): String {
        return this.oAuthService?.getIdentityClaims()?.['employee_number'];
    }

    get username(): string {
        return this.oAuthService.getIdentityClaims()['sub'];
    }

    login() {
        if (!this.oAuthService.hasValidAccessToken()) {
            this.oAuthService.initImplicitFlow();
            return;
        }

        if (this.oAuthService.getIdentityClaims()) {
            this.getGlobalUser();
        }
    }

    logout() {
        this.oAuthService.logOut();
    }

    private configureOauthService() {
        this.oAuthService.configure(authConfig);
        this.oAuthService.tryLogin({
            onTokenReceived: () => {
                this.getGlobalUser();
            },
        });
    }

    private getGlobalUser() {
            this.globalUserService.getUserByUsername()
                .subscribe((user) => {
                    console.log(user);
                    this.setGlobalUser(user);
                });
    
    }

    private setGlobalUser(user: GlobalUserApi) {
        //  user.pidm = 8014; //coordinador
        this.globalUserSubject.next(user);
    }
}
