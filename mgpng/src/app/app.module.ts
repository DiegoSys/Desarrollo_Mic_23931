import { NgModule} from '@angular/core';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AppLayoutModule } from './layout/app.layout.module';
import { OAuthModule, OAuthService } from 'angular-oauth2-oidc';
import { FormsModule } from '@angular/forms';
import { ApiInterceptorService } from './config/interceptor/api-interceptor.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
@NgModule({
    declarations: [AppComponent],
    imports: [
        AppRoutingModule,
        AppLayoutModule,
        OAuthModule.forRoot(),
        FormsModule,
    ],
    providers: [
        { provide: LocationStrategy, useClass: HashLocationStrategy },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ApiInterceptorService,
            multi: true,
        },
        OAuthService,
    ],
    bootstrap: [AppComponent],
})
export class AppModule {}
