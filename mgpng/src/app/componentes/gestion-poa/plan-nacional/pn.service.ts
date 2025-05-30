import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PnModel, PnUpdateModel } from './models/pn.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PnService {
  private apiUrl = `${environment.appApiUrl}/plannacional`;

  constructor(private http: HttpClient) {}

  getAll(params?: { 
    page?: number;
    size?: number;
    sort?: string;
    order?: string;
    totalRecords?: number
  }): Observable<any> {
    const queryParams: any = {};
    
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    
    if (params?.sort) {
      queryParams.sort = params.sort;
      queryParams.order = params.order || 'asc';
    }

    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }

    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getById(codigo: string): Observable<PnModel> {
    return this.http.get<PnModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: PnModel): Observable<PnModel> {
    return this.http.post<PnModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: PnUpdateModel): Observable<PnModel> {
    return this.http.put<PnModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
