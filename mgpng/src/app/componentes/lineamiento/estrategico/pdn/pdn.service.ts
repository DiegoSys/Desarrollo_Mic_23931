import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PdnModel, PdnUpdateModel } from './models/pdn.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PdnService {
private apiUrl = `${environment.appApiUrl}/pdn`;
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
  
  getById(codigo: string): Observable<PdnModel> {
    return this.http.get<PdnModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: PdnModel): Observable<PdnModel> {
    return this.http.post<PdnModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: PdnUpdateModel): Observable<PdnModel> {
    return this.http.put<PdnModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
