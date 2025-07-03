import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProdInsModel, ProdInsUpdateModel } from './models/prod-inst.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProdInsService {
private apiUrl = `${environment.appApiUrl}/prodint`;
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

  getById(codigo: string): Observable<ProdInsModel> {
    return this.http.get<ProdInsModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: ProdInsModel): Observable<ProdInsModel> {
    return this.http.post<ProdInsModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: ProdInsUpdateModel): Observable<ProdInsModel> {
    return this.http.put<ProdInsModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
