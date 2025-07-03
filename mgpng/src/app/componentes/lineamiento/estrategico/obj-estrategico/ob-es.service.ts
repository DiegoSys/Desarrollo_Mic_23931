import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ObEstraModel, ObEstraUpdateModel } from './models/ob-es.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ObEstraService {
private apiUrl = `${environment.appApiUrl}/objestrategico`;
  constructor(private http: HttpClient) {}

  getAll(params?: { 
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    totalRecords?: number;
    searchCriteria?: { [key: string]: string }
  }): Observable<any> {
    const queryParams: any = {};
  
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';
  
    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }
  
    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }
  
    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getById(codigo: string): Observable<ObEstraModel> {
    return this.http.get<ObEstraModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: ObEstraModel): Observable<ObEstraModel> {
    return this.http.post<ObEstraModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: ObEstraUpdateModel): Observable<ObEstraModel> {
    return this.http.put<ObEstraModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
