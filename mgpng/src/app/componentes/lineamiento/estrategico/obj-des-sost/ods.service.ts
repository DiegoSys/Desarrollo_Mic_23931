import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OdsModel, OdsUpdateModel } from './models/obj-des-sost.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OdsService {
  private apiUrl = `${environment.appApiUrl}/objdessost`;

  constructor(private http: HttpClient) {}

  getAll(params?: { 
    page?: number; 
    size?: number; 
    sort?: string; 
    direction?: string; 
    searchCriteria?: { [key: string]: string } 
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';
  
    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }
  
    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getById(codigo: string): Observable<OdsModel> {
    return this.http.get<OdsModel>(`${this.apiUrl}/${codigo}`);
  }

  create(ods: OdsModel): Observable<OdsModel> {
    return this.http.post<OdsModel>(`${this.apiUrl}/add`, ods);
  }

  update(codigo: string, ods: OdsUpdateModel): Observable<OdsModel> {
    return this.http.put<OdsModel>(`${this.apiUrl}/update/${codigo}`, ods);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
