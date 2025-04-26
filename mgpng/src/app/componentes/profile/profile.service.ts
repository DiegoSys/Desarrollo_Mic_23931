import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { environment } from 'src/environments/environment';
import { GlobalUserApi } from './interfaces/GlobalUserApi.interface';
import { GlobalUser } from 'src/app/config/user.types';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {


  private readonly URL = environment.userInfomation;

  

  constructor(private http: HttpClient) { }

  dataSignal = signal<GlobalUserApi | null>(null);

  getUserByUsername() {

    this.http.get<GlobalUserApi>(this.URL)
      .subscribe((data) => {
        this.dataSignal.set(data); 
      });
  }


}
