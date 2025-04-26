import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GlobalUser } from 'src/app/config/user.types';
import { environment } from '../../../environments/environment';
import { map } from 'rxjs/operators';
import { GlobalUserApi } from 'src/app/componentes/profile/interfaces/GlobalUserApi.interface';

@Injectable({
    providedIn: 'root',
})
export class GlobalUserService {
    private readonly URL = environment.userInfomation;
    constructor(private http: HttpClient) {}

    getUserByUsername(): Observable<GlobalUserApi> {
        return this.http
            .get<GlobalUserApi>(this.URL, {
               //headers: { skip: 'true' },
            })
            .pipe(
                map((users: GlobalUserApi) => {
                    localStorage.setItem('id', users.ID.toString());
                    if (users != null) {
                        return users;
                    }
                    return null;
                })
            );
    }
}
