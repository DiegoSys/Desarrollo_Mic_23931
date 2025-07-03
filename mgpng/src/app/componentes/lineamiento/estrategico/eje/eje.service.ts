import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EjeModel, EjeUpdateModel } from './models/eje.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EjeService {
  private apiUrl = `${environment.appApiUrl}/eje`;

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

  getById(codigo: string): Observable<EjeModel> {
    return this.http.get<EjeModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: EjeModel): Observable<EjeModel> {
    return this.http.post<EjeModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: EjeUpdateModel): Observable<EjeModel> {
    return this.http.put<EjeModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
