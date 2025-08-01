import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MetaModel, MetaUpdateModel } from './models/meta.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MetaService {
private apiUrl = `${environment.appApiUrl}/meta`;
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

  getById(codigo: string): Observable<MetaModel> {
    return this.http.get<MetaModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: MetaModel): Observable<MetaModel> {
    return this.http.post<MetaModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: MetaUpdateModel): Observable<MetaModel> {
    return this.http.put<MetaModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
